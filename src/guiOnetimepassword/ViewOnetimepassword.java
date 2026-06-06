package guiOnetimepassword;

import java.util.List;

import database.Database;
import entityClasses.User;
import guiCommon.ActionSpec;
import guiCommon.PeerConnectShell;
import guiCommon.ScreenSpec;
import guiCommon.UiFactory;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewOnetimepassword {
    protected static Label label_PageTitle = new Label("Send One-Time Password");
    protected static Label label_UserDetails = new Label();
    protected static Label label_SelectUser = new Label("Select a user:");
    protected static ComboBox<String> combobox_SelectUser = new ComboBox<>();
    protected static Label label_ConfirmOTP = new Label("Generate an OTP for the selected user.");
    protected static Button button_SendOTP = UiFactory.action(
            ActionSpec.of("Send OTP", "bi-key",
                    ControllerOnetimepassword::performsendOTP));
    protected static Button button_Return = UiFactory.action(
            ActionSpec.of("Return", "bi-arrow-left",
                    ControllerOnetimepassword::performReturn,
                    "pc-button-secondary"));
    protected static Button button_Logout = UiFactory.action(
            ActionSpec.of("Logout", "bi-box-arrow-left",
                    ControllerOnetimepassword::performLogout,
                    "pc-button-secondary"));
    protected static Button button_Quit = UiFactory.action(
            ActionSpec.of("Quit", "bi-x",
                    ControllerOnetimepassword::performQuit,
                    "pc-button-secondary"));

    private static ViewOnetimepassword theView;
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    protected static Stage theStage;
    protected static VBox theRootPane;
    protected static User theUser;
    public static Scene theOTPScene = null;
    protected static String theSelectedUser = "";

    protected static VBox pageCard;
    protected static VBox selectionCard;
    protected static VBox confirmCard;
    protected static HBox footer;

    public static void displayOTPUser(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theView == null) {
            theView = new ViewOnetimepassword();
        }

        label_UserDetails.setText("Admin: " + theUser.getUserName());
        refreshUserList();
        combobox_SelectUser.getSelectionModel().select(0);

        ControllerOnetimepassword.repaintTheWindow();
        ControllerOnetimepassword.doSelectUser();
    }

    public ViewOnetimepassword() {
        label_PageTitle.getStyleClass().add("pc-heading");
        label_UserDetails.getStyleClass().add("pc-body");
        label_SelectUser.getStyleClass().add("pc-field-label");
        label_ConfirmOTP.getStyleClass().add("pc-body");
        UiFactory.prepareCombo(combobox_SelectUser);

        combobox_SelectUser.getSelectionModel().selectedItemProperty().addListener(
                (@SuppressWarnings("unused") ObservableValue<? extends String> observable,
                 @SuppressWarnings("unused") String oldValue,
                 @SuppressWarnings("unused") String newValue) -> ControllerOnetimepassword.doSelectUser());

        pageCard = UiFactory.card(label_PageTitle, label_UserDetails);
        selectionCard = UiFactory.card(UiFactory.section("Account", label_SelectUser, combobox_SelectUser));
        confirmCard = UiFactory.card(
                UiFactory.section("Credential reset", label_ConfirmOTP),
                UiFactory.actions(button_SendOTP));
        footer = UiFactory.actions(button_Return, UiFactory.spacer(), button_Logout, button_Quit);

        theRootPane = new VBox(18);
        theRootPane.getStyleClass().add("pc-screen");
        theRootPane.setAlignment(Pos.TOP_CENTER);

        theOTPScene = PeerConnectShell.scene(
                ScreenSpec.of("One-Time Password",
                        "Issue a temporary password and force the user to reset it at login.",
                        theUser, "Admin", "bi-key"),
                theRootPane);
    }

    protected static void refreshUserList() {
        List<String> userList = theDatabase.getUserList();
        combobox_SelectUser.setItems(FXCollections.observableArrayList(userList));
    }
}
