package guiManageThreads;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

import guiStaffForum.ViewStaffForum;

public class ControllerManageThreads {

	/**********
	 * <p> Method: performCreate() </p>
	 * 
	 * <p> Description: This method creates the dialog for creating a new thread. it prompts
	 * the user to input a thread name and confirm the creation </p>
	 * 
	 * @returns creates a new thread
	 * 
	 */
    protected static void performCreate() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Thread");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter new thread name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String name = result.get().trim();
        if (name.isEmpty()) {
            showError("Thread name cannot be empty.");
            return;
        }

        ViewManageThreads.thePostDatabase.createThread(name);
        ViewManageThreads.populateThreadList();
    }

    /**********
	 * <p> Method: performRename() </p>
	 * 
	 * <p> Description: this method creates the dialog for renaming a thread. It prompts
	 * the user to input a new name and allows the user to confirm. </p>
	 * 
	 * @returns renames a thread
	 * 
	 */
    protected static void performRename() {
        String selected = ViewManageThreads.list_Threads.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a thread to rename.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected);
        dialog.setTitle("Rename Thread");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter new name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String newName = result.get().trim();
        if (newName.isEmpty()) {
            showError("Thread name cannot be empty.");
            return;
        }

        ViewManageThreads.thePostDatabase.renameThread(selected, newName);
        ViewManageThreads.populateThreadList();
    }

    /**********
	 * <p> Method: performDelete() </p>
	 * 
	 * <p> Description: this method creates the dialog for deleting a thread. It prompts
	 * the user to input a thread which to delete and allows the user to confirm. </p>
	 * 
	 * @returns deletes a thread
	 * 
	 */
    protected static void performDelete() {
        String selected = ViewManageThreads.list_Threads.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a thread to delete.");
            return;
        }

        ViewManageThreads.thePostDatabase.deleteThread(selected);
        ViewManageThreads.populateThreadList();
    }

    protected static void performReturn() {
   		 guiStaffForum.ViewStaffForum.displayStaffForum(
   			ViewManageThreads.theStage,
   		 	ViewManageThreads.theUser
   		 );
    }

    protected static void performQuit() {
        System.exit(0);
    }

    private static void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
