package guiAddRemoveRoles;

import java.util.ArrayList;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewAddRemoveRoles {
    protected static Label label_PageTitle = new Label("Manage User Roles");
    protected static Label label_UserDetails = new Label();
    protected static Button button_UpdateThisUser = UiFactory.action(
            ActionSpec.of("Account Update", "bi-person",
                    ViewAddRemoveRoles::performUpdateThisUser,
                    "pc-button-secondary"));

    protected static Label label_SelectUser = new Label("Select a user to be updated:");
    protected static ComboBox<String> combobox_SelectUser = new ComboBox<>();

    protected static List<String> addList = new ArrayList<>();
    protected static Button button_AddRole = UiFactory.action(
            ActionSpec.of("Add Role", "bi-plus",
                    ControllerAddRemoveRoles::performAddRole));
    protected static List<String> removeList = new ArrayList<>();
    protected static Button button_RemoveRole = UiFactory.action(
            ActionSpec.of("Remove Role", "bi-dash",
                    ControllerAddRemoveRoles::performRemoveRole,
                    "pc-button-danger"));
    protected static Label label_CurrentRoles = new Label("This user's current roles:");
    protected static Label label_SelectRoleToBeAdded = new Label("Select a role to be added:");
    protected static ComboBox<String> combobox_SelectRoleToAdd = new ComboBox<>();
    protected static Label label_SelectRoleToBeRemoved = new Label("Select a role to be removed:");
    protected static ComboBox<String> combobox_SelectRoleToRemove = new ComboBox<>();

    protected static Button button_Return = UiFactory.action(
            ActionSpec.of("Return", "bi-arrow-left",
                    ControllerAddRemoveRoles::performReturn,
                    "pc-button-secondary"));
    protected static Button button_Logout = UiFactory.action(
            ActionSpec.of("Logout", "bi-box-arrow-left",
                    ControllerAddRemoveRoles::performLogout,
                    "pc-button-secondary"));
    protected static Button button_Quit = UiFactory.action(
            ActionSpec.of("Quit", "bi-x",
                    ControllerAddRemoveRoles::performQuit,
                    "pc-button-secondary"));

    private static ViewAddRemoveRoles theView;
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    protected static Stage theStage;
    protected static VBox theRootPane;
    protected static User theUser;
    public static Scene theAddRemoveRolesScene = null;
    protected static String theSelectedUser = "";
    protected static String theAddRole = "";
    protected static String theRemoveRole = "";

    protected static VBox pageCard;
    protected static VBox selectionCard;
    protected static VBox roleCard;
    protected static HBox footer;

    public static void displayAddRemoveRoles(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theView == null) {
            theView = new ViewAddRemoveRoles();
        }

        label_UserDetails.setText("Admin: " + theUser.getUserName());
        refreshUserList();
        combobox_SelectUser.getSelectionModel().select(0);

        ControllerAddRemoveRoles.repaintTheWindow();
        ControllerAddRemoveRoles.doSelectUser();
    }

    public ViewAddRemoveRoles() {
        label_PageTitle.getStyleClass().add("pc-heading");
        label_UserDetails.getStyleClass().add("pc-body");
        label_SelectUser.getStyleClass().add("pc-field-label");
        label_CurrentRoles.getStyleClass().add("pc-body");
        label_SelectRoleToBeAdded.getStyleClass().add("pc-field-label");
        label_SelectRoleToBeRemoved.getStyleClass().add("pc-field-label");

        UiFactory.prepareCombo(combobox_SelectUser);
        UiFactory.prepareCombo(combobox_SelectRoleToAdd);
        UiFactory.prepareCombo(combobox_SelectRoleToRemove);

        combobox_SelectUser.getSelectionModel().selectedItemProperty().addListener(
                (@SuppressWarnings("unused") ObservableValue<? extends String> observable,
                 @SuppressWarnings("unused") String oldValue,
                 @SuppressWarnings("unused") String newValue) -> ControllerAddRemoveRoles.doSelectUser());

        pageCard = UiFactory.card(label_PageTitle, label_UserDetails, button_UpdateThisUser);
        selectionCard = UiFactory.card(UiFactory.section("Account", label_SelectUser, combobox_SelectUser));

        HBox addRow = new HBox(10, combobox_SelectRoleToAdd, button_AddRole);
        HBox.setHgrow(combobox_SelectRoleToAdd, Priority.ALWAYS);
        HBox removeRow = new HBox(10, combobox_SelectRoleToRemove, button_RemoveRole);
        HBox.setHgrow(combobox_SelectRoleToRemove, Priority.ALWAYS);

        GridPane roleGrid = UiFactory.formGrid();
        UiFactory.formRow(roleGrid, 0, "Add role", addRow);
        UiFactory.formRow(roleGrid, 1, "Remove role", removeRow);
        roleCard = UiFactory.card(UiFactory.section("Selected user's roles", label_CurrentRoles, roleGrid));
        footer = UiFactory.actions(button_Return, UiFactory.spacer(), button_Logout, button_Quit);

        theRootPane = new VBox(18);
        theRootPane.getStyleClass().add("pc-screen");
        theRootPane.setAlignment(Pos.TOP_CENTER);

        theAddRemoveRolesScene = PeerConnectShell.scene(
                ScreenSpec.of("Manage Roles",
                        "Add or remove Admin, Staff, and Student access for a selected account.",
                        theUser, "Admin", "bi-person-lines-fill"),
                theRootPane);
    }

    protected static void refreshUserList() {
        List<String> userList = theDatabase.getUserList();
        combobox_SelectUser.setItems(FXCollections.observableArrayList(userList));
    }

    protected static void setupButtonUI(Button button, String ff, double f, double w, Pos p,
            double x, double y) {
        button.setMinWidth(w);
        button.setAlignment(p);
    }

    protected static void setupComboBoxUI(ComboBox<String> combo, String ff, double f, double w,
            double x, double y) {
        UiFactory.prepareCombo(combo);
        combo.setMinWidth(w);
    }

    private static void performUpdateThisUser() {
        guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser);
    }
}
