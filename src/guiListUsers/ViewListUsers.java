package guiListUsers;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import javafx.scene.control.ListView;

/*******
 * <p> Title: GUIListUsers Class. </p>
 * 
 * <p> Description: The Java/FX-based page for listing all users in database.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-20 Initial version
 *  
 */

public class ViewListUsers {
	//window dimensions
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    //gui instance
    private static ViewListUsers theView;
    
    //database reference
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    //more stuff
    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;

    //scene view
    public static Scene theListUsersScene = null;

    //gui elements
    protected static Label label_PageTitle = new Label("List of All Users");
    protected static Label label_UserDetails = new Label();
    protected static ListView<String> listView_Users = new ListView<>();
    protected static Button button_Return = new Button("Return");
    protected static Button button_Logout = new Button("Logout");
    protected static Button button_Quit = new Button("Quit");
    
    //main display function
    public static void displayListUsers(Stage ps, User user){
    	//stage and user references
        theStage = ps;
        theUser = user;

        //build the gui
        if (theView == null) theView = new ViewListUsers();
        listView_Users.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 12;");

        //refresh user list
        populateUserList();

        //display scene
        theStage.setScene(theListUsersScene);
        theStage.show();
    }

    //constructor
    public ViewListUsers(){
    	//setup pane and scene
        theRootPane = new Pane();
        theListUsersScene = new Scene(theRootPane, width, height);

        //title stuff
        label_PageTitle.setFont(Font.font("Arial", 28));
        label_PageTitle.setMinWidth(width);
        label_PageTitle.setAlignment(Pos.CENTER);
        label_PageTitle.setLayoutX(0);
        label_PageTitle.setLayoutY(20);

        //current user label stuff
        label_UserDetails.setFont(Font.font("Arial", 18));
        label_UserDetails.setMinWidth(width);
        label_UserDetails.setAlignment(Pos.BASELINE_LEFT);
        label_UserDetails.setLayoutX(20);
        label_UserDetails.setLayoutY(70);

        //list stuff
        listView_Users.setLayoutX(50);
        listView_Users.setLayoutY(120);
        listView_Users.setPrefSize(width - 100, height - 250);

        //return button stuff
        setupButton(button_Return, 20, height - 100);
        button_Return.setOnAction((_) -> { ControllerListUsers.performReturn(); });

        //logout button stuff
        setupButton(button_Logout, 300, height - 100);
        button_Logout.setOnAction((_) -> { ControllerListUsers.performLogout(); });

        //setup button shit
        setupButton(button_Quit, 580, height - 100);
        button_Quit.setOnAction((_) -> { ControllerListUsers.performQuit(); });

        //add stuff to pane
        theRootPane.getChildren().addAll(
            label_PageTitle,
            label_UserDetails,
            listView_Users,
            button_Return,
            button_Logout,
            button_Quit
        );
    }

    //function for populating user list
    private static void populateUserList(){
        List<String> userList = theDatabase.getUserListEnriched();
        listView_Users.setItems(FXCollections.observableArrayList(userList));
        label_UserDetails.setText("Logged in as: " + theUser.getUserName());
    }

    //function for button styling
    private static void setupButton(Button b, double x, double y){
        b.setFont(Font.font("Dialog", 18));
        b.setMinWidth(210);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
}