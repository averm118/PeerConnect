package guiFirstAdmin;

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

public class ViewFirstAdmin {
    protected static Stage theStage;
    private static Scene theFirstAdminScene = null;
    private static final int theRole = 1;

    protected static Label label_PasswordsDoNotMatch = new Label();
    protected static TextField text_AdminUsername = new TextField();
    protected static PasswordField text_AdminPassword1 = new PasswordField();
    protected static PasswordField text_AdminPassword2 = new PasswordField();
    private static Button button_AdminSetup = UiFactory.action(
            ActionSpec.of("Setup Admin Account", "bi-shield-check", () ->
                    ControllerFirstAdmin.doSetupAdmin(theStage, 1)));

    protected static Alert alertUsernamePasswordError = new Alert(AlertType.INFORMATION);
    private static Button button_Quit = UiFactory.action(
            ActionSpec.of("Quit", "bi-x", ControllerFirstAdmin::performQuit, "pc-button-secondary"));

    public static void displayFirstAdmin(Stage ps) {
        theStage = ps;
        new ViewFirstAdmin();
        applicationMain.FoundationsMain.activeHomePage = theRole;
        PeerConnectShell.show(theStage, theFirstAdminScene, "PeerConnect: First Admin Setup");
    }

    private ViewFirstAdmin() {
        UiFactory.prepareInput(text_AdminUsername, "Admin username");
        UiFactory.prepareInput(text_AdminPassword1, "Admin password");
        UiFactory.prepareInput(text_AdminPassword2, "Repeat password");

        text_AdminUsername.textProperty().addListener((_, _, _) -> ControllerFirstAdmin.setAdminUsername());
        text_AdminPassword1.textProperty().addListener((_, _, _) -> ControllerFirstAdmin.setAdminPassword1());
        text_AdminPassword2.textProperty().addListener((_, _, _) -> ControllerFirstAdmin.setAdminPassword2());

        button_AdminSetup.setDefaultButton(true);
        button_Quit.setCancelButton(true);

        alertUsernamePasswordError.setTitle("Invalid admin credentials");
        label_PasswordsDoNotMatch.getStyleClass().add("pc-post-cell-hidden");

        GridPane form = UiFactory.formGrid();
        UiFactory.formRow(form, 0, "Username", text_AdminUsername);
        UiFactory.formRow(form, 1, "Password", text_AdminPassword1);
        UiFactory.formRow(form, 2, "Confirm", text_AdminPassword2);

        Label requirements = UiFactory.body(
                "Username: 4-32 characters, starts with a letter, and may include letters, numbers, underscore, hyphen, or period.\n"
                        + "Password: 8-16 characters with uppercase, lowercase, number, and special character.");

        VBox card = UiFactory.card(
                UiFactory.heading("Create the first administrator"),
                UiFactory.body("This account unlocks PeerConnect and prevents hard-coded credentials."),
                form,
                requirements,
                label_PasswordsDoNotMatch,
                new HBox(10, button_AdminSetup, button_Quit));
        card.setMaxWidth(720);

        VBox screen = new VBox(card);
        screen.getStyleClass().add("pc-screen");
        screen.setAlignment(Pos.TOP_CENTER);

        theFirstAdminScene = PeerConnectShell.scene(
                ScreenSpec.of("PeerConnect Setup", "Create the first administrator before opening the forum.",
                        null, "Admin", "bi-shield-lock"),
                UiFactory.scroll(screen));
    }
}
