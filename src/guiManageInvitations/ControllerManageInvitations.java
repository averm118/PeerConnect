package guiManageInvitations;

/***************************************************************
 * ControllerManageInvitations
 *
 * Purpose:
 * Handles button actions on the Manage Invitations screen.
 ***************************************************************/
public class ControllerManageInvitations {

    /***************************************************************
     * performReturn
     * Returns user to Admin Home screen.
     ***************************************************************/
    protected static void performReturn() {
    	ViewManageInvitations.stopTimer();
        guiAdminHome.ViewAdminHome.displayAdminHome(
                ViewManageInvitations.theStage,
                ViewManageInvitations.theUser
        );
    }

    /***************************************************************
     * performDelete
     * Delete selected invitation.
     * refresh list
     ***************************************************************/
    protected static void performDelete() {
    	String select = ViewManageInvitations.listView_Invitations.getSelectionModel().getSelectedItem();
    	if(select == null) {
    		return;
    	}
    	
    	String code = select.trim().split("\\s+")[0];
    	ViewManageInvitations.theDatabase.deleteInvitationCode(code);
    	ViewManageInvitations.populateInvitationList();
    }

    /***************************************************************
     * performQuit
     * Completely exits the application.
     ***************************************************************/
    protected static void performQuit() {
    	ViewManageInvitations.stopTimer();
        System.exit(0);
    }
}
