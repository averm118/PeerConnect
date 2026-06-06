package guiMultipleRoleDispatch;

import java.util.ArrayList;
import java.util.List;

import database.Database;
import entityClasses.User;
import guiCommon.ActionSpec;
import guiCommon.PeerConnectShell;
import guiCommon.ScreenSpec;
import guiCommon.UiFactory;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewMultipleRoleDispatch {
    private static ViewMultipleRoleDispatch theView;
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    protected static Stage theStage;
    protected static User theUser;
    private static Scene theMultipleRoleDispatchScene = null;

    private static Label label_PageTitle = new Label("Choose Your Workspace");
    private static Label label_UserDetails = new Label();
    private static Label label_WhichRole = new Label("Which role do you wish to play:");
    protected static ComboBox<String> combobox_SelectRole = new ComboBox<>();
    private static Button button_PerformRole = UiFactory.action(
            ActionSpec.of("Continue", "bi-arrow-right",
                    ControllerMultipleRoleDispatch::performRole));
    private static Button button_Logout = UiFactory.action(
            ActionSpec.of("Logout", "bi-box-arrow-left",
                    ControllerMultipleRoleDispatch::performLogout,
                    "pc-button-secondary"));
    private static Button button_Quit = UiFactory.action(
            ActionSpec.of("Quit", "bi-x",
                    ControllerMultipleRoleDispatch::performQuit,
                    "pc-button-secondary"));

    public static void displayMultipleRoleDispatch(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theView == null) {
            theView = new ViewMultipleRoleDispatch();
        }

        List<String> list = new ArrayList<>();
        theDatabase.getUserAccountDetails(theUser.getUserName());
        label_UserDetails.setText("Signed in as " + theUser.getUserName());

        list.add("<Select a role>");
        if (theDatabase.getCurrentAdminRole()) {
            list.add("Admin");
        }
        if (theDatabase.getCurrentNewStaff()) {
            list.add("Staff");
        }
        if (theDatabase.getCurrentNewStudent()) {
            list.add("Student");
        }
        combobox_SelectRole.setItems(FXCollections.observableArrayList(list));
        combobox_SelectRole.getSelectionModel().select(0);

        PeerConnectShell.show(theStage, theMultipleRoleDispatchScene, "PeerConnect: Choose Role");
    }

    private ViewMultipleRoleDispatch() {
        label_PageTitle.getStyleClass().add("pc-heading");
        label_UserDetails.getStyleClass().add("pc-body");
        label_WhichRole.getStyleClass().add("pc-field-label");
        UiFactory.prepareCombo(combobox_SelectRole);

        GridPane form = UiFactory.formGrid();
        UiFactory.formRow(form, 0, "Role", combobox_SelectRole);

        VBox roleCard = UiFactory.card(
                label_PageTitle,
                label_UserDetails,
                UiFactory.body("This account has more than one active role. Pick the workspace for this session."),
                form,
                UiFactory.actions(button_PerformRole));

        HBox footer = UiFactory.actions(button_Logout, UiFactory.spacer(), button_Quit);
        VBox screen = new VBox(18, roleCard, footer);
        screen.getStyleClass().add("pc-screen");
        screen.setAlignment(Pos.TOP_CENTER);

        theMultipleRoleDispatchScene = PeerConnectShell.scene(
                ScreenSpec.of("Choose Role",
                        "Open the right PeerConnect workspace for this session.",
                        theUser, "Multi-role", "bi-person-badge"),
                screen);
    }
}
