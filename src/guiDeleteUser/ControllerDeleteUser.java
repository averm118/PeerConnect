package guiDeleteUser;

import java.util.Optional;
import database.Database;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ControllerDeleteUser {

	//reference to database
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    public ControllerDeleteUser() {}

    //function for selecting user
    protected static void doSelectUser() {
        ViewDeleteUser.theSelectedUser =
                (String) ViewDeleteUser.combobox_SelectUser.getValue();
        repaintTheWindow();
    }

    //function for rebuilding gui 
    protected static void repaintTheWindow() {
        ViewDeleteUser.theRootPane.getChildren().clear();

        if (ViewDeleteUser.theSelectedUser == null ||
                ViewDeleteUser.theSelectedUser.compareTo("<Select a User>") == 0) {
            ViewDeleteUser.theRootPane.getChildren().addAll(
                    ViewDeleteUser.pageCard,
                    ViewDeleteUser.selectionCard,
                    ViewDeleteUser.footer
            );
        }
        else {
            ViewDeleteUser.theRootPane.getChildren().addAll(
                    ViewDeleteUser.pageCard,
                    ViewDeleteUser.selectionCard,
                    ViewDeleteUser.confirmCard,
                    ViewDeleteUser.footer
            );
        }

        guiCommon.PeerConnectShell.show(
                ViewDeleteUser.theStage,
                ViewDeleteUser.theDeleteUserScene,
                "PeerConnect: Delete User");
    }

    //function for deleting user 
    protected static void performDeleteUser() {
    	if (ViewDeleteUser.theSelectedUser.compareTo("<Select a User>") == 0)
            return;

        //extract actual username
        String selectedEntry = ViewDeleteUser.theSelectedUser;
        String selectedUsername = selectedEntry.split(" — ")[0].trim();

        //prevent deleting yourself
        if (selectedUsername.equals(ViewDeleteUser.theUser.getUserName())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Action");
            alert.setHeaderText("You cannot delete your own account.");
            alert.setContentText("Please select a different user.");
            alert.showAndWait();
            return;
        }

        //confirmation pop-up gui
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Are you sure you want to delete this user?");
        confirm.setContentText("User: " + selectedUsername);

        //wait for admin response
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            //delete user
            theDatabase.deleteUser(selectedUsername);

            //refresh gui
            ViewDeleteUser.refreshUserList();
            ViewDeleteUser.combobox_SelectUser.getSelectionModel().select(0);
            doSelectUser();
        }

    }

    //function for return button
    protected static void performReturn() {
        guiAdminHome.ViewAdminHome.displayAdminHome(ViewDeleteUser.theStage,
                ViewDeleteUser.theUser);
    }

    //function for logout button
    protected static void performLogout() {
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewDeleteUser.theStage);
    }

    //function for quit button
    protected static void performQuit() {
        System.exit(0);
    }
}
