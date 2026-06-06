package guiAdminHome;

import java.util.ArrayList;
import java.util.List;

import database.Database;
import entityClasses.User;
import guiCommon.ActionSpec;
import guiCommon.PeerConnectShell;
import guiCommon.ScreenSpec;
import guiCommon.UiFactory;
import guiUserUpdate.ViewUserUpdate;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewAdminHome {
    private static ViewAdminHome theView;
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    protected static Stage theStage;
    protected static User theUser;
    private static Scene theAdminHomeScene;
    private static final int theRole = 1;

    protected static Label label_PageTitle = new Label();
    protected static Label label_UserDetails = new Label();
    protected static Button button_UpdateThisUser = UiFactory.action(
            ActionSpec.of("Account", "bi-person", () -> ViewUserUpdate.displayUserUpdate(theStage, theUser),
                    "pc-button-secondary"));

    protected static Label label_NumberOfInvitations = new Label("0");
    protected static Label label_NumberOfUsers = new Label("0");
    protected static Button button_AdminRequests = UiFactory.action(
            ActionSpec.of("Admin Requests", "bi-inboxes", ControllerAdminHome::manageAdminRequests));

    protected static Label label_Invitations = new Label("Send an invitation");
    protected static Label label_InvitationEmailAddress = new Label("Email Address");
    protected static TextField text_InvitationEmailAddress = new TextField();
    protected static ComboBox<String> combobox_SelectRole = new ComboBox<>();
    protected static String[] roles = {"Admin", "Staff", "Student"};
    protected static Button button_SendInvitation = UiFactory.action(
            ActionSpec.of("Send Invitation", "bi-arrow-right-circle", ControllerAdminHome::performInvitation));
    protected static Alert alertEmailError = new Alert(AlertType.INFORMATION);
    protected static Alert alertEmailSent = new Alert(AlertType.INFORMATION);

    protected static Button button_ManageInvitations = UiFactory.action(
            ActionSpec.of("Invitations", "bi-envelope-open", ControllerAdminHome::manageInvitations));
    protected static Button button_SetOnetimePassword = UiFactory.action(
            ActionSpec.of("One-Time Password", "bi-key", ControllerAdminHome::setOnetimePassword));
    protected static Button button_DeleteUser = UiFactory.action(
            ActionSpec.of("Delete User", "bi-person-x", ControllerAdminHome::deleteUser, "pc-button-danger"));
    protected static Button button_ListUsers = UiFactory.action(
            ActionSpec.of("List Users", "bi-people", ControllerAdminHome::listUsers));
    protected static Button button_AddRemoveRoles = UiFactory.action(
            ActionSpec.of("Roles", "bi-person-lines-fill", ControllerAdminHome::addRemoveRoles));
    protected static Alert alertNotImplemented = new Alert(AlertType.INFORMATION);

    protected static Button button_Logout = UiFactory.action(
            ActionSpec.of("Logout", "bi-box-arrow-left", ControllerAdminHome::performLogout, "pc-button-secondary"));
    protected static Button button_Quit = UiFactory.action(
            ActionSpec.of("Quit", "bi-x", ControllerAdminHome::performQuit, "pc-button-secondary"));

    public static void displayAdminHome(Stage ps, User user) {
        theStage = ps;
        theUser = user;
        if (theView == null) {
            theView = new ViewAdminHome();
        }

        theDatabase.getUserAccountDetails(user.getUserName());
        applicationMain.FoundationsMain.activeHomePage = theRole;
        label_PageTitle.setText("Admin Home");
        label_UserDetails.setText("Signed in as " + theUser.getUserName());
        label_NumberOfInvitations.setText(String.valueOf(theDatabase.getNumberOfInvitations()));
        label_NumberOfUsers.setText(String.valueOf(theDatabase.getNumberOfUsers()));
        combobox_SelectRole.getSelectionModel().select(0);

        PeerConnectShell.show(theStage, theAdminHomeScene, "PeerConnect: Admin Home");
    }

    private ViewAdminHome() {
        label_PageTitle.getStyleClass().add("pc-heading");
        label_UserDetails.getStyleClass().add("pc-body");
        label_NumberOfInvitations.getStyleClass().add("pc-stat-value");
        label_NumberOfUsers.getStyleClass().add("pc-stat-value");

        UiFactory.prepareInput(text_InvitationEmailAddress, "student@example.edu");
        UiFactory.prepareCombo(combobox_SelectRole);

        List<String> list = new ArrayList<>();
        for (String role : roles) {
            list.add(role);
        }
        combobox_SelectRole.setItems(FXCollections.observableArrayList(list));
        combobox_SelectRole.getSelectionModel().select(0);

        alertEmailSent.setTitle("Invitation");
        alertEmailSent.setHeaderText("Invitation was sent");

        GridPane inviteForm = UiFactory.formGrid();
        UiFactory.formRow(inviteForm, 0, "Email", text_InvitationEmailAddress);
        UiFactory.formRow(inviteForm, 1, "Role", combobox_SelectRole);

        VBox invitationCard = UiFactory.card(
                UiFactory.section("Invite", inviteForm, UiFactory.actions(button_SendInvitation)));
        VBox peopleCard = UiFactory.card(
                UiFactory.section("People",
                        wrapActions(button_ListUsers, button_AddRemoveRoles, button_SetOnetimePassword,
                                button_ManageInvitations, button_DeleteUser)));
        VBox requestsCard = UiFactory.card(
                UiFactory.section("Collaboration", UiFactory.body("Track shared staff and admin requests."),
                        UiFactory.actions(button_AdminRequests)));

        HBox stats = new HBox(14,
                stat("Outstanding invitations", label_NumberOfInvitations),
                stat("Registered users", label_NumberOfUsers));
        HBox.setHgrow(stats.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(stats.getChildren().get(1), Priority.ALWAYS);

        VBox left = new VBox(18, UiFactory.card(label_PageTitle, label_UserDetails, stats), invitationCard);
        VBox right = new VBox(18, peopleCard, requestsCard);
        left.setMaxWidth(Double.MAX_VALUE);
        right.setMaxWidth(Double.MAX_VALUE);
        HBox layout = new HBox(18, left, right);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);

        HBox footer = UiFactory.actions(button_UpdateThisUser, UiFactory.spacer(), button_Logout, button_Quit);
        VBox screen = new VBox(18, layout, footer);
        screen.getStyleClass().add("pc-screen");
        screen.setAlignment(Pos.TOP_CENTER);

        theAdminHomeScene = PeerConnectShell.scene(
                ScreenSpec.of("Admin Home", "Manage the people and invitations behind PeerConnect.",
                        theUser, "Admin", "bi-shield-check"),
                UiFactory.scroll(screen));
    }

    private static VBox stat(String label, Label value) {
        Label text = new Label(label);
        text.getStyleClass().add("pc-stat-label");
        VBox box = new VBox(4, value, text);
        box.getStyleClass().add("pc-stat");
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private static HBox wrapActions(Button... buttons) {
        HBox row = new HBox(10, buttons);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }
}
