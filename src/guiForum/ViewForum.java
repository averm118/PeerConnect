package guiForum;

import java.util.List;

import applicationMain.FoundationsMain;
import database.Database;
import database.PostDatabase;
import database.ReplyDatabase;
import database.FeedbackDatabase;
import entityClasses.Post;
import entityClasses.User;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
/**
 * This class displays the forum page.
 *
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */
public class ViewForum {

	private static double width = FoundationsMain.WINDOW_WIDTH;
	private static double height = FoundationsMain.WINDOW_HEIGHT;

    private static ViewForum theView;
    /** The post database used by this page. */
    public static PostDatabase thePostDatabase = applicationMain.FoundationsMain.postDatabase;
    /** The reply database used by this page. */
    public static ReplyDatabase theReplyDatabase = applicationMain.FoundationsMain.replyDatabase;
    /** The main database used by this page. */
    public static Database theDatabase = applicationMain.FoundationsMain.database;
    /** The feedback database used by this page. */
    public static FeedbackDatabase theFeedbackDatabase = applicationMain.FoundationsMain.feedbackDatabase;
    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;
    /** The currently selected post. */
    public static Post thePost;
    /** The scene for the forum page. */
    public static Scene theForumScene = null;

    // UI elements
    protected static Label label_PageTitle = new Label("Discussion Forum");
    protected static Label label_UserDetails = new Label();
    
    protected static CheckBox check_ShowUnread = new CheckBox("Unread Only");
    protected static CheckBox check_ShowMine = new CheckBox("My Posts Only");
    protected static CheckBox check_ShowUnreadReplies = new CheckBox("Unread Replies Only");
    
    protected static TextField text_Search = new TextField();

    protected static ListView<String> listView_Threads = new ListView<>();
    protected static ListView<String> listView_Posts = new ListView<>();

    protected static Button button_CreatePost = new Button("Create Post");
    protected static Button button_Return = new Button("Return");
    protected static Button button_Quit = new Button("Quit");
    
    
    /**
     * This method displays the forum page.
     *
     * @param ps the stage used to show the page
     * @param user the current user
     */
    public static void displayForum(Stage ps, User user) {

        theStage = ps;
        theUser = user;
        
        text_Search.clear();

        if (theView == null) theView = new ViewForum();

        populateThreadList();
        populatePostList("ALL");

        theStage.setScene(theForumScene);
        theStage.show();
    }
    
    /**
     * This constructor creates the forum view.
     */
    public ViewForum() {

        theRootPane = new Pane();
        theForumScene = new Scene(theRootPane, width, height);

        //Title
        label_PageTitle.setFont(Font.font("Arial", 28));
        label_PageTitle.setMinWidth(width);
        label_PageTitle.setAlignment(Pos.CENTER);
        label_PageTitle.setLayoutX(0);
        label_PageTitle.setLayoutY(20);

        //User details
        label_UserDetails.setFont(Font.font("Arial", 18));
        label_UserDetails.setLayoutX(20);
        label_UserDetails.setLayoutY(20);
        
        //check boxes
        check_ShowUnread.setLayoutX(175);
        check_ShowUnread.setLayoutY(90);
        check_ShowUnread.setOnAction((_) -> refreshFilteredPosts());

        check_ShowMine.setLayoutX(275);
        check_ShowMine.setLayoutY(90);
        check_ShowMine.setOnAction((_) -> refreshFilteredPosts());
        
        check_ShowUnreadReplies.setLayoutX(380);
        check_ShowUnreadReplies.setLayoutY(90);
        check_ShowUnreadReplies.setOnAction((_) -> refreshFilteredPosts());
        
        //search bar
        text_Search.setPromptText("Search titles...");
        text_Search.setLayoutX(175);
        text_Search.setLayoutY(60);
        text_Search.setPrefWidth(width-225);
        text_Search.textProperty().addListener((obs, oldV, newV) -> refreshFilteredPosts());

        //Thread list
        listView_Threads.setLayoutX(20);
        listView_Threads.setLayoutY(60);
        listView_Threads.setPrefSize(125, height-140);
        listView_Threads.setOnMouseClicked((_) -> {
            String selected = listView_Threads.getSelectionModel().getSelectedItem();
            if (selected != null){
                populatePostList(selected);
            }
        });

        //Post list
        listView_Posts.setLayoutX(175);
        listView_Posts.setLayoutY(120);
        listView_Posts.setPrefSize(width-225, height-200);
        listView_Posts.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 14;");
        listView_Posts.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                String[] lines = item.split("\n");
                String first = lines[0];
                String second = lines[1];
                int start = second.indexOf('[') + 1;
                int end = second.indexOf(']');
                int postId = Integer.parseInt(second.substring(start, end));

                Post p = thePostDatabase.getPostById(postId);

                if (p.getInappropriate() && !theUser.getNewStaff() && !theUser.getUserName().equals(p.getAuthor())) {
                    first = "[FLAGGED] [This post has been flagged as inappropriate and is hidden.] — " + p.getAuthor();
                }

                setText(first + "\n" + second);
                setWrapText(true);

                boolean unread = thePostDatabase.isPostUnread(
                        theUser.getUserName(),
                        postId);
                
                int unreadFeedbackCount = theFeedbackDatabase.getUnreadFeedbackCount(
                        theUser.getUserName(),
                        "POST",
                        postId
                );

                if (thePostDatabase.getPostById(postId).getAuthor().equals(theUser.getUserName())) {
                    unread = false; //creator always sees their own post as read
                }

                if (unread || unreadFeedbackCount > 0) {
                    setStyle("-fx-font-weight: bold;");
                } else {
                    setStyle("-fx-font-weight: normal;");
                }
            }
        });
        
        //open posts on click
        listView_Posts.setOnMouseClicked((_) -> {
            String selected = listView_Posts.getSelectionModel().getSelectedItem();
            if (selected != null) {

                String[] lines = selected.split("\n");
                String second = lines[1];
                int start = second.indexOf('[') + 1;
                int end = second.indexOf(']');
                int id = Integer.parseInt(second.substring(start, end));

                Post selectedPost = thePostDatabase.getPostById(id);

                if (selectedPost.getInappropriate() && !theUser.getNewStaff() && !theUser.getUserName().equals(selectedPost.getAuthor())) {
                    return;
                }

                //refresh list to remove bold
                String currentThread = listView_Threads.getSelectionModel().getSelectedItem();
                populatePostList(currentThread);

                //open post view
                guiPost.ViewPost.displayPost(
                        ViewForum.theStage,
                        ViewForum.theUser,
                        selectedPost
                );
            }
        });

        //Buttons
        setupButton(button_CreatePost, width - 250, 20);
        button_CreatePost.setOnAction((_) -> ControllerForum.performCreatePost());

        setupButton(button_Return, 300, height - 50);
        button_Return.setOnAction((_) -> ControllerForum.performReturn());

        setupButton(button_Quit, 580, height - 50);
        button_Quit.setOnAction((_) -> ControllerForum.performQuit());

        theRootPane.getChildren().addAll(
                label_PageTitle,
                label_UserDetails,
                listView_Threads,
                listView_Posts,
                button_CreatePost,
                button_Return,
                button_Quit,
                check_ShowUnread, 
                check_ShowMine,
                check_ShowUnreadReplies,
                text_Search
        );
    }

    private static void setupButton(Button b, double x, double y) {
        b.setFont(Font.font("Dialog", 18));
        b.setMinWidth(210);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
    /**
     * This method populates the thread list.
     */
    public static void populateThreadList() {
        List<String> threads = thePostDatabase.getThreadList();
        listView_Threads.setItems(FXCollections.observableArrayList(threads));
        label_UserDetails.setText("Logged in as: " + theUser.getUserName());
    }
    
    /**
     * This method populates the post list for a thread.
     *
     * @param threadName the name of the selected thread
     */
    public static void populatePostList(String threadName) {
        if (threadName == null) {
            threadName = "ALL";
        }
        //get posts from thread
        List<String> posts = thePostDatabase.getPostTitlesByThread(threadName, theUser.getUserName());
        String search = text_Search.getText().trim().toLowerCase();
        		
        List<String> filtered = posts.stream()
                .filter(item -> {

                    //extract post ID
                    String[] lines = item.split("\n");
                    String second = lines[1];
                    int start = second.indexOf('[') + 1;
                    int end = second.indexOf(']');
                    int postId = Integer.parseInt(second.substring(start, end));

                    Post p = thePostDatabase.getPostById(postId);

                    //filter 1: unread only
                    if(check_ShowUnread.isSelected()) {
                        boolean unread = thePostDatabase.isPostUnread(theUser.getUserName(), postId);
                        if(p.getAuthor().equals(theUser.getUserName())) unread = false;
                        if(!unread) return false;
                    }

                    //filter 2: user's posts 
                    if(check_ShowMine.isSelected()) {
                        if(!p.getAuthor().equals(theUser.getUserName())) return false;
                    }
                    
                    //filter 3: unread replies only
                    if(check_ShowUnreadReplies.isSelected()) {
                    	if(theReplyDatabase.getUnreadReplyCount(theUser.getUserName(), postId) == 0) {
                    		return false;
                    	}
                    }
                    
                    //filter 4: keyword search
                    if(!search.isEmpty()) {
                        if(!p.getTitle().toLowerCase().contains(search)) return false;
                    }

                    return true;
                })
                .toList();
        
        //display posts
        listView_Posts.setItems(FXCollections.observableArrayList(filtered));
    }
    
    private static void refreshFilteredPosts() {
        String currentThread = listView_Threads.getSelectionModel().getSelectedItem();
        populatePostList(currentThread);
    }
}