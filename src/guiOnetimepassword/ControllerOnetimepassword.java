package guiOnetimepassword;


import database.Database;
import javafx.scene.control.Alert;

public class ControllerOnetimepassword {

	//reference to database
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    public ControllerOnetimepassword() {}

    //function for selecting user
    protected static void doSelectUser() {
        ViewOnetimepassword.theSelectedUser =
                (String) ViewOnetimepassword.combobox_SelectUser.getValue();
        repaintTheWindow();
    }
    //function for rebuilding gui 
    protected static void repaintTheWindow() {
        ViewOnetimepassword.theRootPane.getChildren().clear();

        if (ViewOnetimepassword.theSelectedUser == null ||
                ViewOnetimepassword.theSelectedUser.compareTo("<Select a User>") == 0) {
            ViewOnetimepassword.theRootPane.getChildren().addAll(
                    ViewOnetimepassword.pageCard,
                    ViewOnetimepassword.selectionCard,
                    ViewOnetimepassword.footer
            );
        }
        else {
            ViewOnetimepassword.theRootPane.getChildren().addAll(
                    ViewOnetimepassword.pageCard,
                    ViewOnetimepassword.selectionCard,
                    ViewOnetimepassword.confirmCard,
                    ViewOnetimepassword.footer
            );
        }

        guiCommon.PeerConnectShell.show(
                ViewOnetimepassword.theStage,
                ViewOnetimepassword.theOTPScene,
                "PeerConnect: One-Time Password");
    }
 
    protected static void performsendOTP() {
    	if (ViewOnetimepassword.theSelectedUser == null ||
    	        ViewOnetimepassword.theSelectedUser.compareTo("<Select a User>") == 0) {
    	        return;
    	    }
    	 String selectedEntry = ViewOnetimepassword.theSelectedUser;
         String selectedUsername = selectedEntry.split(" — ")[0].trim();

    	 if (selectedUsername.equals(ViewOnetimepassword.theUser.getUserName())) {
             Alert alert = new Alert(Alert.AlertType.WARNING);
             alert.setTitle("Invalid Action");
             alert.setHeaderText("You cannot send yourself an OTP");
             alert.setContentText("Please use Update Account or select a different user");
             alert.showAndWait();
             return;
         }
    
    	    String selected = ViewOnetimepassword.theSelectedUser;
    	    String username = selected.split("—")[0].trim(); 

    	    String otp = theDatabase.generateOTP(username);
    	    
    	    //alert the user
    	    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    	    alert.setTitle("One-Time Password Generated");
    	    alert.setHeaderText("A One-Time Password has been set for " + username);
    	    alert.setContentText("The OTP is:\n\n" + otp + 
    	            "\n\n(User will be forced to change password after login.)");
    	    alert.showAndWait();

    	    String msg = "One-Time Password for " + username + " is:\n" + otp
    	            + "\n\n(User will be forced to change password after login.)";

    	    System.out.println(msg);
            }

    //function for return button
    protected static void performReturn() {
        guiAdminHome.ViewAdminHome.displayAdminHome(ViewOnetimepassword.theStage,
                ViewOnetimepassword.theUser);
    }

    //function for logout button
    protected static void performLogout() {
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewOnetimepassword.theStage);
    }

    //function for quit button
    protected static void performQuit() {
        System.exit(0);
    }
}


