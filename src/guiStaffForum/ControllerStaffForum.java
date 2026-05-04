package guiStaffForum;

import guiStaffHome.ViewStaffHome;

/**
 * This class controls actions on the forum page.
 *
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */
public class ControllerStaffForum {

	/**
	 * This constructor creates a ControllerForum object.
	 */
	public ControllerStaffForum() {}
	
    protected static void performCreatePost() {
    	guiCreatePost.ViewCreatePost.displayCreatePost(ViewStaffForum.theStage, ViewStaffForum.theUser);
    }
    
	public static void goToManageThreads() {
		guiManageThreads.ViewManageThreads.displayManageThreads(ViewStaffForum.theStage, ViewStaffForum.theUser);
	}
    
    protected static void performReturn() {
    	if(ViewStaffForum.theUser.getAdminRole()) {
    		guiAdminHome.ViewAdminHome.displayAdminHome(
    				ViewStaffForum.theStage,
    	            ViewStaffForum.theUser
    	    );
    	}else if(ViewStaffForum.theUser.getNewStaff()) {
   		 	guiStaffHome.ViewStaffHome.displayStaffHome(
	                ViewStaffForum.theStage,
	                ViewStaffForum.theUser
	        );
    	}else if(ViewStaffForum.theUser.getNewStudent()) {
   		 	guiStudentHome.ViewStudentHome.displayStudentHome(
	                ViewStaffForum.theStage,
	                ViewStaffForum.theUser
	        );
    	}
    }

    protected static void performQuit() {
        System.exit(0);
    }
}