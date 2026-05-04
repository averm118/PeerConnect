package guiForum;

/**
 * This class controls actions on the forum page.
 *
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */
public class ControllerForum {

	/**
	 * This constructor creates a ControllerForum object.
	 */
	public ControllerForum() {}
	
	
    protected static void performCreatePost() {
    	guiCreatePost.ViewCreatePost.displayCreatePost(ViewForum.theStage, ViewForum.theUser);
    }
    
    protected static void performReturn() {
    	if(ViewForum.theUser.getAdminRole()) {
    		guiAdminHome.ViewAdminHome.displayAdminHome(
    				ViewForum.theStage,
    	            ViewForum.theUser
    	    );
    	}else if(ViewForum.theUser.getNewStaff()) {
   		 	guiStaffHome.ViewStaffHome.displayStaffHome(
	                ViewForum.theStage,
	                ViewForum.theUser
	        );
    	}else if(ViewForum.theUser.getNewStudent()) {
   		 	guiStudentHome.ViewStudentHome.displayStudentHome(
	                ViewForum.theStage,
	                ViewForum.theUser
	        );
    	}
    }

    protected static void performQuit() {
        System.exit(0);
    }
}