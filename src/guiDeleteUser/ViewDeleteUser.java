package guiDeleteUser;

import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;

public class ViewDeleteUser {
	//gui window sizing
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    //title and user details
    protected static Label label_PageTitle = new Label();
    protected static Label label_UserDetails = new Label();
    
    //user selection
    protected static Label label_SelectUser = new Label("Select a user to delete:");
    protected static ComboBox<String> combobox_SelectUser = new ComboBox<>();

    //deletion confirmation
    protected static Label label_ConfirmDelete = new Label("Press the button below to delete this user:");
    protected static Button button_DeleteUser = new Button("Delete User");

    protected static Line line_Separator1 = new Line(20, 200, width - 20, 200);

    //navigation buttons
    protected static Button button_Return = new Button("Return");
    protected static Button button_Logout = new Button("Logout");
    protected static Button button_Quit = new Button("Quit");

    //view instance
    private static ViewDeleteUser theView;
    //database reference
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;

    public static Scene theDeleteUserScene = null;
    protected static String theSelectedUser = "";

    //main display function
    public static void displayDeleteUser(Stage ps, User user){
        theStage = ps;
        theUser = user;

        if (theView == null) theView = new ViewDeleteUser();

        refreshUserList();
        combobox_SelectUser.getSelectionModel().select(0);

        ControllerDeleteUser.repaintTheWindow();
        ControllerDeleteUser.doSelectUser();
    }

    //gui constructor 
    public ViewDeleteUser(){
        theRootPane = new Pane();
        theDeleteUserScene = new Scene(theRootPane, width, height);

        //title
        label_PageTitle.setText("Delete User Page");
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

        //user identity
        label_UserDetails.setText("Admin: " + theUser.getUserName());
        setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

        //user selection
        setupLabelUI(label_SelectUser, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 130);
        setupComboBoxUI(combobox_SelectUser, "Dialog", 16, 250, 280, 125);

        combobox_SelectUser.getSelectionModel().selectedItemProperty().addListener(
                (@SuppressWarnings("unused") ObservableValue<? extends String> observable,
                 @SuppressWarnings("unused") String oldValue,
                 @SuppressWarnings("unused") String newValue) -> {
                    ControllerDeleteUser.doSelectUser();
                });

        //remove confirmation
        setupLabelUI(label_ConfirmDelete, "Arial", 20, 400, Pos.BASELINE_LEFT, 20, 250);
        setupButtonUI(button_DeleteUser, "Dialog", 18, 200, Pos.CENTER, 20, 300);
        button_DeleteUser.setOnAction((_) -> ControllerDeleteUser.performDeleteUser());

        //return button
        setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
        button_Return.setOnAction((_) -> ControllerDeleteUser.performReturn());
        //logout button
        setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
        button_Logout.setOnAction((_) -> ControllerDeleteUser.performLogout());
        //quit button
        setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
        button_Quit.setOnAction((_) -> ControllerDeleteUser.performQuit());
    }

    //function for reseting list of users
    protected static void refreshUserList() {
        List<String> userList = theDatabase.getUserList();
        combobox_SelectUser.setItems(FXCollections.observableArrayList(userList));
    }

    //function for setting up labels
    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    //function for setting up buttons
    protected static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }

    //function for setting up combo boxes
    protected static void setupComboBoxUI(ComboBox<String> c, String ff, double f, double w, double x, double y) {
        c.setStyle("-fx-font: " + f + " " + ff + ";");
        c.setMinWidth(w);
        c.setLayoutX(x);
        c.setLayoutY(y);
    }
}