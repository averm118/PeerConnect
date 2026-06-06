package guiEditPost;

import java.util.List;

import applicationMain.FoundationsMain;
import database.Database;
import database.PostDatabase;
import entityClasses.Post;
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

public class ViewEditPost {
    private static ViewEditPost theView;
    public static PostDatabase thePostDatabase = FoundationsMain.postDatabase;
    public static Database theDatabase = FoundationsMain.database;

    protected static Stage theStage;
    protected static User theUser;
    protected static Post thePost;
    public static Scene theEditPostScene = null;

    protected static Label label_PageTitle = new Label("Edit Post");
    protected static Label label_UserDetails = new Label();
    protected static Label label_Title = new Label("Title:");
    protected static TextField textField_Title = new TextField();
    protected static Label label_Body = new Label("Body:");
    protected static TextArea textArea_Body = new TextArea();
    protected static Label label_Thread = new Label("Thread:");
    protected static ComboBox<String> combo_Threads = new ComboBox<>();
    protected static Button button_Save = UiFactory.action(
            ActionSpec.of("Save Changes", "bi-check2", ControllerEditPost::performSave));
    protected static Button button_Return = UiFactory.action(
            ActionSpec.of("Return", "bi-arrow-left", ControllerEditPost::performReturn, "pc-button-secondary"));
    protected static Button button_Quit = UiFactory.action(
            ActionSpec.of("Quit", "bi-x", ControllerEditPost::performQuit, "pc-button-secondary"));

    public static void displayEditPost(Stage ps, User user, Post post) {
        theStage = ps;
        theUser = user;
        thePost = post;
        ControllerEditPost.thePost = post;

        if (theView == null) {
            theView = new ViewEditPost();
        }

        populateThreadList();
        textField_Title.setText(post.getTitle());
        textArea_Body.setText(post.getBody());
        combo_Threads.setValue(post.getThread());
        label_UserDetails.setText("Editing as " + theUser.getUserName());
        textField_Title.requestFocus();

        PeerConnectShell.show(theStage, theEditPostScene, "PeerConnect: Edit Post");
    }

    public ViewEditPost() {
        label_PageTitle.getStyleClass().add("pc-heading");
        label_UserDetails.getStyleClass().add("pc-body");
        UiFactory.prepareInput(textField_Title, "Write a clear discussion title");
        UiFactory.prepareTextArea(textArea_Body, "Update the post body...");
        UiFactory.prepareCombo(combo_Threads);
        textArea_Body.setPrefRowCount(14);

        GridPane form = UiFactory.formGrid();
        UiFactory.formRow(form, 0, "Thread", combo_Threads);
        UiFactory.formRow(form, 1, "Title", textField_Title);
        UiFactory.formRow(form, 2, "Body", textArea_Body);
        GridPane.setVgrow(textArea_Body, Priority.ALWAYS);

        HBox actions = UiFactory.actions(button_Save, UiFactory.spacer(), button_Return, button_Quit);
        VBox card = UiFactory.card(label_PageTitle, label_UserDetails, form, actions);
        card.setMaxWidth(920);

        VBox screen = new VBox(card);
        screen.getStyleClass().add("pc-screen");
        screen.setAlignment(Pos.TOP_CENTER);

        theEditPostScene = PeerConnectShell.scene(
                ScreenSpec.of("Edit Post", "Refine the conversation without losing its context.",
                        theUser, "Compose", "bi-pencil-square"),
                UiFactory.scroll(screen));
    }

    public static void populateThreadList() {
        List<String> threads = thePostDatabase.getAllThreads();
        combo_Threads.getItems().setAll(threads);
    }
}
