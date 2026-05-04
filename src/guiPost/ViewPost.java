package guiPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.PostDatabase;
import database.ReplyDatabase;
import database.FeedbackDatabase;
import entityClasses.Feedback;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;
import guiCreatePost.ViewCreatePost;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
/**
 * This class displays the post page.
 *
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */
public class ViewPost {

    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    private static ViewPost theView;

    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;
    /** The currently selected post. */
    public static Post thePost;
    /** The post database used by this page. */
    public static PostDatabase thePostDatabase = applicationMain.FoundationsMain.postDatabase;
    /** The reply database used by this page. */
    public static ReplyDatabase theReplyDatabase = applicationMain.FoundationsMain.replyDatabase;
    /** The scene for the post page. */
    public static Scene theScene;
    /** The feedback database used by this page. */
    public static FeedbackDatabase theFeedbackDatabase = applicationMain.FoundationsMain.feedbackDatabase;
    
    protected static VBox threadBox = new VBox();

    protected static ListView<Reply> listView_Replies = new ListView<>();
    
    protected static CheckBox check_ShowUnread = new CheckBox("Show Unread Replies Only");

    protected static Button button_ReplyToPost = new Button("Reply to Post");
    protected static Button button_Edit = new Button("Edit Post");
    protected static Button button_Delete = new Button("Delete Post");
    protected static Button button_LeaveFeedback = new Button("Leave Feedback");
    protected static Button button_ViewFeedback = new Button("View Feedback");
    protected static Button button_MarkInappropriate = new Button("Mark Inappropriate");
    protected static Button button_ClearFlag = new Button("Clear Flag");
    protected static Button button_Return = new Button("Return");
    /**
     * This method displays the post page.
     *
     * @param ps the stage used to show the page
     * @param user the current user
     * @param post the post being displayed
     */
    public static void displayPost(Stage ps, User user, Post post) {

        theStage = ps;
        theUser = user;
        thePost = post;

        if (theView == null) theView = new ViewPost();

        populatePost();
        populateReplies();

        theStage.setScene(theScene);
        theStage.show();

        thePostDatabase.markPostAsRead(theUser.getUserName(), post.getId());
        theFeedbackDatabase.markFeedbackForTargetAsRead(theUser.getUserName(), "POST", post.getId());
    }

    /**
     * This constructor creates the post view.
     */
    public ViewPost() {

        theRootPane = new Pane();
        theScene = new Scene(theRootPane, width, height);

        //Thread box
        threadBox.setLayoutX(20);
        threadBox.setLayoutY(20);
        threadBox.setSpacing(10);
        threadBox.setPrefWidth(width - 40);
        listView_Replies.setPrefHeight(height - 320);
   
      //main reply button
        button_ReplyToPost.setFont(Font.font("Dialog", 16));
        button_ReplyToPost.setLayoutX(20);
        button_ReplyToPost.setLayoutY(height - 50);
        button_ReplyToPost.setMinWidth(175);
        button_ReplyToPost.setOnAction((_) -> ControllerPost.performSubmitReply());
        
        //Edit button
        button_Edit.setFont(Font.font("Dialog", 16));
        button_Edit.setLayoutX(width-585);
        button_Edit.setLayoutY(height-50);
        button_Edit.setMinWidth(175);
        button_Edit.setOnAction((_) -> ControllerPost.performEdit());
        
        //check box
        check_ShowUnread.setLayoutX(width-200);
        check_ShowUnread.setLayoutY(40);
        check_ShowUnread.setOnAction(_ -> populateReplies());
        
        //delete button
        button_Delete.setFont(Font.font("Dialog", 16));
        button_Delete.setLayoutX(width - 390);
        button_Delete.setLayoutY(height - 50);
        button_Delete.setMinWidth(175);
        button_Delete.setOnAction((_) -> {
        	Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        		confirm.setContentText("Are you sure you want to delete this post?");
                confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        	confirm.showAndWait().ifPresent(response -> {
        		if (response == ButtonType.YES) {
        			ControllerPost.performDelete();
        		}
        	});
        });
        
        //leave feedback button
        button_LeaveFeedback.setFont(Font.font("Dialog", 16));
        button_LeaveFeedback.setLayoutX(20);
        button_LeaveFeedback.setLayoutY(height - 150);
        button_LeaveFeedback.setMinWidth(175);
        button_LeaveFeedback.setOnAction((_) -> ControllerPost.performLeavePostFeedback());
        
        //view feedback button
        button_ViewFeedback.setFont(Font.font("Dialog", 16));
        button_ViewFeedback.setLayoutX(215);
        button_ViewFeedback.setLayoutY(height - 150);
        button_ViewFeedback.setMinWidth(175);
        button_ViewFeedback.setOnAction((_) -> ControllerPost.performViewPostFeedback());
        //mark inappropriate button
        button_MarkInappropriate.setFont(Font.font("Dialog", 16));
        button_MarkInappropriate.setLayoutX(410);
        button_MarkInappropriate.setLayoutY(height - 150);
        button_MarkInappropriate.setMinWidth(175);
        button_MarkInappropriate.setOnAction((_) -> ControllerPost.performMarkPostInappropriate());
        
        //clear flag button
        button_ClearFlag.setFont(Font.font("Dialog", 16));
        button_ClearFlag.setLayoutX(410);
        button_ClearFlag.setLayoutY(height - 150);
        button_ClearFlag.setMinWidth(175);
        button_ClearFlag.setOnAction((_) -> ControllerPost.performClearPostFlag());

        //return button
        button_Return.setFont(Font.font("Dialog", 16));
        button_Return.setLayoutX(width - 195);
        button_Return.setLayoutY(height - 50);
        button_Return.setMinWidth(175);
        button_Return.setOnAction((_) -> ControllerPost.performReturn());
        
        theRootPane.getChildren().addAll(
                threadBox,
                button_ReplyToPost,
                button_LeaveFeedback,
                button_ViewFeedback,
                button_MarkInappropriate,
                button_ClearFlag,
                button_Return,
                check_ShowUnread
        );

    }
    
    //Box containing author, title, and body of original post
    private static VBox buildPostBlock() {
    	VBox box = new VBox();
        box.setSpacing(5);
        box.setPadding(new Insets(12));
        box.setStyle("-fx-background-color: #f2f2f2; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        String postTitleText = thePost.getTitle();
        String postBodyText = thePost.getBody();
        
        if (thePost.getInappropriate() && !theUser.getNewStaff() && !theUser.getUserName().equals(thePost.getAuthor())) {
            postTitleText = "[This post has been flagged as inappropriate and is hidden.]";
            postBodyText = "[This post has been flagged as inappropriate and is hidden.]";
        }

        Label title = new Label((thePost.getInappropriate() ? "[FLAGGED] " : "") + postTitleText);
        title.setFont(Font.font("Arial", 22));

        Label author = new Label("Posted by: " + thePost.getAuthor());
        author.setFont(Font.font("Arial", 14));

        Label body = new Label(postBodyText);
        body.setWrapText(true);

        box.getChildren().addAll(title, author, body);
        return box;
    }


    /**
     * This method populates the post information on the page.
     */
    public static void populatePost() {
        threadBox.getChildren().clear();
        theRootPane.getChildren().remove(button_Edit);
        theRootPane.getChildren().remove(button_Delete);
        theRootPane.getChildren().remove(button_LeaveFeedback);
        theRootPane.getChildren().remove(button_ViewFeedback);
        theRootPane.getChildren().remove(button_MarkInappropriate);
        theRootPane.getChildren().remove(button_ClearFlag);
        
        int unreadFeedbackCount = theFeedbackDatabase.getUnreadFeedbackCount(
              theUser.getUserName(),
              "POST",
              thePost.getId()
        );
          
        if (unreadFeedbackCount > 0) {
              button_ViewFeedback.setText("View Feedback (" + unreadFeedbackCount + " unread)");
        } else {
              button_ViewFeedback.setText("View Feedback");
        }

        VBox postBlock = buildPostBlock();
        threadBox.getChildren().add(postBlock);

        Separator sep = new Separator();
        threadBox.getChildren().add(sep);

        VBox.setVgrow(listView_Replies, Priority.ALWAYS);
        threadBox.getChildren().add(listView_Replies);


        if (theUser.getUserName().equals(thePost.getAuthor())) {
            if (!theRootPane.getChildren().contains(button_Edit))
                theRootPane.getChildren().add(button_Edit);
            if (!theRootPane.getChildren().contains(button_Delete))
                theRootPane.getChildren().add(button_Delete);
        }

        if (theUser.getNewStaff()) {

            // staff should always see view feedback
            if (!theRootPane.getChildren().contains(button_ViewFeedback))
                theRootPane.getChildren().add(button_ViewFeedback);

            // staff cannot leave feedback on own post
            if (!theUser.getUserName().equals(thePost.getAuthor())) {
                if (!theRootPane.getChildren().contains(button_LeaveFeedback))
                    theRootPane.getChildren().add(button_LeaveFeedback);
            }

            // author + staff delete logic
            if (theUser.getUserName().equals(thePost.getAuthor())) {
                if (!theRootPane.getChildren().contains(button_Delete))
                    theRootPane.getChildren().add(button_Delete);
            }

            if (thePost.getInappropriate()) {
                if (!theRootPane.getChildren().contains(button_ClearFlag))
                    theRootPane.getChildren().add(button_ClearFlag);

                // staff can delete flagged posts
                if (!theRootPane.getChildren().contains(button_Delete))
                    theRootPane.getChildren().add(button_Delete);

            } else {
                if (!theRootPane.getChildren().contains(button_MarkInappropriate))
                    theRootPane.getChildren().add(button_MarkInappropriate);
            }

        } else {
            theRootPane.getChildren().remove(button_LeaveFeedback);
            theRootPane.getChildren().remove(button_MarkInappropriate);
            theRootPane.getChildren().remove(button_ClearFlag);
            
            if (theFeedbackDatabase.getVisibleFeedbackCount(
                    theUser.getUserName(),
                    "POST",
                    thePost.getId()) > 0) {

                if (!theRootPane.getChildren().contains(button_ViewFeedback))
                    theRootPane.getChildren().add(button_ViewFeedback);
            } else {
                theRootPane.getChildren().remove(button_ViewFeedback);
            }
        }
    }

    /**
     * This method populates the reply list for the current post.
     */
    public static void populateReplies() {        
    	List<Reply> replies = theReplyDatabase.getReplyList(thePost.getId());
        Map<Integer, List<Reply>> children = new HashMap<>();

        for (Reply r : replies) {
            children.computeIfAbsent(r.getParent(), k -> new ArrayList<>()).add(r);
        }

        List<Reply> ordered = new ArrayList<>();
        buildReplyTree(0, children, ordered, 0);
        
        if (check_ShowUnread.isSelected()) {
            ordered = ordered.stream()
                    .filter(r -> {
                        boolean unread = theReplyDatabase.isReplyUnread(theUser.getUserName(), r.getID());
                        if (r.getAuthor().equals(theUser.getUserName())) unread = false;
                        return unread;
                    })
                    .toList();
        }

        listView_Replies.setItems(FXCollections.observableArrayList(ordered));
        listView_Replies.setCellFactory(list -> new ReplyCell());
    }
    /**
     * This method marks all replies for the current post as read for the current user.
     */
    public static void markAllRepliesAsRead() {
        theReplyDatabase.markRepliesAsRead(theUser.getUserName(), thePost.getId());
    }

    //handles spacing to clearly identify what a reply references
    private static void buildReplyTree(
    		int parentId,
            Map<Integer, List<Reply>> children,
            List<Reply> ordered,
            int depth
    ) {
        if (!children.containsKey(parentId)) return;

        for (Reply r : children.get(parentId)) {
            r.setDepth(depth);
            ordered.add(r);
            buildReplyTree(r.getID(), children, ordered, depth + 1);
        }
    }

    
    //handles replies and their formatting
    private static class ReplyCell extends ListCell<Reply> {
        @Override
        protected void updateItem(Reply reply, boolean empty) {
            super.updateItem(reply, empty);

            if (empty || reply == null) {
                setGraphic(null);
                return;
            }
            
            boolean unread = theReplyDatabase.isReplyUnread(
                    theUser.getUserName(),
                    reply.getID()
            );

            if (reply.getAuthor().equals(theUser.getUserName())) {
                unread = false;
            }

            int unreadFeedbackCount = theFeedbackDatabase.getUnreadFeedbackCount(
                    theUser.getUserName(),
                    "REPLY",
                    reply.getID()
            );
            
            String feedbackText = "";
            if (unreadFeedbackCount > 0) {
                feedbackText = "\nFeedback: " + unreadFeedbackCount + " unread";
            }
            
            String replyBodyText = reply.getBody();
            String replyAuthorText = "Author: " + reply.getAuthor();
            
            if (reply.getInappropriate() && !theUser.getNewStaff() && !theUser.getUserName().equals(reply.getAuthor())) {
                replyAuthorText = "Author: " + reply.getAuthor() + " [FLAGGED]";
                replyBodyText = "[This reply has been flagged as inappropriate and is hidden.]";
                feedbackText = "";
            } else if (reply.getInappropriate()) {
                replyAuthorText = "Author: " + reply.getAuthor() + " [FLAGGED]";
            }

            Label labelInfo = new Label(
                replyAuthorText +
                "\n" + replyBodyText +
                feedbackText
            );
            labelInfo.setWrapText(true);

            if (unread) {
                labelInfo.setStyle("-fx-font-weight: bold;");
            } else {
                labelInfo.setStyle("-fx-font-weight: normal;");
            }

            // Force wrapping
            int indent = reply.getDepth() * 30;
            labelInfo.maxWidthProperty().bind(listView_Replies.widthProperty().subtract(indent+175));

            Button replyButton = new Button("Reply");
            replyButton.setOnAction((_) -> ControllerPost.performReplyToReply(reply));
            
            Button editButton = new Button("Edit");
            editButton.setOnAction((_) -> ControllerPost.performEditReply(reply));
            
            Button feedbackButton = new Button("Leave Feedback");
            feedbackButton.setOnAction((_) -> ControllerPost.performLeaveReplyFeedback(reply));
            
            Button viewFeedbackButton = new Button("View Feedback");
            if (unreadFeedbackCount > 0) {
                viewFeedbackButton.setText("View Feedback (" + unreadFeedbackCount + " unread)");
            }
            viewFeedbackButton.setOnAction((_) -> ControllerPost.performViewReplyFeedback(reply));
            
            Button markButton = new Button("Mark Inappropriate");
            markButton.setOnAction((_) -> ControllerPost.performMarkReplyInappropriate(reply));
            
            Button clearButton = new Button("Clear Flag");
            clearButton.setOnAction((_) -> ControllerPost.performClearReplyFlag(reply));
            HBox box = new HBox();
            box.setSpacing(20);
            box.setPadding(new Insets(0, 0, 0, indent));

            Region spacer = new Region();
            spacer.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(spacer, Priority.SOMETIMES);
            
            if (reply.getAuthor().equals(theUser.getUserName())) {
                box.getChildren().addAll(labelInfo, spacer, editButton, replyButton);
            } else {
            	box.getChildren().addAll(labelInfo, spacer, replyButton);
            }
            
            if (theUser.getNewStaff()) {
                if (reply.getInappropriate()) {
                    box.getChildren().addAll(viewFeedbackButton, feedbackButton, clearButton);
                } else {
                    box.getChildren().addAll(viewFeedbackButton, feedbackButton, markButton);
                }
            } else {
                if (theFeedbackDatabase.getVisibleFeedbackCount(theUser.getUserName(), "REPLY", reply.getID()) > 0) {
                    box.getChildren().add(viewFeedbackButton);
                }
            }
            setGraphic(box);
        }
    }
}