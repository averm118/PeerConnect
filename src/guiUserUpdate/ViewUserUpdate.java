package guiUserUpdate;

import java.util.Optional;

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
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewUserUpdate {
    private static ViewUserUpdate theView;
    private static Database theDatabase = applicationMain.FoundationsMain.database;
    private static Stage theStage;
    private static User theUser;
    public static Scene theUserUpdateScene = null;
    public final static double WINDOW_WIDTH = 500;
    public final static double WINDOW_HEIGHT = 430;

    private static Label label_ApplicationTitle = new Label("Update Account Details");
    private static Label label_Purpose = new Label("Keep your profile complete so conversations and feedback stay clear.");

    private static Label label_Username = new Label("Username:");
    private static Label label_Password = new Label("Password:");
    private static Label label_FirstName = new Label("First Name:");
    private static Label label_MiddleName = new Label("Middle Name:");
    private static Label label_LastName = new Label("Last Name:");
    private static Label label_PreferredFirstName = new Label("Preferred First Name:");
    private static Label label_EmailAddress = new Label("Email Address:");

    private static Label label_CurrentUsername = new Label();
    private static Label label_CurrentPassword = new Label();
    private static Label label_CurrentFirstName = new Label();
    private static Label label_CurrentMiddleName = new Label();
    private static Label label_CurrentLastName = new Label();
    private static Label label_CurrentPreferredFirstName = new Label();
    private static Label label_CurrentEmailAddress = new Label();

    private static Button button_UpdatePassword = UiFactory.action(
            ActionSpec.of("Update Password", "bi-key", ViewUserUpdate::showPasswordWindow, "pc-button-secondary"));
    private static Button button_UpdateFirstName = UiFactory.action(
            ActionSpec.of("Update", "bi-pencil", () -> updateTextValue("First Name", "First Name",
                    value -> theDatabase.updateFirstName(theUser.getUserName(), value)), "pc-button-secondary"));
    private static Button button_UpdateMiddleName = UiFactory.action(
            ActionSpec.of("Update", "bi-pencil", () -> updateTextValue("Middle Name", "Middle Name",
                    value -> theDatabase.updateMiddleName(theUser.getUserName(), value)), "pc-button-secondary"));
    private static Button button_UpdateLastName = UiFactory.action(
            ActionSpec.of("Update", "bi-pencil", () -> updateTextValue("Last Name", "Last Name",
                    value -> theDatabase.updateLastName(theUser.getUserName(), value)), "pc-button-secondary"));
    private static Button button_UpdatePreferredFirstName = UiFactory.action(
            ActionSpec.of("Update", "bi-pencil", () -> updateTextValue("Preferred First Name", "Preferred First Name",
                    value -> theDatabase.updatePreferredFirstName(theUser.getUserName(), value)), "pc-button-secondary"));
    private static Button button_UpdateEmailAddress = UiFactory.action(
            ActionSpec.of("Update", "bi-pencil", () -> updateTextValue("Email Address", "Email Address",
                    value -> theDatabase.updateEmailAddress(theUser.getUserName(), value)), "pc-button-secondary"));

    private static Button button_ProceedToUserHomePage = UiFactory.action(
            ActionSpec.of("Continue", "bi-arrow-right", ViewUserUpdate::continueToHome));

    @FunctionalInterface
    private interface StringUpdater {
        void update(String value);
    }

    public static void displayUserUpdate(Stage ps, User user) {
        theUser = user;
        theStage = ps;

        if (theView == null) {
            theView = new ViewUserUpdate();
        }

        refreshLabels();
        PeerConnectShell.show(theStage, theUserUpdateScene, "PeerConnect: Account Details");
    }

    private ViewUserUpdate() {
        label_ApplicationTitle.getStyleClass().add("pc-heading");
        label_Purpose.getStyleClass().add("pc-body");
        styleValueLabels();

        GridPane grid = UiFactory.formGrid();
        addReadOnlyRow(grid, 0, label_Username, label_CurrentUsername, null);
        addReadOnlyRow(grid, 1, label_Password, label_CurrentPassword, button_UpdatePassword);
        addReadOnlyRow(grid, 2, label_FirstName, label_CurrentFirstName, button_UpdateFirstName);
        addReadOnlyRow(grid, 3, label_MiddleName, label_CurrentMiddleName, button_UpdateMiddleName);
        addReadOnlyRow(grid, 4, label_LastName, label_CurrentLastName, button_UpdateLastName);
        addReadOnlyRow(grid, 5, label_PreferredFirstName, label_CurrentPreferredFirstName,
                button_UpdatePreferredFirstName);
        addReadOnlyRow(grid, 6, label_EmailAddress, label_CurrentEmailAddress, button_UpdateEmailAddress);

        VBox card = UiFactory.card(label_ApplicationTitle, label_Purpose, grid,
                UiFactory.actions(button_ProceedToUserHomePage));
        card.setMaxWidth(840);

        VBox screen = new VBox(card);
        screen.getStyleClass().add("pc-screen");
        screen.setAlignment(Pos.TOP_CENTER);

        theUserUpdateScene = PeerConnectShell.scene(
                ScreenSpec.of("Account Details", "Complete the profile fields required before entering your role home.",
                        theUser, "Profile", "bi-person-badge"),
                UiFactory.scroll(screen));
    }

    private static void styleValueLabels() {
        Label[] labels = {
                label_CurrentUsername,
                label_CurrentPassword,
                label_CurrentFirstName,
                label_CurrentMiddleName,
                label_CurrentLastName,
                label_CurrentPreferredFirstName,
                label_CurrentEmailAddress
        };
        for (Label label : labels) {
            label.getStyleClass().add("pc-body");
            label.setWrapText(true);
        }
    }

    private static void addReadOnlyRow(GridPane grid, int row, Label label, Label value, Button action) {
        label.getStyleClass().add("pc-field-label");
        HBox rowContent = new HBox(10, value);
        rowContent.setAlignment(Pos.CENTER_LEFT);
        if (action != null) {
            rowContent.getChildren().add(action);
        }
        grid.add(label, 0, row);
        grid.add(rowContent, 1, row);
    }

    private static void refreshLabels() {
        theDatabase.getUserAccountDetails(theUser.getUserName());
        label_CurrentUsername.setText(orNone(theUser.getUserName()));
        label_CurrentPassword.setText("Stored securely");
        label_CurrentFirstName.setText(orNone(theUser.getFirstName()));
        label_CurrentMiddleName.setText(orNone(theUser.getMiddleName()));
        label_CurrentLastName.setText(orNone(theUser.getLastName()));
        label_CurrentPreferredFirstName.setText(orNone(theUser.getPreferredFirstName()));
        label_CurrentEmailAddress.setText(orNone(theUser.getEmailAddress()));
    }

    private static String orNone(String value) {
        return value == null || value.isBlank() ? "<none>" : value;
    }

    private static void updateTextValue(String title, String header, StringUpdater updater) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Update " + title);
        dialog.setHeaderText("Update your " + header);
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(value -> {
            updater.update(value);
            theDatabase.getUserAccountDetails(theUser.getUserName());
            syncUserFromDatabase();
            refreshLabels();
        });
    }

    private static void syncUserFromDatabase() {
        theUser.setFirstName(theDatabase.getCurrentFirstName());
        theUser.setMiddleName(theDatabase.getCurrentMiddleName());
        theUser.setLastName(theDatabase.getCurrentLastName());
        theUser.setPreferredFirstName(theDatabase.getCurrentPreferredFirstName());
        theUser.setEmailAddress(theDatabase.getCurrentEmailAddress());
    }

    private static void showPasswordWindow() {
        Stage stage = new Stage();
        stage.setTitle("Update Password");
        Pane root = new Pane();
        guiChangePassword.ViewChangePassword.view(root);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        guiCommon.PeerConnectTheme.attach(scene);
        stage.setScene(scene);
        stage.showAndWait();

        String newPassword = guiChangePassword.ModelChangePassword.goodPassword;
        if (newPassword == null || newPassword.isEmpty()) {
            return;
        }
        theDatabase.updatePassword(theUser.getPassword(), newPassword);
        theUser.setPassword(newPassword);
        label_CurrentPassword.setText("Stored securely");
        guiChangePassword.ModelChangePassword.goodPassword = "";
        guiUserLogin.ViewUserLogin.displayUserLogin(theStage);
    }

    private static void continueToHome() {
        String firstName = theDatabase.getCurrentFirstName();
        String lastName = theDatabase.getCurrentLastName();
        String email = theDatabase.getCurrentEmailAddress();
        String prefName = theDatabase.getCurrentPreferredFirstName();
        String passwordCheck = guiChangePassword.ModelChangePassword.evaluatePassword(theDatabase.getCurrentPassword());
        boolean passwordValid = passwordCheck.isEmpty();

        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty()
                || email == null || email.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText("You must complete the required account information.");
            alert.setContentText("First Name, Last Name, and Email Address are required.");
            alert.showAndWait();
            return;
        }
        if (!passwordValid) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Password is invalid");
            alert.setHeaderText("Please update your password.");
            alert.showAndWait();
            return;
        }

        if (prefName == null || prefName.isEmpty()) {
            theDatabase.updatePreferredFirstName(theUser.getUserName(), firstName);
            theUser.setPreferredFirstName(firstName);
            label_CurrentPreferredFirstName.setText(firstName);
        }

        ControllerUserUpdate.goToUserHomePage(theStage, theUser);
    }
}
