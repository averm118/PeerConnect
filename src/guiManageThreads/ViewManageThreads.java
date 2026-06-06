package guiManageThreads;

import java.util.List;

import applicationMain.FoundationsMain;
import database.PostDatabase;
import entityClasses.User;
import guiCommon.ActionSpec;
import guiCommon.PeerConnectShell;
import guiCommon.ScreenSpec;
import guiCommon.UiFactory;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewManageThreads {
    public static PostDatabase thePostDatabase = FoundationsMain.postDatabase;

    protected static Stage theStage;
    protected static User theUser;
    public static Scene theScene;

    protected static Label label_Title = new Label("Thread Manager");
    protected static ListView<String> list_Threads = new ListView<>();

    protected static Button button_Create = UiFactory.action(
            ActionSpec.of("Create Thread", "bi-plus", ControllerManageThreads::performCreate));
    protected static Button button_Rename = UiFactory.action(
            ActionSpec.of("Rename Thread", "bi-pencil", ControllerManageThreads::performRename,
                    "pc-button-secondary"));
    protected static Button button_Delete = UiFactory.action(
            ActionSpec.of("Delete Thread", "bi-trash", ControllerManageThreads::performDelete,
                    "pc-button-danger"));
    protected static Button button_Return = UiFactory.action(
            ActionSpec.of("Return", "bi-arrow-left", ControllerManageThreads::performReturn,
                    "pc-button-secondary"));
    protected static Button button_Quit = UiFactory.action(
            ActionSpec.of("Quit", "bi-x", ControllerManageThreads::performQuit,
                    "pc-button-secondary"));

    public static void displayManageThreads(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theScene == null) {
            new ViewManageThreads();
        }

        populateThreadList();
        PeerConnectShell.show(theStage, theScene, "PeerConnect: Thread Manager");
    }

    public ViewManageThreads() {
        label_Title.getStyleClass().add("pc-heading");
        UiFactory.prepareList(list_Threads, "Thread list");

        VBox listCard = UiFactory.card(
                UiFactory.section("Threads", list_Threads));
        VBox.setVgrow(list_Threads, Priority.ALWAYS);

        HBox tools = UiFactory.actions(button_Create, button_Rename, button_Delete,
                UiFactory.spacer(), button_Return, button_Quit);
        VBox screen = new VBox(18, UiFactory.card(label_Title,
                UiFactory.body("Create, rename, or remove forum threads.")), listCard, tools);
        screen.getStyleClass().add("pc-screen");
        screen.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(listCard, Priority.ALWAYS);

        theScene = PeerConnectShell.scene(
                ScreenSpec.of("Thread Manager", "Keep the staff forum organized and readable.",
                        theUser, "Staff", "bi-folder2-open"),
                screen);
    }

    public static void populateThreadList() {
        List<String> threads = thePostDatabase.getAllThreads();
        threads.removeIf(t -> t == null || t.trim().isEmpty());
        list_Threads.getItems().setAll(threads);
    }
}
