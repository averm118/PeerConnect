package guiEditPost;

import java.util.List;

import applicationMain.FoundationsMain;
import database.Database;
import database.PostDatabase;
import entityClasses.Post;
import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * This class displays the edit post page.
 *
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */
public class ViewEditPost {

    private static double width = FoundationsMain.WINDOW_WIDTH;
    private static double height = FoundationsMain.WINDOW_HEIGHT;

    private static ViewEditPost theView;
    /** The post database used by this page. */
    public static PostDatabase thePostDatabase = applicationMain.FoundationsMain.postDatabase;
    /** The main database used by this page. */
    public static Database theDatabase = applicationMain.FoundationsMain.database;

    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;
    protected static Post thePost;
    /** The scene for the edit post page. */
    public static Scene theEditPostScene = null;

    // UI elements
    protected static Label label_PageTitle = new Label("Edit Post");
    protected static Label label_UserDetails = new Label();

    protected static Label label_Title = new Label("Title:");
    protected static TextField textField_Title = new TextField();

    protected static Label label_Body = new Label("Body:");
    protected static TextArea textArea_Body = new TextArea();

    protected static Label label_Thread = new Label("Thread:");
    protected static ComboBox<String> combo_Threads = new ComboBox<>();

    protected static Button button_Save = new Button("Save Changes");
    protected static Button button_Return = new Button("Return");
    protected static Button button_Quit = new Button("Quit");

    /**
     * This method displays the edit post page.
     *
     * @param ps the stage used to show the page
     * @param user the current user
     * @param post the post being edited
     */
    public static void displayEditPost(Stage ps, User user, Post post) {

        theStage = ps;
        theUser = user;
        thePost = post;
        ControllerEditPost.thePost = post;

        if (theView == null) theView = new ViewEditPost();

        populateThreadList();
        
        textField_Title.setText(post.getTitle());
        textArea_Body.setText(post.getBody());
        combo_Threads.setValue(post.getThread());

        label_UserDetails.setText("Logged in as: " + theUser.getUserName());

        theStage.setScene(theEditPostScene);
        theStage.show();
    }

    /**
     * This constructor creates the edit post view.
     */
    public ViewEditPost() {

        theRootPane = new Pane();
        theEditPostScene = new Scene(theRootPane, width, height);

        // Page title
        label_PageTitle.setFont(Font.font("Arial", 28));
        label_PageTitle.setMinWidth(width);
        label_PageTitle.setAlignment(Pos.CENTER);
        label_PageTitle.setLayoutX(0);
        label_PageTitle.setLayoutY(20);

        // User info
        label_UserDetails.setFont(Font.font("Arial", 18));
        label_UserDetails.setLayoutX(20);
        label_UserDetails.setLayoutY(70);

        // Thread dropdown
        label_Thread.setFont(Font.font("Arial", 16));
        label_Thread.setLayoutX(50);
        label_Thread.setLayoutY(130);

        combo_Threads.setLayoutX(150);
        combo_Threads.setLayoutY(125);
        combo_Threads.setPrefWidth(300);

        // Title input
        label_Title.setFont(Font.font("Arial", 16));
        label_Title.setLayoutX(50);
        label_Title.setLayoutY(180);

        textField_Title.setLayoutX(150);
        textField_Title.setLayoutY(175);
        textField_Title.setPrefWidth(width - 200);

        // Body input
        label_Body.setFont(Font.font("Arial", 16));
        label_Body.setLayoutX(50);
        label_Body.setLayoutY(230);

        textArea_Body.setLayoutX(150);
        textArea_Body.setLayoutY(225);
        textArea_Body.setPrefSize(width - 200, height - 350);

        // Buttons
        setupButton(button_Save, 50, height - 100);
        button_Save.setOnAction((_) -> ControllerEditPost.performSave());

        setupButton(button_Return, 300, height - 100);
        button_Return.setOnAction((_) -> ControllerEditPost.performReturn());

        setupButton(button_Quit, 550, height - 100);
        button_Quit.setOnAction((_) -> ControllerEditPost.performQuit());

        theRootPane.getChildren().addAll(
            label_PageTitle,
            label_UserDetails,
            label_Thread,
            combo_Threads,
            label_Title,
            textField_Title,
            label_Body,
            textArea_Body,
            button_Save,
            button_Return,
            button_Quit
        );
    }

    /**
     * This method populates the thread list.
     */
    public static void populateThreadList() {
        List<String> threads = thePostDatabase.getAllThreads();
        combo_Threads.getItems().setAll(threads);
    }

    // Buttons
    private static void setupButton(Button b, double x, double y) {
        b.setFont(Font.font("Dialog", 18));
        b.setMinWidth(210);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
}