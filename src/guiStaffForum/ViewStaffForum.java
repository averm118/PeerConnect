package guiStaffForum;

import java.util.List;

import applicationMain.FoundationsMain;
import database.Database;
import database.FeedbackDatabase;
import database.PostDatabase;
import database.ReplyDatabase;
import entityClasses.Post;
import entityClasses.User;
import guiCommon.ActionSpec;
import guiCommon.PeerConnectShell;
import guiCommon.ScreenSpec;
import guiCommon.UiFactory;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewStaffForum {
    private static final PseudoClass UNREAD = PseudoClass.getPseudoClass("unread");
    private static final PseudoClass FLAGGED = PseudoClass.getPseudoClass("flagged");

    private static ViewStaffForum theView;
    public static PostDatabase thePostDatabase = applicationMain.FoundationsMain.postDatabase;
    public static ReplyDatabase theReplyDatabase = applicationMain.FoundationsMain.replyDatabase;
    public static Database theDatabase = applicationMain.FoundationsMain.database;
    public static FeedbackDatabase theFeedbackDatabase = applicationMain.FoundationsMain.feedbackDatabase;
    protected static Stage theStage;
    protected static User theUser;
    public static Post thePost;
    public static Scene theForumScene = null;

    protected static Label label_PageTitle = new Label("Discussion Forum");
    protected static Label label_UserDetails = new Label();
    protected static CheckBox check_ShowUnread = new CheckBox("Unread");
    protected static CheckBox check_ShowMine = new CheckBox("Mine");
    protected static CheckBox check_ShowUnreadReplies = new CheckBox("Unread replies");
    protected static TextField text_Search = new TextField();
    protected static ListView<String> listView_Threads = new ListView<>();
    protected static ListView<String> listView_Posts = new ListView<>();
    protected static Button button_ManageThreads = UiFactory.action(
            ActionSpec.of("Manage Threads", "bi-folder2-open", ControllerStaffForum::goToManageThreads,
                    "pc-button-secondary"));
    protected static Button button_CreatePost = UiFactory.action(
            ActionSpec.of("Create Post", "bi-plus", ControllerStaffForum::performCreatePost));
    protected static Button button_Return = UiFactory.action(
            ActionSpec.of("Return", "bi-arrow-left", ControllerStaffForum::performReturn, "pc-button-secondary"));
    protected static Button button_Quit = UiFactory.action(
            ActionSpec.of("Quit", "bi-x", ControllerStaffForum::performQuit, "pc-button-secondary"));

    public static void displayStaffForum(Stage ps, User user) {
        theStage = ps;
        theUser = user;
        text_Search.clear();

        if (theView == null) {
            theView = new ViewStaffForum();
        }

        populateThreadList();
        populatePostList("ALL");
        PeerConnectShell.show(theStage, theForumScene, "PeerConnect: Staff Forum");
    }

    public ViewStaffForum() {
        label_PageTitle.getStyleClass().add("pc-heading");
        label_UserDetails.getStyleClass().add("pc-body");
        UiFactory.prepareInput(text_Search, "Search titles...");
        UiFactory.prepareList(listView_Threads, "Discussion threads");
        UiFactory.prepareList(listView_Posts, "Posts");
        listView_Threads.getStyleClass().add("pc-thread-list");

        check_ShowUnread.setOnAction(_ -> refreshFilteredPosts());
        check_ShowMine.setOnAction(_ -> refreshFilteredPosts());
        check_ShowUnreadReplies.setOnAction(_ -> refreshFilteredPosts());
        text_Search.textProperty().addListener((_, _, _) -> refreshFilteredPosts());

        listView_Threads.setOnMouseClicked(_ -> {
            String selected = listView_Threads.getSelectionModel().getSelectedItem();
            if (selected != null) {
                populatePostList(selected);
            }
        });

        listView_Posts.setCellFactory(_ -> new PostCell());
        listView_Posts.setOnMouseClicked(_ -> openSelectedPost());

        VBox threadPanel = UiFactory.card(UiFactory.section("Threads", listView_Threads, button_ManageThreads));
        VBox.setVgrow(listView_Threads, Priority.ALWAYS);

        HBox filterBar = new HBox(12, text_Search, check_ShowUnread, check_ShowMine, check_ShowUnreadReplies);
        filterBar.getStyleClass().add("pc-filter-bar");
        HBox.setHgrow(text_Search, Priority.ALWAYS);

        VBox postPanel = UiFactory.card(UiFactory.section("Posts", filterBar, listView_Posts));
        VBox.setVgrow(listView_Posts, Priority.ALWAYS);

        SplitPane split = new SplitPane(threadPanel, postPanel);
        split.setDividerPositions(0.23);
        VBox.setVgrow(split, Priority.ALWAYS);

        HBox footer = UiFactory.actions(button_CreatePost, UiFactory.spacer(), button_Return, button_Quit);
        VBox screen = new VBox(18, UiFactory.card(label_PageTitle, label_UserDetails), split, footer);
        screen.getStyleClass().add("pc-screen");
        screen.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(split, Priority.ALWAYS);

        theForumScene = PeerConnectShell.scene(
                ScreenSpec.of("Staff Forum", "Moderate posts, manage threads, and keep feedback visible.",
                        theUser, "Staff", "bi-chat-square-text"),
                screen);
    }

    public static void populateThreadList() {
        List<String> threads = thePostDatabase.getThreadList();
        listView_Threads.setItems(FXCollections.observableArrayList(threads));
        label_UserDetails.setText("Logged in as " + theUser.getUserName());
    }

    public static void populatePostList(String threadName) {
        if (threadName == null) {
            threadName = "ALL";
        }
        List<String> posts = thePostDatabase.getPostTitlesByThread(threadName, theUser.getUserName());
        String search = text_Search.getText().trim().toLowerCase();

        List<String> filtered = posts.stream()
                .filter(item -> {
                    int postId = parsePostId(item);
                    Post p = thePostDatabase.getPostById(postId);
                    if (p == null) {
                        return false;
                    }
                    if (check_ShowUnread.isSelected()) {
                        boolean unread = thePostDatabase.isPostUnread(theUser.getUserName(), postId);
                        if (p.getAuthor().equals(theUser.getUserName())) {
                            unread = false;
                        }
                        if (!unread) {
                            return false;
                        }
                    }
                    if (check_ShowMine.isSelected() && !p.getAuthor().equals(theUser.getUserName())) {
                        return false;
                    }
                    if (check_ShowUnreadReplies.isSelected()
                            && theReplyDatabase.getUnreadReplyCount(theUser.getUserName(), postId) == 0) {
                        return false;
                    }
                    return search.isEmpty() || p.getTitle().toLowerCase().contains(search);
                })
                .toList();

        listView_Posts.setItems(FXCollections.observableArrayList(filtered));
    }

    private static void refreshFilteredPosts() {
        String currentThread = listView_Threads.getSelectionModel().getSelectedItem();
        populatePostList(currentThread);
    }

    private static void openSelectedPost() {
        String selected = listView_Posts.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        int id = parsePostId(selected);
        Post selectedPost = thePostDatabase.getPostById(id);
        if (selectedPost == null) {
            return;
        }
        if (selectedPost.getInappropriate()
                && !theUser.getNewStaff()
                && !theUser.getUserName().equals(selectedPost.getAuthor())) {
            return;
        }

        String currentThread = listView_Threads.getSelectionModel().getSelectedItem();
        populatePostList(currentThread);
        guiPost.ViewPost.displayPost(ViewStaffForum.theStage, ViewStaffForum.theUser, selectedPost);
    }

    private static int parsePostId(String item) {
        String[] lines = item.split("\n");
        String second = lines.length > 1 ? lines[1] : item;
        int start = second.indexOf('[') + 1;
        int end = second.indexOf(']');
        return Integer.parseInt(second.substring(start, end));
    }

    private static class PostCell extends ListCell<String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                pseudoClassStateChanged(UNREAD, false);
                pseudoClassStateChanged(FLAGGED, false);
                return;
            }

            int postId = parsePostId(item);
            Post p = thePostDatabase.getPostById(postId);
            if (p == null) {
                setText(item);
                return;
            }

            boolean hiddenFlagged = p.getInappropriate()
                    && !theUser.getNewStaff()
                    && !theUser.getUserName().equals(p.getAuthor());
            boolean unread = thePostDatabase.isPostUnread(theUser.getUserName(), postId);
            if (p.getAuthor().equals(theUser.getUserName())) {
                unread = false;
            }
            int unreadFeedbackCount = theFeedbackDatabase.getUnreadFeedbackCount(theUser.getUserName(), "POST", postId);
            int unreadReplyCount = theReplyDatabase.getUnreadReplyCount(theUser.getUserName(), postId);

            Label title = new Label(hiddenFlagged
                    ? "Flagged post hidden"
                    : (p.getInappropriate() ? "[FLAGGED] " : "") + p.getTitle());
            title.getStyleClass().add("pc-post-cell-title");
            if (hiddenFlagged) {
                title.getStyleClass().add("pc-post-cell-hidden");
            }

            Label meta = new Label("by " + p.getAuthor() + " in " + p.getThread() + " | Post #" + postId);
            meta.getStyleClass().add("pc-post-cell-meta");

            HBox badges = new HBox(6);
            if (unread) {
                badges.getChildren().add(UiFactory.badge("Unread", "pc-badge-unread"));
            }
            if (unreadReplyCount > 0) {
                badges.getChildren().add(UiFactory.badge(unreadReplyCount + " replies", "pc-badge-unread"));
            }
            if (unreadFeedbackCount > 0) {
                badges.getChildren().add(UiFactory.badge(unreadFeedbackCount + " feedback", "pc-badge-unread"));
            }
            if (p.getInappropriate()) {
                badges.getChildren().add(UiFactory.badge("Flagged", "pc-badge-flagged"));
            }

            VBox cell = new VBox(6, title, meta, badges);
            cell.setFillWidth(true);
            setText(null);
            setGraphic(cell);
            pseudoClassStateChanged(UNREAD, unread || unreadFeedbackCount > 0 || unreadReplyCount > 0);
            pseudoClassStateChanged(FLAGGED, p.getInappropriate());
        }
    }
}
