package guiCreatePost;

import java.util.List;

import applicationMain.FoundationsMain;
import database.Database;
import database.PostDatabase;
import entityClasses.User;
import guiCommon.ActionSpec;
import guiCommon.PeerConnectShell;
import guiCommon.ScreenSpec;
import guiCommon.UiFactory;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewCreatePost {
    private static ViewCreatePost theView;
    public static PostDatabase thePostDatabase = FoundationsMain.postDatabase;
    public static Database theDatabase = FoundationsMain.database;

    protected static Stage theStage;
    protected static User theUser;
    public static Scene theCreatePostScene = null;

    protected static Label label_PageTitle = new Label("Create New Post");
    protected static Label label_UserDetails = new Label();
    protected static Label label_Title = new Label("Title:");
    protected static TextField textField_Title = new TextField();
    protected static Label label_Body = new Label("Body:");
    protected static TextArea textArea_Body = new TextArea();
    protected static Label label_Thread = new Label("Thread:");
    protected static ComboBox<String> combo_Threads = new ComboBox<>();
    protected static Button button_Post = UiFactory.action(
            ActionSpec.of("Publish Post", "bi-arrow-right-circle", ControllerCreatePost::performPost));
    protected static Button button_Return = UiFactory.action(
            ActionSpec.of("Return", "bi-arrow-left", ControllerCreatePost::performReturn, "pc-button-secondary"));
    protected static Button button_Quit = UiFactory.action(
            ActionSpec.of("Quit", "bi-x", ControllerCreatePost::performQuit, "pc-button-secondary"));

    public static void displayCreatePost(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theView == null) {
            theView = new ViewCreatePost();
        }

        populateThreadList();
        textField_Title.clear();
        textArea_Body.clear();
        combo_Threads.getSelectionModel().clearSelection();
        combo_Threads.setValue(null);
        label_UserDetails.setText("Composing as " + theUser.getUserName());
        textField_Title.requestFocus();

        PeerConnectShell.show(theStage, theCreatePostScene, "PeerConnect: Create Post");
    }

    public ViewCreatePost() {
        label_PageTitle.getStyleClass().add("pc-heading");
        label_UserDetails.getStyleClass().add("pc-body");
        UiFactory.prepareInput(textField_Title, "Write a clear discussion title");
        UiFactory.prepareTextArea(textArea_Body, "Share context, questions, or updates...");
        UiFactory.prepareCombo(combo_Threads);
        textArea_Body.setPrefRowCount(14);

        GridPane form = UiFactory.formGrid();
        UiFactory.formRow(form, 0, "Thread", combo_Threads);
        UiFactory.formRow(form, 1, "Title", textField_Title);
        UiFactory.formRow(form, 2, "Body", textArea_Body);
        GridPane.setVgrow(textArea_Body, Priority.ALWAYS);

        HBox actions = UiFactory.actions(button_Post, UiFactory.spacer(), button_Return, button_Quit);
        VBox card = UiFactory.card(label_PageTitle, label_UserDetails, form, actions);
        card.setMaxWidth(920);

        VBox screen = new VBox(card);
        screen.getStyleClass().add("pc-screen");
        screen.setAlignment(Pos.TOP_CENTER);

        theCreatePostScene = PeerConnectShell.scene(
                ScreenSpec.of("Create Post", "Start a new conversation or add an announcement.",
                        theUser, "Compose", "bi-pencil-square"),
                UiFactory.scroll(screen));
    }

    public static void populateThreadList() {
        List<String> threads = thePostDatabase.getAllThreads();
        combo_Threads.getItems().setAll(threads);
    }
}
