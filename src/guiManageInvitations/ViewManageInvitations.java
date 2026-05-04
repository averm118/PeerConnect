package guiManageInvitations;

// Import statements bring in JavaFX UI tools and app classes
import java.util.List;
import database.Database;
import entityClasses.User;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class ViewManageInvitations {

    // Window dimensions from main app settings
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    // Singleton pattern — ensures only one instance of this view exists
    private static ViewManageInvitations theView;

    // Shared database instance
    public static Database theDatabase = applicationMain.FoundationsMain.database;
    
    private static Timeline refreshTimer;
    // GUI state variables
    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;

    // Scene object representing this screen
    public static Scene theManageInvitationsScene = null;

    // UI elements
    protected static Label label_PageTitle = new Label("Outstanding Invitations");
    protected static Label label_UserDetails = new Label();
    protected static ListView<String> listView_Invitations = new ListView<>();
    
    protected static Button button_Return = new Button("Return");
    protected static Button button_Delete = new Button("Delete");
    protected static Button button_Quit = new Button("Quit");

    	
  
    /***************************************************************
     * displayManageInvitations
     *
     * Entry point for showing this screen.
     * Called by ControllerAdminHome.
     ***************************************************************/
    public static void displayManageInvitations(Stage ps, User user) {

        theStage = ps;
        theUser = user;

        // Ensure only one instance exists
        if (theView == null) theView = new ViewManageInvitations();

        // Set monospaced font for column alignment
        listView_Invitations.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 12;");

        // Load invitation data from database
        populateInvitationList();

        //refresh screen every section to show invitation timer counting 
        if(refreshTimer != null) refreshTimer.stop();
        refreshTimer = new Timeline(new KeyFrame(Duration.minutes(1), (_) -> populateInvitationList()));
        
        refreshTimer.setCycleCount(Timeline.INDEFINITE);
        refreshTimer.play();
        // Switch scene
        theStage.setScene(theManageInvitationsScene);
        theStage.show();
    }
    //stops the timer when you leave manage invitation screen
    protected static void stopTimer() {
    	if(refreshTimer != null) {
    		refreshTimer.stop();
    		refreshTimer = null;
    	}
    }
    /***************************************************************
     * Constructor builds all GUI components
     ***************************************************************/
    public ViewManageInvitations() {

        theRootPane = new Pane();
        theManageInvitationsScene = new Scene(theRootPane, width, height);

        // Page title formatting
        label_PageTitle.setFont(Font.font("Arial", 28));
        label_PageTitle.setMinWidth(width);
        label_PageTitle.setAlignment(Pos.CENTER);
        label_PageTitle.setLayoutX(0);
        label_PageTitle.setLayoutY(20);

        // Logged-in user info
        label_UserDetails.setFont(Font.font("Arial", 18));
        label_UserDetails.setLayoutX(20);
        label_UserDetails.setLayoutY(70);

        // Invitation list display
        listView_Invitations.setLayoutX(50);
        listView_Invitations.setLayoutY(120);
        listView_Invitations.setPrefSize(width - 100, height - 250);
        //listView_Invitations.setItems(invitationData);
        // Buttons
        setupButton(button_Return, 20, height - 100);
        button_Return.setOnAction((_) -> ControllerManageInvitations.performReturn());

        setupButton(button_Delete, 300, height - 100);
        button_Delete.setOnAction((_) -> ControllerManageInvitations.performDelete());

        setupButton(button_Quit, 580, height - 100);
        button_Quit.setOnAction((_) -> ControllerManageInvitations.performQuit());

        // Add all elements to pane
        theRootPane.getChildren().addAll(
            label_PageTitle,
            label_UserDetails,
            listView_Invitations,
            button_Return,
            button_Delete,
            button_Quit
        );
    }

    /***************************************************************
     * populateInvitationList
     *
     * Fetches invitation data from DB and updates ListView.
     ***************************************************************/
    public static void populateInvitationList() {
    	
    	
    	

        List<String> invitationList = theDatabase.getInvitationListEnriched();
        listView_Invitations.setItems(FXCollections.observableArrayList(invitationList));

       
        label_UserDetails.setText("Logged in as: " + theUser.getUserName());
        
    }

    /***************************************************************
     * setupButton
     *
     * Utility method to reduce repetition when configuring buttons.
     ***************************************************************/
    private static void setupButton(Button b, double x, double y) {

        b.setFont(Font.font("Dialog", 18));
        b.setMinWidth(210);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
}
