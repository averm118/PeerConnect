package guiAdminRequestManagement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.Database;
import entityClasses.AdminRequest;
import entityClasses.User;
import guiCommon.ActionSpec;
import guiCommon.PeerConnectShell;
import guiCommon.ScreenSpec;
import guiCommon.UiFactory;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Shared request-management page used by staff and admins.
 *
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */
public class ViewAdminRequestManagement {
    private static ViewAdminRequestManagement theView;

    public static Database theDatabase = applicationMain.FoundationsMain.database;

    protected static Stage theStage;
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

    protected static Button button_CreateRequest = UiFactory.action(
            ActionSpec.of("Create Request", "bi-plus",
                    ControllerAdminRequestManagement::performCreateRequest));
    protected static Button button_CloseRequest = UiFactory.action(
            ActionSpec.of("Close Open", "bi-check2-circle",
                    ControllerAdminRequestManagement::performCloseRequest,
                    "pc-button-danger"));
    protected static Button button_ReopenRequest = UiFactory.action(
            ActionSpec.of("Reopen Closed", "bi-arrow-counterclockwise",
                    ControllerAdminRequestManagement::performReopenRequest,
                    "pc-button-secondary"));
    protected static Button button_UpdateReopenedRequest = UiFactory.action(
            ActionSpec.of("Update Reopened", "bi-pencil-square",
                    ControllerAdminRequestManagement::performUpdateReopenedRequest,
                    "pc-button-secondary"));
    protected static Button button_Return = UiFactory.action(
            ActionSpec.of("Return", "bi-arrow-left",
                    ControllerAdminRequestManagement::performReturn,
                    "pc-button-secondary"));
    protected static Button button_Quit = UiFactory.action(
            ActionSpec.of("Quit", "bi-x",
                    ControllerAdminRequestManagement::performQuit,
                    "pc-button-secondary"));

    /** Displays this page. */
    public static void displayAdminRequestManagement(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theView == null) {
            theView = new ViewAdminRequestManagement();
        }

        label_UserDetails.setText("Logged in as " + theUser.getUserName());
        refreshLists();
        clearEditor();
        configureButtonsForRole();

        PeerConnectShell.show(theStage, theScene, "PeerConnect: Admin Requests");
    }

    /** Singleton constructor. */
    private ViewAdminRequestManagement() {
        label_PageTitle.getStyleClass().add("pc-heading");
        label_UserDetails.getStyleClass().add("pc-body");
        label_OpenRequests.getStyleClass().add("pc-section-title");
        label_ClosedRequests.getStyleClass().add("pc-section-title");
        label_Editor.getStyleClass().add("pc-section-title");
        label_LinkInfo.getStyleClass().add("pc-caption");

        UiFactory.prepareList(listView_OpenRequests, "Open admin requests");
        UiFactory.prepareList(listView_ClosedRequests, "Closed admin requests");
        UiFactory.prepareTextArea(text_RequestDescription,
                "Write a request, closure comment, or reopened-request update.");

        listView_OpenRequests.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                listView_ClosedRequests.getSelectionModel().clearSelection();
                loadSelectedRequestIntoEditor();
            }
        });
        listView_ClosedRequests.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                listView_OpenRequests.getSelectionModel().clearSelection();
                loadSelectedRequestIntoEditor();
            }
        });

        VBox openColumn = UiFactory.section("Open requests", listView_OpenRequests);
        VBox closedColumn = UiFactory.section("Closed requests", listView_ClosedRequests);
        HBox requestColumns = new HBox(18, UiFactory.card(openColumn), UiFactory.card(closedColumn));
        HBox.setHgrow(requestColumns.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(requestColumns.getChildren().get(1), Priority.ALWAYS);
        VBox.setVgrow(listView_OpenRequests, Priority.ALWAYS);
        VBox.setVgrow(listView_ClosedRequests, Priority.ALWAYS);

        VBox editorCard = UiFactory.card(
                UiFactory.section("Request workspace", text_RequestDescription),
                label_LinkInfo,
                UiFactory.actions(button_CreateRequest, button_ReopenRequest,
                        button_UpdateReopenedRequest, UiFactory.spacer(), button_CloseRequest));

        HBox footer = UiFactory.actions(button_Return, UiFactory.spacer(), button_Quit);
        VBox screen = new VBox(18,
                UiFactory.card(label_PageTitle, label_UserDetails),
                requestColumns,
                editorCard,
                footer);
        screen.getStyleClass().add("pc-screen");
        screen.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(requestColumns, Priority.ALWAYS);

        theScene = PeerConnectShell.scene(
                ScreenSpec.of("Admin Requests",
                        "Triage open requests, reopen closed issues, and keep moderation work visible.",
                        theUser, roleLabel(), "bi-kanban"),
                screen);
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
        if (selected == null) {
            return null;
        }
        return openRequestMap.get(selected);
    }

    /** Gets the selected closed request object. */
    public static AdminRequest getSelectedClosedRequest() {
        String selected = listView_ClosedRequests.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return null;
        }
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

    private static String roleLabel() {
        return theUser != null && theUser.getAdminRole() ? "Admin" : "Staff";
    }
}
