package guiEditPost;

import entityClasses.Post;

/**
 * This class controls actions on the edit post page.
 *
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */
public class ControllerEditPost {
	
	/**
	 * This constructor creates a ControllerEditPost object.
	 */
	public ControllerEditPost() {}

    protected static Post thePost;  // The post being edited

    protected static void performSave() {

        String thread = ViewEditPost.combo_Threads.getValue();
        String title = ViewEditPost.textField_Title.getText().trim();
        String body = ViewEditPost.textArea_Body.getText().trim();

        if (thread == null) {
            thread = "General";
        }

        if (title.isEmpty() || body.isEmpty()) {
            System.out.println("Missing fields.");
            return;
        }

        // Update the existing post object
        thePost.setTitle(title);
        thePost.setBody(body);
        thePost.setThread(thread);

        // Update in database
        ViewEditPost.thePostDatabase.editPost(thePost);

        performReturn();
    }

    protected static void performReturn() {
        guiForum.ViewForum.displayForum(
                ViewEditPost.theStage,
                ViewEditPost.theUser
        );
    }

    protected static void performQuit() {
        System.exit(0);
    }
}