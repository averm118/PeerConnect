package guiAdminRequestManagement;

import entityClasses.AdminRequest;
import guiAdminHome.ViewAdminHome;
import guiStaffHome.ViewStaffHome;

/**
 * Controller for the Admin Request Management page.
 *
 * This page is shared by staff and admin users. Staff can create requests,
 * reopen closed requests, and update reopened requests. Admins can additionally
 * close open requests after handling them.
 *
* @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */
public class ControllerAdminRequestManagement {

    /** Default constructor is not used. */
    public ControllerAdminRequestManagement() {}

    /** Creates a new open request from the text area content. */
    protected static void performCreateRequest() {
        String description = ViewAdminRequestManagement.text_RequestDescription.getText().trim();
        if (description.isEmpty()) {
            ViewAdminRequestManagement.showMessage("Enter a request description before creating the request.");
            return;
        }

        ViewAdminRequestManagement.theDatabase.createAdminRequest(
                ViewAdminRequestManagement.theUser.getUserName(), description, null);
        ViewAdminRequestManagement.text_RequestDescription.clear();
        ViewAdminRequestManagement.refreshLists();
        ViewAdminRequestManagement.showMessage("Request created successfully.");
    }

    /** Closes the currently selected open request. Admin only. */
    protected static void performCloseRequest() {
        AdminRequest selected = ViewAdminRequestManagement.getSelectedOpenRequest();
        if (selected == null) {
            ViewAdminRequestManagement.showMessage("Select an open request to close.");
            return;
        }

        String adminComment = ViewAdminRequestManagement.text_RequestDescription.getText().trim();
        if (adminComment.isEmpty()) {
            ViewAdminRequestManagement.showMessage(
                    "Enter an admin comment in the text area before closing the request.");
            return;
        }

        ViewAdminRequestManagement.theDatabase.closeAdminRequest(
                selected.getRequestId(),
                ViewAdminRequestManagement.theUser.getUserName(),
                adminComment);

        ViewAdminRequestManagement.refreshLists();
        ViewAdminRequestManagement.text_RequestDescription.clear();
        ViewAdminRequestManagement.showMessage("Request closed successfully.");
    }

    /** Reopens the currently selected closed request by creating a linked open request. */
    protected static void performReopenRequest() {
        AdminRequest selected = ViewAdminRequestManagement.getSelectedClosedRequest();
        if (selected == null) {
            ViewAdminRequestManagement.showMessage("Select a closed request to reopen.");
            return;
        }

        int newId = ViewAdminRequestManagement.theDatabase.reopenAdminRequest(
                selected.getRequestId(),
                ViewAdminRequestManagement.theUser.getUserName(),
                selected.getDescription());

        ViewAdminRequestManagement.refreshLists();
        ViewAdminRequestManagement.selectOpenRequestById(newId);
        ViewAdminRequestManagement.loadSelectedRequestIntoEditor();
        ViewAdminRequestManagement.showMessage(
                "Closed request reopened. The new open request is linked to request #"
                        + selected.getRequestId() + ".");
    }

    /** Updates the description of the selected reopened request. */
    protected static void performUpdateReopenedRequest() {
        AdminRequest selected = ViewAdminRequestManagement.getSelectedOpenRequest();
        if (selected == null) {
            ViewAdminRequestManagement.showMessage("Select an open request to update.");
            return;
        }

        if (selected.getOriginalRequestId() == null) {
            ViewAdminRequestManagement.showMessage(
                    "Only reopened requests can be updated for this user story.");
            return;
        }

        if (!selected.getRequesterUserName().equals(ViewAdminRequestManagement.theUser.getUserName())) {
            ViewAdminRequestManagement.showMessage(
                    "You can only update reopened requests that you reopened yourself.");
            return;
        }

        String updatedDescription = ViewAdminRequestManagement.text_RequestDescription.getText().trim();
        if (updatedDescription.isEmpty()) {
            ViewAdminRequestManagement.showMessage("Description cannot be empty.");
            return;
        }

        ViewAdminRequestManagement.theDatabase.updateAdminRequestDescription(
                selected.getRequestId(), updatedDescription);
        ViewAdminRequestManagement.refreshLists();
        ViewAdminRequestManagement.selectOpenRequestById(selected.getRequestId());
        ViewAdminRequestManagement.loadSelectedRequestIntoEditor();
        ViewAdminRequestManagement.showMessage("Reopened request updated successfully.");
    }

    /** Returns the user to the proper home page based on role. */
    protected static void performReturn() {
        if (ViewAdminRequestManagement.theUser.getAdminRole()) {
            ViewAdminHome.displayAdminHome(ViewAdminRequestManagement.theStage,
                    ViewAdminRequestManagement.theUser);
        } else {
            ViewStaffHome.displayStaffHome(ViewAdminRequestManagement.theStage,
                    ViewAdminRequestManagement.theUser);
        }
    }

    /** Exits the application. */
    protected static void performQuit() {
        System.exit(0);
    }
}
