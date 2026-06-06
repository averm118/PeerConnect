package guiUserLogin;

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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewUserLogin {
    private static Stage theStage;
    public static Scene theUserLoginScene = null;
    private static ViewUserLogin theView = null;

    protected static Alert alertUsernamePasswordError = new Alert(AlertType.INFORMATION);

    protected static TextField text_Username = new TextField();
    protected static PasswordField text_Password = new PasswordField();
    private static Button button_Login = UiFactory.action(
            ActionSpec.of("Log In", "bi-box-arrow-in-right", () -> ControllerUserLogin.doLogin(theStage)));

    private static TextField text_Invitation = new TextField();
    private static Button button_SetupAccount = UiFactory.action(
            ActionSpec.of("Setup Account", "bi-person-plus", () ->
                    ControllerUserLogin.doSetupAccount(theStage, text_Invitation.getText()), "pc-button-secondary"));

    private static Button button_Quit = UiFactory.action(
            ActionSpec.of("Quit", "bi-x", ControllerUserLogin::performQuit, "pc-button-secondary"));

    public static void displayUserLogin(Stage ps) {
        theStage = ps;
        if (theView == null) {
            theView = new ViewUserLogin();
        }

        text_Username.clear();
        text_Password.clear();
        text_Invitation.clear();
        text_Username.requestFocus();

        PeerConnectShell.show(theStage, theUserLoginScene, "PeerConnect: Sign In");
    }

    private ViewUserLogin() {
        UiFactory.prepareInput(text_Username, "Username");
        UiFactory.prepareInput(text_Password, "Password");
        UiFactory.prepareInput(text_Invitation, "Invitation code");

        button_Login.setDefaultButton(true);
        button_Quit.setCancelButton(true);

        alertUsernamePasswordError.setTitle("Invalid username/password");
        alertUsernamePasswordError.setHeaderText(null);

        Label intro = UiFactory.heading("A discussion workspace for students, staff, and admins.");
        Label support = UiFactory.body("Sign in to follow threads, review feedback, manage requests, and keep course conversations moving.");

        GridPane loginForm = UiFactory.formGrid();
        UiFactory.formRow(loginForm, 0, "Username", text_Username);
        UiFactory.formRow(loginForm, 1, "Password", text_Password);

        VBox loginCard = UiFactory.card(
                UiFactory.section("Existing account", loginForm, UiFactory.actions(button_Login)));
        VBox.setVgrow(loginCard, Priority.NEVER);

        GridPane inviteForm = UiFactory.formGrid();
        UiFactory.formRow(inviteForm, 0, "Invitation", text_Invitation);
        VBox inviteCard = UiFactory.card(
                UiFactory.section("New here", inviteForm, UiFactory.actions(button_SetupAccount)));

        HBox cards = new HBox(18, loginCard, inviteCard);
        cards.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(loginCard, Priority.ALWAYS);
        HBox.setHgrow(inviteCard, Priority.ALWAYS);
        loginCard.setMaxWidth(Double.MAX_VALUE);
        inviteCard.setMaxWidth(Double.MAX_VALUE);

        VBox screen = new VBox(22, intro, support, cards, UiFactory.actions(button_Quit));
        screen.getStyleClass().add("pc-screen");
        screen.setAlignment(Pos.TOP_CENTER);

        theUserLoginScene = PeerConnectShell.scene(
                ScreenSpec.of("PeerConnect", "Course conversations, feedback, and role tools in one place.",
                        null, "Welcome", "bi-chat-square-text"),
                UiFactory.scroll(screen));
    }
}
