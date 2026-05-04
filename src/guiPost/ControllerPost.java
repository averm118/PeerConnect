package guiPost;

import java.util.List;

import entityClasses.Feedback;
import entityClasses.Post;
import entityClasses.Reply;
import guiCreatePost.ViewCreatePost;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This class controls actions on the post page.
 *
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */
public class ControllerPost{
	
	/**
	 * This constructor creates a ControllerPost object.
	 */
	public ControllerPost() {}
	
	 /**
     * This method leaves feedback on the post.
     */
    protected static void performLeavePostFeedback() {
    	TextInputDialog dialog = new TextInputDialog();
    	dialog.setTitle("Leave Feedback");
    	dialog.setHeaderText(null);
    	dialog.setContentText("Enter feedback:");
    	
    	dialog.showAndWait().ifPresent(text -> {
    		String trimmed = text.trim();
    		
    		if (!trimmed.isEmpty()) {
    			Feedback feedback = new Feedback(
    					"POST",
    					ViewPost.thePost.getId(),
    					ViewPost.theUser.getUserName(),
    					ViewPost.thePost.getAuthor(),
    					trimmed
    			);
    			ViewPost.theFeedbackDatabase.createFeedback(feedback);
    			ViewPost.populatePost();
    			ViewPost.populateReplies();
    		}
    	});
    }
    
    /**
     * This method views feedback on the post.
     */
    protected static void performViewPostFeedback() {
    	List<Feedback> feedbackList = ViewPost.theFeedbackDatabase.getFeedbackForTarget(
    			"POST",
    			ViewPost.thePost.getId(),
    			ViewPost.theUser.getUserName()
    	);
    	
    	ViewPost.theFeedbackDatabase.markFeedbackForTargetAsRead(
    			ViewPost.theUser.getUserName(),
    			"POST",
    			ViewPost.thePost.getId()
    	);
    	
    	Stage popup = new Stage();
    	popup.initOwner(ViewPost.theStage);
    	popup.setTitle("Post Feedback");
    	
    	VBox root = new VBox(10);
    	root.setPadding(new Insets(15));
    	
    	TextArea area = new TextArea();
    	area.setWrapText(true);
    	area.setEditable(false);
    	
    	String text = "";
    	
    	if (feedbackList.isEmpty()) {
    		text = "No feedback.";
    	}
    	else {
    		for (Feedback feedback : feedbackList) {
    			text += "From: " + feedback.getSender() + "\n";
    			text += "To: " + feedback.getRecipient() + "\n";
    			text += feedback.getBody() + "\n\n";
    		}
    	}
    	
    	area.setText(text);
    	root.getChildren().add(area);
    	
    	popup.setScene(new Scene(root, 450, 300));
    	popup.show();
    	
    	ViewPost.populatePost();
    	ViewPost.populateReplies();
    }
    
    /**
     * This method marks the post as inappropriate.
     */
    protected static void performMarkPostInappropriate() {
    	ViewPost.thePostDatabase.setPostInappropriate(ViewPost.thePost.getId(), true);
    	ViewPost.thePost = ViewPost.thePostDatabase.getPostById(ViewPost.thePost.getId());
    	ViewPost.populatePost();
    }
    
    /**
     * This method clears the inappropriate flag on the post.
     */
    protected static void performClearPostFlag() {
    	ViewPost.thePostDatabase.setPostInappropriate(ViewPost.thePost.getId(), false);
    	ViewPost.thePost = ViewPost.thePostDatabase.getPostById(ViewPost.thePost.getId());
    	ViewPost.populatePost();
    }
	
	/**
	 * This method opens a pop-up for replying to a post or reply.
	 *
	 * @param parentReply the parent reply
	 */
	private static void performReply(Reply parentReply) {
	    Stage popup = new Stage();
	    popup.initOwner(ViewPost.theStage);
	    popup.setTitle("Write a reply");

	    VBox root = new VBox(10);
	    root.setPadding(new Insets(15));

	    TextArea area = new TextArea();
	    area.setPromptText("Write your reply...");
	    area.setWrapText(true);

	    Button submit = new Button("Submit");
	    submit.setOnAction(e -> {
	        String text = area.getText().trim();
	        
	        if (text.isEmpty()) {
	            Alert alert = new Alert(Alert.AlertType.ERROR);
	            alert.setTitle("Empty Reply");
	            alert.setHeaderText(null);
	            alert.setContentText("You must write something before submitting your reply.");
	            alert.showAndWait();
	            return;
	        }

	        if (!text.isEmpty()) {
	            Reply newReply = new Reply(
	                ViewPost.thePost.getId(),
	                parentReply == null ? 0 : parentReply.getID(),
	                text,
	                ViewPost.theUser.getUserName()
	            );
	            ViewPost.theReplyDatabase.createReply(newReply);
	            ViewPost.populateReplies();
	        }
	        popup.close();
	    });

	    root.getChildren().addAll(area, submit);

	    popup.setScene(new Scene(root, 400, 250));
	    popup.show();
	}
	
	/**
	 * This method allows the user to edit a reply.
	 *
	 * @param reply the reply to edit
	 */
	public static void performEditReply(Reply reply) {
	    Stage popup = new Stage();
	    popup.initOwner(ViewPost.theStage);
	    popup.setTitle("Edit reply");

	    VBox root = new VBox(10);
	    root.setPadding(new Insets(15));

	    TextArea area = new TextArea();
	    area.setText(reply.getBody());
	    area.setWrapText(true);

	    Button submit = new Button("Confirm Edit");
	    Button delete = new Button("Delete Reply");
	    
	    submit.setOnAction(e -> {
	        String text = area.getText().trim();
	        if (!text.isEmpty()) {
	            reply.setBody(text);
	            ViewPost.theReplyDatabase.editReply(reply);
	            ViewPost.populateReplies();
	        }
	        popup.close();
	    });
	    
	    delete.setOnAction(e -> {
	        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
	                "Are you sure you want to delete this reply?",
	                ButtonType.YES, ButtonType.NO);

	        confirm.showAndWait().ifPresent(response -> {
	            if (response == ButtonType.YES) {
	                ViewPost.theReplyDatabase.deleteReply(reply.getID());
	                ViewPost.populateReplies();
	                popup.close();
	            }
	        });
	    });
	    
	    //spacer to push delete button to the right
	    Region spacer = new Region();
	    HBox.setHgrow(spacer, Priority.ALWAYS);
	    
	    HBox buttonRow = new HBox(10, submit, spacer, delete);

	    root.getChildren().addAll(area, buttonRow);

	    popup.setScene(new Scene(root, 400, 250));
	    popup.show();

	}
	
	/**
	 * This method allows the user to reply to another reply.
	 *
	 * @param parentReply the reply being responded to
	 */
	public static void performReplyToReply(Reply parentReply) {
	    performReply(parentReply);
	}

	//submits the reply and adds it to the database
    protected static void performSubmitReply() {    
    	performReply(null);  // null = replying to the main post
    }
    
    protected static void performEdit() {
        guiEditPost.ViewEditPost.displayEditPost(
                ViewPost.theStage,
                ViewPost.theUser,
                ViewPost.thePost
        );
    }
    
    protected static void performReturn() {
    	if (ViewPost.thePost != null) {
    	    guiPost.ViewPost.markAllRepliesAsRead();
    	}
    	
    	if (ViewPost.theUser.getNewStaff()) {
    		guiStaffForum.ViewStaffForum.displayStaffForum(
    				ViewPost.theStage,
    				ViewPost.theUser
    		);
    	} else {
    		guiForum.ViewForum.displayForum(
    				ViewPost.theStage,
    				ViewPost.theUser
    		);
    	}
    }

    protected static void performDelete() {
        if (ViewPost.theUser.getNewStaff()) {
            List<Reply> replies = ViewPost.theReplyDatabase.getReplyList(ViewPost.thePost.getId());
            
            for (Reply reply : replies) {
                ViewPost.theReplyDatabase.deleteReplyCompletely(reply.getID());
            }
            
            ViewPost.thePostDatabase.deletePostCompletely(ViewPost.thePost.getId());
        } else {
            ViewPost.thePostDatabase.deletePost(ViewPost.thePost.getId());
        }
        
        performReturn();
    }
    
    /**
     * This method leaves feedback on a reply.
     *
     * @param reply the reply
     */
    protected static void performLeaveReplyFeedback(Reply reply) {
    	TextInputDialog dialog = new TextInputDialog();
    	dialog.setTitle("Leave Feedback");
    	dialog.setHeaderText(null);
    	dialog.setContentText("Enter feedback:");
    	
    	dialog.showAndWait().ifPresent(text -> {
    		String trimmed = text.trim();
    		
    		if (!trimmed.isEmpty()) {
    			Feedback feedback = new Feedback(
    					"REPLY",
    					reply.getID(),
    					ViewPost.theUser.getUserName(),
    					reply.getAuthor(),
    					trimmed
    			);
    			ViewPost.theFeedbackDatabase.createFeedback(feedback);
    			ViewPost.populatePost();
    			ViewPost.populateReplies();
    		}
    	});
    }
    
    /**
     * This method views feedback on a reply.
     *
     * @param reply the reply
     */
    protected static void performViewReplyFeedback(Reply reply) {
    	List<Feedback> feedbackList = ViewPost.theFeedbackDatabase.getFeedbackForTarget(
    			"REPLY",
    			reply.getID(),
    			ViewPost.theUser.getUserName()
    	);
    	
    	ViewPost.theFeedbackDatabase.markFeedbackForTargetAsRead(
    			ViewPost.theUser.getUserName(),
    			"REPLY",
    			reply.getID()
    	);
    	
    	Stage popup = new Stage();
    	popup.initOwner(ViewPost.theStage);
    	popup.setTitle("Reply Feedback");
    	
    	VBox root = new VBox(10);
    	root.setPadding(new Insets(15));
    	
    	TextArea area = new TextArea();
    	area.setWrapText(true);
    	area.setEditable(false);
    	
    	String text = "";
    	
    	if (feedbackList.isEmpty()) {
    		text = "No feedback.";
    	}
    	else {
    		for (Feedback feedback : feedbackList) {
    			text += "From: " + feedback.getSender() + "\n";
    			text += "To: " + feedback.getRecipient() + "\n";
    			text += feedback.getBody() + "\n\n";
    		}
    	}
    	
    	area.setText(text);
    	root.getChildren().add(area);
    	
    	popup.setScene(new Scene(root, 450, 300));
    	popup.show();
    	
    	ViewPost.populatePost();
    	ViewPost.populateReplies();
    }
    
    /**
     * This method marks a reply as inappropriate.
     *
     * @param reply the reply
     */
    protected static void performMarkReplyInappropriate(Reply reply) {
    	ViewPost.theReplyDatabase.setReplyInappropriate(reply.getID(), true);
    	ViewPost.populateReplies();
    }
    
    /**
     * This method clears the inappropriate flag on a reply.
     *
     * @param reply the reply
     */
    protected static void performClearReplyFlag(Reply reply) {
    	ViewPost.theReplyDatabase.setReplyInappropriate(reply.getID(), false);
    	ViewPost.populateReplies();
    }
}