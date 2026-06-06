package guiEvaluateDiscussions;

import applicationMain.FoundationsMain;
import database.FeedbackDatabase;
import database.PostDatabase;
import database.ReplyDatabase;
import entityClasses.User;
import guiCommon.ActionSpec;
import guiCommon.PeerConnectShell;
import guiCommon.ScreenSpec;
import guiCommon.UiFactory;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Displays the staff evaluation page.
 *
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */
public class ViewEvaluateDiscussions {
    private static ViewEvaluateDiscussions theView;

    public static PostDatabase thePostDatabase = FoundationsMain.postDatabase;
    public static ReplyDatabase theReplyDatabase = FoundationsMain.replyDatabase;
    public static FeedbackDatabase theFeedbackDatabase = FoundationsMain.feedbackDatabase;

    protected static Stage theStage;
    protected static User theUser;
    protected static Scene theScene;

    protected static Label label_PageTitle = new Label("Evaluate Discussions");
    protected static Label label_UserDetails = new Label();
    protected static Label label_Status = new Label();
    protected static Label label_Student = new Label("Student Username:");
    protected static Label label_Thread = new Label("Thread:");
    protected static Label label_MinPostWords = new Label("Min Post Words:");
    protected static Label label_MinReplyWords = new Label("Min Reply Words:");
    protected static Label label_MinReplies = new Label("Min Replies:");
    protected static Label label_Grade = new Label("Grade:");
    protected static Label label_Feedback = new Label("Feedback:");
    protected static Label label_Result = new Label("Evaluation Result:");
    protected static Label label_PostsMade = new Label("Student Posts:");
    protected static Label label_RepliesMade = new Label("Student Replies:");

    protected static TextArea area_PostsMade = new TextArea();
    protected static TextArea area_RepliesMade = new TextArea();
    protected static TextField text_Student = new TextField();
    protected static TextField text_Thread = new TextField();
    protected static TextField text_MinPostWords = new TextField("5");
    protected static TextField text_MinReplyWords = new TextField("4");
    protected static TextField text_MinReplies = new TextField("2");
    protected static TextField text_Grade = new TextField();
    protected static TextArea area_Result = new TextArea();
    protected static TextArea area_Feedback = new TextArea();

    protected static Button button_Evaluate = UiFactory.action(
            ActionSpec.of("Evaluate", "bi-clipboard-check",
                    ControllerEvaluateDiscussions::performEvaluate));
    protected static Button button_Save = UiFactory.action(
            ActionSpec.of("Save Evaluation", "bi-save",
                    ControllerEvaluateDiscussions::performSave));
    protected static Button button_Return = UiFactory.action(
            ActionSpec.of("Return", "bi-arrow-left",
                    ControllerEvaluateDiscussions::performReturn,
                    "pc-button-secondary"));
    protected static Button button_Quit = UiFactory.action(
            ActionSpec.of("Quit", "bi-x",
                    ControllerEvaluateDiscussions::performQuit,
                    "pc-button-secondary"));

    public static void displayEvaluateDiscussions(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theView == null) {
            theView = new ViewEvaluateDiscussions();
        }

        label_UserDetails.setText("Staff reviewer: " + theUser.getUserName());
        text_Student.clear();
        text_Thread.clear();
        text_MinPostWords.setText("5");
        text_MinReplyWords.setText("4");
        text_MinReplies.setText("2");
        text_Grade.clear();
        area_Result.clear();
        area_Feedback.clear();
        label_Status.setText("");
        area_PostsMade.clear();
        area_RepliesMade.clear();

        PeerConnectShell.show(theStage, theScene, "PeerConnect: Evaluate Discussions");
    }

    public ViewEvaluateDiscussions() {
        label_PageTitle.getStyleClass().add("pc-heading");
        label_UserDetails.getStyleClass().add("pc-body");
        label_Status.getStyleClass().add("pc-caption");
        label_Result.getStyleClass().add("pc-section-title");
        label_PostsMade.getStyleClass().add("pc-section-title");
        label_RepliesMade.getStyleClass().add("pc-section-title");

        UiFactory.prepareInput(text_Student, "student username");
        UiFactory.prepareInput(text_Thread, "thread name");
        UiFactory.prepareInput(text_MinPostWords, "minimum words");
        UiFactory.prepareInput(text_MinReplyWords, "minimum words");
        UiFactory.prepareInput(text_MinReplies, "minimum replies");
        UiFactory.prepareInput(text_Grade, "grade");
        UiFactory.prepareTextArea(area_Result, "Evaluation result");
        UiFactory.prepareTextArea(area_Feedback, "Feedback for the student");
        UiFactory.prepareTextArea(area_PostsMade, "Student posts");
        UiFactory.prepareTextArea(area_RepliesMade, "Student replies");
        area_Result.setEditable(false);
        area_PostsMade.setEditable(false);
        area_RepliesMade.setEditable(false);

        GridPane criteria = UiFactory.formGrid();
        UiFactory.formRow(criteria, 0, "Student", text_Student);
        UiFactory.formRow(criteria, 1, "Thread", text_Thread);
        UiFactory.formRow(criteria, 2, "Min post words", text_MinPostWords);
        UiFactory.formRow(criteria, 3, "Min reply words", text_MinReplyWords);
        UiFactory.formRow(criteria, 4, "Min replies", text_MinReplies);
        UiFactory.formRow(criteria, 5, "Grade", text_Grade);

        VBox criteriaCard = UiFactory.card(
                UiFactory.section("Evaluation criteria", criteria),
                UiFactory.actions(button_Evaluate));

        VBox resultCard = UiFactory.card(UiFactory.section("Result", area_Result));
        VBox feedbackCard = UiFactory.card(UiFactory.section("Feedback", area_Feedback));

        VBox evidenceCard = UiFactory.card(
                UiFactory.section("Student posts", area_PostsMade),
                UiFactory.section("Student replies", area_RepliesMade));
        VBox.setVgrow(area_PostsMade, Priority.ALWAYS);
        VBox.setVgrow(area_RepliesMade, Priority.ALWAYS);

        VBox leftColumn = new VBox(18, criteriaCard, feedbackCard);
        VBox rightColumn = new VBox(18, resultCard, evidenceCard);
        HBox workspace = new HBox(18, leftColumn, rightColumn);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);
        HBox.setHgrow(rightColumn, Priority.ALWAYS);
        VBox.setVgrow(evidenceCard, Priority.ALWAYS);
        VBox.setVgrow(workspace, Priority.ALWAYS);

        HBox footer = UiFactory.actions(button_Save, label_Status, UiFactory.spacer(), button_Return, button_Quit);
        VBox screen = new VBox(18, UiFactory.card(label_PageTitle, label_UserDetails), workspace, footer);
        screen.getStyleClass().add("pc-screen");
        screen.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(workspace, Priority.ALWAYS);

        theScene = PeerConnectShell.scene(
                ScreenSpec.of("Evaluate Discussions",
                        "Review student participation with evidence, feedback, and a saved grade.",
                        theUser, "Staff", "bi-clipboard-check"),
                screen);
    }
}
