package guiManageThreads;

import java.util.List;

import applicationMain.FoundationsMain;
import database.PostDatabase;
import entityClasses.User;
import guiStaffForum.ControllerStaffForum;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ViewManageThreads {

    private static double width = FoundationsMain.WINDOW_WIDTH;
    private static double height = FoundationsMain.WINDOW_HEIGHT;

    public static PostDatabase thePostDatabase = FoundationsMain.postDatabase;

    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;

    public static Scene theScene;

    protected static Label label_Title = new Label("Thread Manager");
    protected static ListView<String> list_Threads = new ListView<>();

    protected static Button button_Create = new Button("Create Thread");
    protected static Button button_Rename = new Button("Rename Thread");
    protected static Button button_Delete = new Button("Delete Thread");
    protected static Button button_Return = new Button("Return");
    protected static Button button_Quit = new Button("Quit");

    /**********
	 * <p> Method: desplayManageThreads() </p>
	 * 
	 * <p> Description: this method creates a new scene for which to handle thread management. </p>
	 * 
	 */
    public static void displayManageThreads(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theScene == null) new ViewManageThreads();

        populateThreadList();

        theStage.setScene(theScene);
        theStage.show();
    }

    /**********
	 * <p> Method: ViewManageThreads() </p>
	 * 
	 * <p> Description: this method creates all of the objects responsible for the gui. </p>
	 * 
	 */
    public ViewManageThreads() {
        theRootPane = new Pane();
        theScene = new Scene(theRootPane, width, height);

        label_Title.setFont(Font.font("Arial", 28));
        label_Title.setMinWidth(width);
        label_Title.setAlignment(Pos.CENTER);
        label_Title.setLayoutY(20);

        list_Threads.setLayoutX(50);
        list_Threads.setLayoutY(100);
        list_Threads.setPrefSize(width - 100, height - 250);

        setupButton(button_Create, 50, height - 120);
        button_Create.setOnAction(_ -> ControllerManageThreads.performCreate());

        setupButton(button_Rename, 300, height - 120);
        button_Rename.setOnAction(_ -> ControllerManageThreads.performRename());

        setupButton(button_Delete, 550, height - 120);
        button_Delete.setOnAction(_ -> ControllerManageThreads.performDelete());
        
        setupButton(button_Return, 300, height - 50);
        button_Return.setOnAction((_) -> ControllerManageThreads.performReturn());

        setupButton(button_Quit, 550, height - 50);
        button_Quit.setOnAction((_) -> ControllerManageThreads.performQuit());

        theRootPane.getChildren().addAll(
            label_Title,
            list_Threads,
            button_Create,
            button_Rename,
            button_Delete,
            button_Return,
            button_Quit
        );
    }

    /**********
	 * <p> Method: populateThreadList() </p>
	 * 
	 * <p> Description: this method fills the thread list with the names of each thread. </p>
	 * 
	 */
    public static void populateThreadList() {
        List<String> threads = thePostDatabase.getAllThreads();

        threads.removeIf(t -> t == null || t.trim().isEmpty());

        list_Threads.getItems().setAll(threads);
    }


    private static void setupButton(Button b, double x, double y) {
        b.setFont(Font.font("Dialog", 18));
        b.setMinWidth(210);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
}
