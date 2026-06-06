package guiNewAccount;

import database.Database;
import entityClasses.User;
import guiCommon.ActionSpec;
import guiCommon.PeerConnectShell;
import guiCommon.ScreenSpec;
import guiCommon.UiFactory;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewNewAccount {
    protected static Label label_NewUserCreation = new Label("Create your PeerConnect account");
    protected static Label label_NewUserLine = new Label("Choose a username and password to accept your invitation.");
    protected static TextField text_Username = new TextField();
    protected static PasswordField text_Password1 = new PasswordField();
    protected static PasswordField text_Password2 = new PasswordField();
    protected static Button button_UserSetup = UiFactory.action(
            ActionSpec.of("Create Account", "bi-person-check", ControllerNewAccount::doCreateUser));
    protected static TextField text_Invitation = new TextField();
    protected static Label label_UserNameRequirements = UiFactory.body(
            "Username: 4-32 characters, starts with a letter, and may include letters, numbers, underscore, hyphen, or period.");
    protected static Label label_PasswordRequirements = UiFactory.body(
            "Password: 8-16 characters with uppercase, lowercase, number, and special character.");

    protected static Alert alertInvitationCodeIsInvalid = new Alert(AlertType.INFORMATION);
    protected static Alert alertUsernamePasswordError = new Alert(AlertType.INFORMATION);
    protected static Button button_Quit = UiFactory.action(
            ActionSpec.of("Quit", "bi-x", ControllerNewAccount::performQuit, "pc-button-secondary"));

    private static ViewNewAccount theView;
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    protected static Stage theStage;
    protected static User theUser;
    protected static String theInvitationCode;
    protected static String emailAddress;
    protected static String theRole;
    public static Scene theNewAccountScene = null;

    public static void displayNewAccount(Stage ps, String ic) {
        theStage = ps;
        theInvitationCode = ic;

        if (theView == null) {
            theView = new ViewNewAccount();
        }

        text_Username.clear();
        text_Password1.clear();
        text_Password2.clear();

        theRole = theDatabase.getRoleGivenAnInvitationCode(theInvitationCode);
        if (theRole.length() == 0) {
            alertInvitationCodeIsInvalid.showAndWait();
            return;
        }

        emailAddress = theDatabase.getEmailAddressUsingCode(theInvitationCode);
        text_Invitation.setText(theInvitationCode);
        text_Username.requestFocus();

        PeerConnectShell.show(theStage, theNewAccountScene, "PeerConnect: Account Setup");
    }

    private ViewNewAccount() {
        UiFactory.prepareInput(text_Username, "Username");
        UiFactory.prepareInput(text_Password1, "Password");
        UiFactory.prepareInput(text_Password2, "Repeat password");
        UiFactory.prepareInput(text_Invitation, "Invitation code");
        text_Invitation.setEditable(false);

        button_UserSetup.setDefaultButton(true);
        button_Quit.setCancelButton(true);

        alertInvitationCodeIsInvalid.setTitle("Invalid Invitation Code");
        alertInvitationCodeIsInvalid.setHeaderText("The invitation code is not valid.");
        alertInvitationCodeIsInvalid.setContentText("Correct the code and try again.");

        alertUsernamePasswordError.setTitle("Account setup needs attention");
        alertUsernamePasswordError.setHeaderText(null);

        GridPane form = UiFactory.formGrid();
        UiFactory.formRow(form, 0, "Invitation", text_Invitation);
        UiFactory.formRow(form, 1, "Username", text_Username);
        UiFactory.formRow(form, 2, "Password", text_Password1);
        UiFactory.formRow(form, 3, "Confirm", text_Password2);

        VBox requirements = UiFactory.section("Requirements", label_UserNameRequirements, label_PasswordRequirements);
        VBox card = UiFactory.card(
                UiFactory.heading(label_NewUserCreation.getText()),
                UiFactory.body(label_NewUserLine.getText()),
                form,
                requirements,
                new HBox(10, button_UserSetup, button_Quit));
        card.setMaxWidth(760);

        VBox screen = new VBox(card);
        screen.getStyleClass().add("pc-screen");
        screen.setAlignment(Pos.TOP_CENTER);

        theNewAccountScene = PeerConnectShell.scene(
                ScreenSpec.of("Invitation Accepted", "Set up your account and join the conversation.",
                        null, "New account", "bi-envelope"),
                UiFactory.scroll(screen));
    }
}
