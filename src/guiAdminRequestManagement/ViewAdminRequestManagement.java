package guiAdminRequestManagement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.Database;
import entityClasses.AdminRequest;
import entityClasses.User;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Shared request-management page used by staff and admins.
 *
 * The left list shows open requests that are shared between staff and admins.
 * The right list shows closed requests maintained by the system. Staff can reopen
 * closed requests and update the description of the reopened request. Admins can
 * close open requests after handling them.
 *
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */
public class ViewAdminRequestManagement {

    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    private static ViewAdminRequestManagement theView;

    public static Database theDatabase = applicationMain.FoundationsMain.database;

    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;
    public static Scene theScene = null;

    private static final Map<String, AdminRequest> openRequestMap = new HashMap<>();
    private static final Map<String, AdminRequest> closedRequestMap = new HashMap<>();

    protected static Label label_PageTitle = new Label("Admin Request Management");
    protected static Label label_UserDetails = new Label();
    protected static Label label_OpenRequests = new Label("Shared Open Requests");
    protected static Label label_ClosedRequests = new Label("Closed Requests");
    protected static Label label_Editor = new Label("Request Description / Update Area");
    protected static Label label_LinkInfo = new Label("Linked Original Request: none");

    protected static ListView<String> listView_OpenRequests = new ListView<>();
    protected static ListView<String> listView_ClosedRequests = new ListView<>();
    protected static TextArea text_RequestDescription = new TextArea();

    protected static Button button_CreateRequest = new Button("Create Request");
    protected static Button button_CloseRequest = new Button("Close Selected Open Request");
    protected static Button button_ReopenRequest = new Button("Reopen Selected Closed Request");
    protected static Button button_UpdateReopenedRequest = new Button("Update Reopened Request");
    protected static Button button_Return = new Button("Return");
    protected static Button button_Quit = new Button("Quit");

    /** Displays this page. */
    public static void displayAdminRequestManagement(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theView == null) {
            theView = new ViewAdminRequestManagement();
        }

        label_UserDetails.setText("Logged in as: " + theUser.getUserName());
        refreshLists();
        clearEditor();
        configureButtonsForRole();

        theStage.setTitle("CSE 360 Foundations: Admin Request Management");
        theStage.setScene(theScene);
        theStage.show();
    }

    /** Singleton constructor. */
    private ViewAdminRequestManagement() {
        theRootPane = new Pane();
        theScene = new Scene(theRootPane, width, height);

        setupLabel(label_PageTitle, 28, width, Pos.CENTER, 0, 10);
        setupLabel(label_UserDetails, 18, 300, Pos.BASELINE_LEFT, 20, 55);
        setupLabel(label_OpenRequests, 18, 250, Pos.BASELINE_LEFT, 20, 95);
        setupLabel(label_ClosedRequests, 18, 250, Pos.BASELINE_LEFT, 420, 95);
        setupLabel(label_Editor, 18, 350, Pos.BASELINE_LEFT, 20, 360);
        setupLabel(label_LinkInfo, 16, width - 40, Pos.BASELINE_LEFT, 20, 520);

        listView_OpenRequests.setLayoutX(20);
        listView_OpenRequests.setLayoutY(125);
        listView_OpenRequests.setPrefSize(360, 220);
        listView_OpenRequests.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 12;");
        listView_OpenRequests.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                listView_ClosedRequests.getSelectionModel().clearSelection();
                loadSelectedRequestIntoEditor();
            }
        });

        listView_ClosedRequests.setLayoutX(420);
        listView_ClosedRequests.setLayoutY(125);
        listView_ClosedRequests.setPrefSize(360, 220);
        listView_ClosedRequests.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 12;");
        listView_ClosedRequests.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                listView_OpenRequests.getSelectionModel().clearSelection();
                loadSelectedRequestIntoEditor();
            }
        });

        text_RequestDescription.setLayoutX(20);
        text_RequestDescription.setLayoutY(390);
        text_RequestDescription.setPrefSize(760, 120);
        text_RequestDescription.setWrapText(true);

        setupButton(button_CreateRequest, 630, 520, 150);
        button_CreateRequest.setOnAction(e -> ControllerAdminRequestManagement.performCreateRequest());

        setupButton(button_CloseRequest, 20, 555, 200);
        button_CloseRequest.setOnAction(e -> ControllerAdminRequestManagement.performCloseRequest());

        setupButton(button_ReopenRequest, 300, 555, 210);
        button_ReopenRequest.setOnAction(e -> ControllerAdminRequestManagement.performReopenRequest());

        setupButton(button_UpdateReopenedRequest, 590, 555, 170);
        button_UpdateReopenedRequest.setOnAction(e -> ControllerAdminRequestManagement.performUpdateReopenedRequest());

        // Return / quit placed slightly above to avoid crowding.
        setupButton(button_Return, 420, 350, 120);
        button_Return.setOnAction(e -> ControllerAdminRequestManagement.performReturn());

        setupButton(button_Quit, 550, 350, 120);
        button_Quit.setOnAction(e -> ControllerAdminRequestManagement.performQuit());

        theRootPane.getChildren().addAll(
                label_PageTitle, label_UserDetails,
                label_OpenRequests, label_ClosedRequests, label_Editor, label_LinkInfo,
                listView_OpenRequests, listView_ClosedRequests,
                text_RequestDescription,
                button_CreateRequest, button_CloseRequest, button_ReopenRequest,
                button_UpdateReopenedRequest, button_Return, button_Quit);
    }

    /** Refreshes both lists from the database. */
    public static void refreshLists() {
        populateOpenRequests();
        populateClosedRequests();
        configureButtonsForRole();
    }

    /** Loads the selected request into the text area and link label. */
    public static void loadSelectedRequestIntoEditor() {
        AdminRequest request = getSelectedOpenRequest();
        if (request == null) {
            request = getSelectedClosedRequest();
        }

        if (request == null) {
            clearEditor();
            return;
        }

        text_RequestDescription.setText(request.getDescription());

        if (request.getOriginalRequestId() == null) {
            label_LinkInfo.setText("Linked Original Request: none");
        } else {
            label_LinkInfo.setText("Linked Original Request: #" + request.getOriginalRequestId());
        }
    }

    /** Selects an open request in the list by request id. */
    public static void selectOpenRequestById(int requestId) {
        for (String item : openRequestMap.keySet()) {
            AdminRequest request = openRequestMap.get(item);
            if (request != null && request.getRequestId() == requestId) {
                listView_OpenRequests.getSelectionModel().select(item);
                break;
            }
        }
    }

    /** Gets the selected open request object. */
    public static AdminRequest getSelectedOpenRequest() {
        String selected = listView_OpenRequests.getSelectionModel().getSelectedItem();
        if (selected == null) return null;
        return openRequestMap.get(selected);
    }

    /** Gets the selected closed request object. */
    public static AdminRequest getSelectedClosedRequest() {
        String selected = listView_ClosedRequests.getSelectionModel().getSelectedItem();
        if (selected == null) return null;
        return closedRequestMap.get(selected);
    }

    /** Shows an information dialog. */
    public static void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Admin Request Management");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /** Clears the editor area. */
    private static void clearEditor() {
        text_RequestDescription.clear();
        label_LinkInfo.setText("Linked Original Request: none");
    }

    /** Populates the open-request list. */
    private static void populateOpenRequests() {
        openRequestMap.clear();
        List<AdminRequest> requests = theDatabase.getOpenAdminRequests();
        List<String> rows = new java.util.ArrayList<>();

        for (AdminRequest request : requests) {
            String row = formatRow(request);
            rows.add(row);
            openRequestMap.put(row, request);
        }

        listView_OpenRequests.setItems(FXCollections.observableArrayList(rows));
    }

    /** Populates the closed-request list. */
    private static void populateClosedRequests() {
        closedRequestMap.clear();
        List<AdminRequest> requests = theDatabase.getClosedAdminRequests();
        List<String> rows = new java.util.ArrayList<>();

        for (AdminRequest request : requests) {
            String row = formatRow(request);
            rows.add(row);
            closedRequestMap.put(row, request);
        }

        listView_ClosedRequests.setItems(FXCollections.observableArrayList(rows));
    }

    /** Formats a request for the ListView. */
    private static String formatRow(AdminRequest request) {
        String description = request.getDescription();
        if (description.length() > 50) {
            description = description.substring(0, 50) + "...";
        }

        String linkedInfo = request.getOriginalRequestId() == null
                ? "orig:none"
                : "orig:#" + request.getOriginalRequestId();

        return String.format("#%-4d | %-10s | %-6s | %-10s | %s | %s",
                request.getRequestId(),
                request.getRequesterUserName(),
                request.getStatus(),
                request.getClosedBy() == null ? "-" : request.getClosedBy(),
                linkedInfo,
                description);
    }

    /** Enables or disables buttons based on the current role. */
    private static void configureButtonsForRole() {
        boolean isAdmin = theUser != null && theUser.getAdminRole();
        button_CloseRequest.setDisable(!isAdmin);
    }

    /** Shared label helper. */
    private static void setupLabel(Label label, double fontSize, double minWidth,
            Pos alignment, double x, double y) {
        label.setFont(Font.font("Arial", fontSize));
        label.setMinWidth(minWidth);
        label.setAlignment(alignment);
        label.setLayoutX(x);
        label.setLayoutY(y);
    }

    /** Shared button helper. */
    private static void setupButton(Button button, double x, double y, double minWidth) {
        button.setFont(Font.font("Dialog", 14));
        button.setMinWidth(minWidth);
        button.setLayoutX(x);
        button.setLayoutY(y);
    }
}
