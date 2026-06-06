package guiViewEvaluations;

import java.util.List;

import database.FeedbackDatabase;
import entityClasses.Feedback;
import entityClasses.User;
import guiCommon.ActionSpec;
import guiCommon.PeerConnectShell;
import guiCommon.ScreenSpec;
import guiCommon.UiFactory;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewViewEvaluations {
    private static ViewViewEvaluations theView;
    public static FeedbackDatabase theFeedbackDatabase = applicationMain.FoundationsMain.feedbackDatabase;

    protected static Stage theStage;
    protected static User theUser;
    protected static Scene theScene;

    protected static Label label_PageTitle = new Label("View Evaluations");
    protected static Label label_UserDetails = new Label();
    protected static ListView<String> listView_Evaluations = new ListView<>();
    protected static TextArea area_Details = new TextArea();
    protected static Button button_Return = UiFactory.action(
            ActionSpec.of("Return", "bi-arrow-left", ControllerViewEvaluations::performReturn,
                    "pc-button-secondary"));
    protected static Button button_Quit = UiFactory.action(
            ActionSpec.of("Quit", "bi-x", ControllerViewEvaluations::performQuit,
                    "pc-button-secondary"));

    private static List<Feedback> currentEvaluations;

    public static void displayViewEvaluations(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theView == null) {
            theView = new ViewViewEvaluations();
        }

        label_UserDetails.setText("Logged in as " + theUser.getUserName());
        area_Details.clear();
        currentEvaluations = theFeedbackDatabase.getFeedbackByRecipientAndType(
                theUser.getUserName(), "THREAD_EVALUATION");

        java.util.List<String> items = new java.util.ArrayList<>();
        for (Feedback f : currentEvaluations) {
            String threadName = extractThreadName(f.getBody());
            items.add("Thread: " + threadName + " | Evaluation ID: " + f.getId());
        }
        listView_Evaluations.setItems(FXCollections.observableArrayList(items));

        PeerConnectShell.show(theStage, theScene, "PeerConnect: Evaluations");
    }

    private static String extractThreadName(String body) {
        if (body == null) {
            return "Unknown";
        }
        String marker = "Thread: ";
        int start = body.indexOf(marker);
        if (start == -1) {
            return "Unknown";
        }
        start += marker.length();
        int end = body.indexOf("\n", start);
        if (end == -1) {
            end = body.length();
        }
        return body.substring(start, end).trim();
    }

    public ViewViewEvaluations() {
        label_PageTitle.getStyleClass().add("pc-heading");
        label_UserDetails.getStyleClass().add("pc-body");
        UiFactory.prepareList(listView_Evaluations, "Evaluation list");
        UiFactory.prepareTextArea(area_Details, "Select an evaluation to read the details.");
        area_Details.setEditable(false);

        listView_Evaluations.setOnMouseClicked(_ -> {
            int index = listView_Evaluations.getSelectionModel().getSelectedIndex();
            if (index >= 0 && index < currentEvaluations.size()) {
                area_Details.setText(currentEvaluations.get(index).getBody());
            }
        });

        VBox listCard = UiFactory.card(UiFactory.section("Evaluations", listView_Evaluations));
        VBox detailsCard = UiFactory.card(UiFactory.section("Details", area_Details));
        VBox.setVgrow(listView_Evaluations, Priority.ALWAYS);
        VBox.setVgrow(area_Details, Priority.ALWAYS);

        SplitPane split = new SplitPane(listCard, detailsCard);
        split.setDividerPositions(0.36);
        VBox.setVgrow(split, Priority.ALWAYS);

        HBox footer = UiFactory.actions(UiFactory.spacer(), button_Return, button_Quit);
        VBox screen = new VBox(18, UiFactory.card(label_PageTitle, label_UserDetails), split, footer);
        screen.getStyleClass().add("pc-screen");
        screen.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(split, Priority.ALWAYS);

        theScene = PeerConnectShell.scene(
                ScreenSpec.of("Evaluations", "Review saved discussion evaluations and feedback.",
                        theUser, "Student", "bi-clipboard-check"),
                screen);
    }
}
