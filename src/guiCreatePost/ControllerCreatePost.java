package guiCreatePost;

import entityClasses.Post;
import guiPost.ViewPost;
import javafx.scene.control.Alert;

/**
 * This class controls actions on the create post page.
 *
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */
public class ControllerCreatePost {
	
	/**
	 * This constructor creates a ControllerCreatePost object.
	 */
	public ControllerCreatePost() {}

    protected static void performPost() {
        String thread = ViewCreatePost.combo_Threads.getValue();
        String title = ViewCreatePost.textField_Title.getText().trim();
        String body = ViewCreatePost.textArea_Body.getText().trim();

        if (thread == null) {
        	thread = "General";
        }
        
        if (title.isEmpty() || body.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing Fields");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in both the title and the body before posting.");
            alert.showAndWait();
            return;
        }

        Post post = new Post(title, body, ViewCreatePost.theUser.getUserName(), thread);
        
        ViewCreatePost.thePostDatabase.createPost(post);
        
        performReturn();
    }

    protected static void performReturn() {
    	if (ViewCreatePost.theUser.getNewStaff()) {
    		guiStaffForum.ViewStaffForum.displayStaffForum(
    				ViewCreatePost.theStage,
    				ViewCreatePost.theUser
    		);
    	} else {
    		guiForum.ViewForum.displayForum(
    				ViewCreatePost.theStage,
    				ViewCreatePost.theUser
    		);
    	}
    }

    protected static void performQuit() {
        System.exit(0);
    }
}