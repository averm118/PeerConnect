package guiPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.FeedbackDatabase;
import database.PostDatabase;
import database.ReplyDatabase;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;
import guiCommon.ActionSpec;
import guiCommon.PeerConnectShell;
import guiCommon.ScreenSpec;
import guiCommon.UiFactory;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewPost {
    private static ViewPost theView;

    protected static Stage theStage;
    protected static User theUser;
    public static Post thePost;
    public static PostDatabase thePostDatabase = applicationMain.FoundationsMain.postDatabase;
    public static ReplyDatabase theReplyDatabase = applicationMain.FoundationsMain.replyDatabase;
    public static FeedbackDatabase theFeedbackDatabase = applicationMain.FoundationsMain.feedbackDatabase;
    public static Scene theScene;

    protected static VBox threadBox = new VBox(14);
    protected static ListView<Reply> listView_Replies = new ListView<>();
    protected static CheckBox check_ShowUnread = new CheckBox("Unread replies only");

    protected static Button button_ReplyToPost = UiFactory.action(
            ActionSpec.of("Reply", "bi-reply", ControllerPost::performSubmitReply));
    protected static Button button_Edit = UiFactory.action(
            ActionSpec.of("Edit Post", "bi-pencil-square", ControllerPost::performEdit, "pc-button-secondary"));
    protected static Button button_Delete = UiFactory.action(
            ActionSpec.of("Delete Post", "bi-trash", ViewPost::confirmDeletePost, "pc-button-danger"));
    protected static Button button_LeaveFeedback = UiFactory.action(
            ActionSpec.of("Leave Feedback", "bi-chat-left-quote", ControllerPost::performLeavePostFeedback,
                    "pc-button-secondary"));
    protected static Button button_ViewFeedback = UiFactory.action(
            ActionSpec.of("View Feedback", "bi-envelope-open", ControllerPost::performViewPostFeedback,
                    "pc-button-secondary"));
    protected static Button button_MarkInappropriate = UiFactory.action(
            ActionSpec.of("Mark Inappropriate", "bi-flag", ControllerPost::performMarkPostInappropriate,
                    "pc-button-danger"));
    protected static Button button_ClearFlag = UiFactory.action(
            ActionSpec.of("Clear Flag", "bi-flag-fill", ControllerPost::performClearPostFlag,
                    "pc-button-secondary"));
    protected static Button button_Return = UiFactory.action(
            ActionSpec.of("Return", "bi-arrow-left", ControllerPost::performReturn, "pc-button-secondary"));

    private static HBox primaryActions = new HBox(10);
    private static HBox staffActions = new HBox(10);
    private static Label label_PostTitle = new Label("Post");
    private static Label label_UserDetails = new Label();

    public static void displayPost(Stage ps, User user, Post post) {
        theStage = ps;
        theUser = user;
        thePost = post;

        if (theView == null) {
            theView = new ViewPost();
        }

        populatePost();
        populateReplies();

        PeerConnectShell.show(theStage, theScene, "PeerConnect: Post");
        thePostDatabase.markPostAsRead(theUser.getUserName(), post.getId());
        theFeedbackDatabase.markFeedbackForTargetAsRead(theUser.getUserName(), "POST", post.getId());
    }

    public ViewPost() {
        label_PostTitle.getStyleClass().add("pc-heading");
        label_UserDetails.getStyleClass().add("pc-body");
        UiFactory.prepareList(listView_Replies, "Replies");

        check_ShowUnread.setOnAction(_ -> populateReplies());

        primaryActions.setAlignment(Pos.CENTER_LEFT);
        staffActions.setAlignment(Pos.CENTER_LEFT);

        VBox postContent = UiFactory.card(threadBox);
        VBox.setVgrow(listView_Replies, Priority.ALWAYS);
        VBox.setVgrow(postContent, Priority.ALWAYS);

        HBox topTools = UiFactory.actions(check_ShowUnread, UiFactory.spacer(), button_Return);
        VBox screen = new VBox(16,
                UiFactory.card(label_PostTitle, label_UserDetails, topTools),
                postContent,
                primaryActions,
                staffActions);
        screen.getStyleClass().add("pc-screen");
        VBox.setVgrow(postContent, Priority.ALWAYS);

        theScene = PeerConnectShell.scene(
                ScreenSpec.of("Post Detail", "Read the full thread, reply, and manage feedback.",
                        theUser, "Conversation", "bi-chat-left-text"),
                screen);
    }

    private static void confirmDeletePost() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Post");
        confirm.setHeaderText("Delete this post?");
        confirm.setContentText("This action will remove or hide the post depending on your role.");
        confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                ControllerPost.performDelete();
            }
        });
    }

    private static VBox buildPostBlock() {
        VBox box = new VBox(8);
        box.getStyleClass().add("pc-section");

        String postTitleText = thePost.getTitle();
        String postBodyText = thePost.getBody();
        boolean hiddenFlagged = thePost.getInappropriate()
                && !theUser.getNewStaff()
                && !theUser.getUserName().equals(thePost.getAuthor());

        if (hiddenFlagged) {
            postTitleText = "Flagged post hidden";
            postBodyText = "This post has been flagged as inappropriate and is hidden.";
        }

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label((thePost.getInappropriate() && !hiddenFlagged ? "[FLAGGED] " : "") + postTitleText);
        title.getStyleClass().add("pc-post-cell-title");
        title.setWrapText(true);
        HBox.setHgrow(title, Priority.ALWAYS);
        titleRow.getChildren().add(title);
        if (thePost.getInappropriate()) {
            titleRow.getChildren().add(UiFactory.badge("Flagged", "pc-badge-flagged"));
        }

        Label author = new Label("Posted by " + thePost.getAuthor() + " in " + thePost.getThread()
                + " | Post #" + thePost.getId());
        author.getStyleClass().add("pc-post-cell-meta");

        Label body = new Label(postBodyText);
        body.getStyleClass().add(hiddenFlagged ? "pc-post-cell-hidden" : "pc-body");
        body.setWrapText(true);

        box.getChildren().addAll(titleRow, author, body);
        return box;
    }

    public static void populatePost() {
        threadBox.getChildren().clear();
        primaryActions.getChildren().clear();
        staffActions.getChildren().clear();

        label_PostTitle.setText(thePost.getTitle());
        label_UserDetails.setText("Viewing as " + theUser.getUserName());

        int unreadFeedbackCount = theFeedbackDatabase.getUnreadFeedbackCount(
                theUser.getUserName(), "POST", thePost.getId());
        button_ViewFeedback.setText(unreadFeedbackCount > 0
                ? "View Feedback (" + unreadFeedbackCount + " unread)"
                : "View Feedback");

        threadBox.getChildren().add(buildPostBlock());
        threadBox.getChildren().add(new Separator());
        threadBox.getChildren().add(listView_Replies);

        primaryActions.getChildren().add(button_ReplyToPost);

        boolean author = theUser.getUserName().equals(thePost.getAuthor());
        if (author) {
            primaryActions.getChildren().add(button_Edit);
            if (!theUser.getNewStaff()) {
                primaryActions.getChildren().add(button_Delete);
            }
        }

        if (theUser.getNewStaff()) {
            staffActions.getChildren().add(button_ViewFeedback);
            if (!author) {
                staffActions.getChildren().add(button_LeaveFeedback);
            }
            if (thePost.getInappropriate()) {
                staffActions.getChildren().addAll(button_ClearFlag, button_Delete);
            } else {
                staffActions.getChildren().add(button_MarkInappropriate);
            }
            if (author && !staffActions.getChildren().contains(button_Delete)) {
                staffActions.getChildren().add(button_Delete);
            }
        } else if (theFeedbackDatabase.getVisibleFeedbackCount(
                theUser.getUserName(), "POST", thePost.getId()) > 0) {
            primaryActions.getChildren().add(button_ViewFeedback);
        }
    }

    public static void populateReplies() {
        List<Reply> replies = theReplyDatabase.getReplyList(thePost.getId());
        Map<Integer, List<Reply>> children = new HashMap<>();

        for (Reply r : replies) {
            children.computeIfAbsent(r.getParent(), _ -> new ArrayList<>()).add(r);
        }

        List<Reply> ordered = new ArrayList<>();
        buildReplyTree(0, children, ordered, 0);

        if (check_ShowUnread.isSelected()) {
            ordered = ordered.stream()
                    .filter(r -> {
                        boolean unread = theReplyDatabase.isReplyUnread(theUser.getUserName(), r.getID());
                        if (r.getAuthor().equals(theUser.getUserName())) {
                            unread = false;
                        }
                        return unread;
                    })
                    .toList();
        }

        listView_Replies.setItems(FXCollections.observableArrayList(ordered));
        listView_Replies.setCellFactory(_ -> new ReplyCell());
    }

    public static void markAllRepliesAsRead() {
        theReplyDatabase.markRepliesAsRead(theUser.getUserName(), thePost.getId());
    }

    private static void buildReplyTree(
            int parentId,
            Map<Integer, List<Reply>> children,
            List<Reply> ordered,
            int depth) {
        if (!children.containsKey(parentId)) {
            return;
        }

        for (Reply r : children.get(parentId)) {
            r.setDepth(depth);
            ordered.add(r);
            buildReplyTree(r.getID(), children, ordered, depth + 1);
        }
    }

    private static class ReplyCell extends ListCell<Reply> {
        @Override
        protected void updateItem(Reply reply, boolean empty) {
            super.updateItem(reply, empty);

            if (empty || reply == null) {
                setGraphic(null);
                return;
            }

            boolean unread = theReplyDatabase.isReplyUnread(theUser.getUserName(), reply.getID());
            if (reply.getAuthor().equals(theUser.getUserName())) {
                unread = false;
            }

            int unreadFeedbackCount = theFeedbackDatabase.getUnreadFeedbackCount(
                    theUser.getUserName(), "REPLY", reply.getID());

            String replyBodyText = reply.getBody();
            String replyAuthorText = "Author: " + reply.getAuthor();
            boolean hiddenFlagged = reply.getInappropriate()
                    && !theUser.getNewStaff()
                    && !theUser.getUserName().equals(reply.getAuthor());

            if (hiddenFlagged) {
                replyAuthorText = "Author: " + reply.getAuthor() + " [FLAGGED]";
                replyBodyText = "This reply has been flagged as inappropriate and is hidden.";
            } else if (reply.getInappropriate()) {
                replyAuthorText = "Author: " + reply.getAuthor() + " [FLAGGED]";
            }

            Label author = new Label(replyAuthorText);
            author.getStyleClass().add("pc-post-cell-meta");

            Label body = new Label(replyBodyText);
            body.setWrapText(true);
            body.getStyleClass().add(hiddenFlagged ? "pc-post-cell-hidden" : "pc-body");

            HBox badges = new HBox(6);
            if (unread) {
                badges.getChildren().add(UiFactory.badge("Unread", "pc-badge-unread"));
            }
            if (unreadFeedbackCount > 0) {
                badges.getChildren().add(UiFactory.badge(unreadFeedbackCount + " feedback", "pc-badge-unread"));
            }
            if (reply.getInappropriate()) {
                badges.getChildren().add(UiFactory.badge("Flagged", "pc-badge-flagged"));
            }

            HBox buttons = new HBox(8);
            Button replyButton = UiFactory.action(
                    ActionSpec.of("Reply", "bi-reply", () -> ControllerPost.performReplyToReply(reply),
                            "pc-button-secondary"));
            Button editButton = UiFactory.action(
                    ActionSpec.of("Edit", "bi-pencil", () -> ControllerPost.performEditReply(reply),
                            "pc-button-secondary"));
            Button feedbackButton = UiFactory.action(
                    ActionSpec.of("Leave Feedback", "bi-chat-left-quote",
                            () -> ControllerPost.performLeaveReplyFeedback(reply), "pc-button-secondary"));
            Button viewFeedbackButton = UiFactory.action(
                    ActionSpec.of(unreadFeedbackCount > 0
                                    ? "View Feedback (" + unreadFeedbackCount + " unread)"
                                    : "View Feedback",
                            "bi-envelope-open", () -> ControllerPost.performViewReplyFeedback(reply),
                            "pc-button-secondary"));
            Button markButton = UiFactory.action(
                    ActionSpec.of("Mark", "bi-flag", () -> ControllerPost.performMarkReplyInappropriate(reply),
                            "pc-button-danger"));
            Button clearButton = UiFactory.action(
                    ActionSpec.of("Clear Flag", "bi-flag-fill", () -> ControllerPost.performClearReplyFlag(reply),
                            "pc-button-secondary"));

            if (reply.getAuthor().equals(theUser.getUserName())) {
                buttons.getChildren().add(editButton);
            }
            buttons.getChildren().add(replyButton);
            if (theUser.getNewStaff()) {
                buttons.getChildren().addAll(viewFeedbackButton, feedbackButton,
                        reply.getInappropriate() ? clearButton : markButton);
            } else if (theFeedbackDatabase.getVisibleFeedbackCount(theUser.getUserName(), "REPLY", reply.getID()) > 0) {
                buttons.getChildren().add(viewFeedbackButton);
            }

            VBox box = new VBox(8, author, body, badges, buttons);
            box.getStyleClass().add("pc-reply-cell");
            if (unread) {
                box.getStyleClass().add("pc-reply-cell-unread");
            }
            if (reply.getInappropriate()) {
                box.getStyleClass().add("pc-reply-cell-flagged");
            }
            box.setPadding(new Insets(12, 12, 12, 12 + reply.getDepth() * 24));
            setText(null);
            setGraphic(box);
        }
    }
}
