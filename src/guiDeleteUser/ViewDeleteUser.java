package guiDeleteUser;

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

public class ViewDeleteUser {
    protected static Label label_PageTitle = new Label("Delete User");
    protected static Label label_UserDetails = new Label();
    protected static Label label_SelectUser = new Label("Select a user to delete:");
    protected static ComboBox<String> combobox_SelectUser = new ComboBox<>();
    protected static Label label_ConfirmDelete = new Label("Press the button below to delete this user:");
    protected static Button button_DeleteUser = UiFactory.action(
            ActionSpec.of("Delete User", "bi-trash",
                    ControllerDeleteUser::performDeleteUser,
                    "pc-button-danger"));
    protected static Button button_Return = UiFactory.action(
            ActionSpec.of("Return", "bi-arrow-left",
                    ControllerDeleteUser::performReturn,
                    "pc-button-secondary"));
    protected static Button button_Logout = UiFactory.action(
            ActionSpec.of("Logout", "bi-box-arrow-left",
                    ControllerDeleteUser::performLogout,
                    "pc-button-secondary"));
    protected static Button button_Quit = UiFactory.action(
            ActionSpec.of("Quit", "bi-x",
                    ControllerDeleteUser::performQuit,
                    "pc-button-secondary"));

    private static ViewDeleteUser theView;
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    protected static Stage theStage;
    protected static VBox theRootPane;
    protected static User theUser;
    public static Scene theDeleteUserScene = null;
    protected static String theSelectedUser = "";

    protected static VBox pageCard;
    protected static VBox selectionCard;
    protected static VBox confirmCard;
    protected static HBox footer;

    public static void displayDeleteUser(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theView == null) {
            theView = new ViewDeleteUser();
        }

        label_UserDetails.setText("Admin: " + theUser.getUserName());
        refreshUserList();
        combobox_SelectUser.getSelectionModel().select(0);

        ControllerDeleteUser.repaintTheWindow();
        ControllerDeleteUser.doSelectUser();
    }

    public ViewDeleteUser() {
        label_PageTitle.getStyleClass().add("pc-heading");
        label_UserDetails.getStyleClass().add("pc-body");
        label_SelectUser.getStyleClass().add("pc-field-label");
        label_ConfirmDelete.getStyleClass().add("pc-body");
        UiFactory.prepareCombo(combobox_SelectUser);

        combobox_SelectUser.getSelectionModel().selectedItemProperty().addListener(
                (@SuppressWarnings("unused") ObservableValue<? extends String> observable,
                 @SuppressWarnings("unused") String oldValue,
                 @SuppressWarnings("unused") String newValue) -> ControllerDeleteUser.doSelectUser());

        pageCard = UiFactory.card(label_PageTitle, label_UserDetails);
        selectionCard = UiFactory.card(UiFactory.section("Account", label_SelectUser, combobox_SelectUser));
        confirmCard = UiFactory.card(
                UiFactory.section("Destructive action", label_ConfirmDelete),
                UiFactory.actions(button_DeleteUser));
        footer = UiFactory.actions(button_Return, UiFactory.spacer(), button_Logout, button_Quit);

        theRootPane = new VBox(18);
        theRootPane.getStyleClass().add("pc-screen");
        theRootPane.setAlignment(Pos.TOP_CENTER);

        theDeleteUserScene = PeerConnectShell.scene(
                ScreenSpec.of("Delete User",
                        "Remove an account only after selecting and confirming the target user.",
                        theUser, "Admin", "bi-person-x"),
                theRootPane);
    }

    protected static void refreshUserList() {
        List<String> userList = theDatabase.getUserList();
        combobox_SelectUser.setItems(FXCollections.observableArrayList(userList));
    }
}
