package guiListUsers;

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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewListUsers {
    private static ViewListUsers theView;
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    protected static Stage theStage;
    protected static User theUser;
    public static Scene theListUsersScene = null;

    protected static Label label_PageTitle = new Label("List of All Users");
    protected static Label label_UserDetails = new Label();
    protected static ListView<String> listView_Users = new ListView<>();
    protected static Button button_Return = UiFactory.action(
            ActionSpec.of("Return", "bi-arrow-left", ControllerListUsers::performReturn, "pc-button-secondary"));
    protected static Button button_Logout = UiFactory.action(
            ActionSpec.of("Logout", "bi-box-arrow-left", ControllerListUsers::performLogout, "pc-button-secondary"));
    protected static Button button_Quit = UiFactory.action(
            ActionSpec.of("Quit", "bi-x", ControllerListUsers::performQuit, "pc-button-secondary"));

    public static void displayListUsers(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theView == null) {
            theView = new ViewListUsers();
        }

        populateUserList();
        PeerConnectShell.show(theStage, theListUsersScene, "PeerConnect: Users");
    }

    public ViewListUsers() {
        label_PageTitle.getStyleClass().add("pc-heading");
        label_UserDetails.getStyleClass().add("pc-body");
        UiFactory.prepareList(listView_Users, "All users");

        VBox listCard = UiFactory.card(UiFactory.section("Users", listView_Users));
        VBox.setVgrow(listView_Users, Priority.ALWAYS);
        VBox.setVgrow(listCard, Priority.ALWAYS);

        HBox footer = UiFactory.actions(button_Return, UiFactory.spacer(), button_Logout, button_Quit);
        VBox screen = new VBox(18, UiFactory.card(label_PageTitle, label_UserDetails), listCard, footer);
        screen.getStyleClass().add("pc-screen");
        screen.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(listCard, Priority.ALWAYS);

        theListUsersScene = PeerConnectShell.scene(
                ScreenSpec.of("Users", "Review every registered PeerConnect account.",
                        theUser, "Admin", "bi-people"),
                screen);
    }

    private static void populateUserList() {
        List<String> userList = theDatabase.getUserListEnriched();
        listView_Users.setItems(FXCollections.observableArrayList(userList));
        label_UserDetails.setText("Logged in as " + theUser.getUserName());
    }
}
