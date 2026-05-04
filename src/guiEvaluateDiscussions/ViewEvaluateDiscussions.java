package guiEvaluateDiscussions;

import applicationMain.FoundationsMain;
import database.FeedbackDatabase;
import database.PostDatabase;
import database.ReplyDatabase;
import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * This class displays the Staff evaluation page.
 *
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */

public class ViewEvaluateDiscussions {

	private static double width = FoundationsMain.WINDOW_WIDTH;
	private static double height = FoundationsMain.WINDOW_HEIGHT;

	private static ViewEvaluateDiscussions theView;

	public static PostDatabase thePostDatabase = applicationMain.FoundationsMain.postDatabase;
	public static ReplyDatabase theReplyDatabase = applicationMain.FoundationsMain.replyDatabase;
	public static FeedbackDatabase theFeedbackDatabase = applicationMain.FoundationsMain.feedbackDatabase;

	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;
	protected static Scene theScene;
	// Page title and status labels
	protected static Label label_PageTitle = new Label("Evaluate Discussions");
	protected static Label label_UserDetails = new Label();
	protected static Label label_Status = new Label();
	// Labels for staff input fields.
	protected static Label label_Student = new Label("Student Username:");
	protected static Label label_Thread = new Label("Thread:");
	protected static Label label_MinPostWords = new Label("Min Post Words:");
	protected static Label label_MinReplyWords = new Label("Min Reply Words:");
	protected static Label label_MinReplies = new Label("Min Replies:");
	protected static Label label_Grade = new Label("Grade:");
	protected static Label label_Feedback = new Label("Feedback:");
	protected static Label label_Result = new Label("Evaluation Result:");
	// Labels showing the student's posts and replies
	protected static Label label_PostsMade = new Label("Student Posts:");
	protected static Label label_RepliesMade = new Label("Student Replies:");
	// Text areas used to display student work
	protected static TextArea area_PostsMade = new TextArea();
	protected static TextArea area_RepliesMade = new TextArea();
	// Input fields for staff evaluation settings
	protected static TextField text_Student = new TextField();
	protected static TextField text_Thread = new TextField();
	protected static TextField text_MinPostWords = new TextField("5");
	protected static TextField text_MinReplyWords = new TextField("4");
	protected static TextField text_MinReplies = new TextField("2");
	protected static TextField text_Grade = new TextField();
	// Text areas for evaluation result and feedback
	protected static TextArea area_Result = new TextArea();
	protected static TextArea area_Feedback = new TextArea();
	// Buttons on pahe
	protected static Button button_Evaluate = new Button("Evaluate");
	protected static Button button_Save = new Button("Save Evaluation");
	protected static Button button_Return = new Button("Return");
	protected static Button button_Quit = new Button("Quit");
	
	/***
	 * 
	 * Prepares and displays the Evaluate Discussions page
	 * for the given staff user. This method resets all input and output fields
	 * so that a new discussion evaluation can be performed
	 * 
	 * @param ps the stage where the page will be displayed
	 * @param user the currently logged-in staff user
	 */


	public static void displayEvaluateDiscussions(Stage ps, User user) {
		theStage = ps;
		theUser = user;

		if (theView == null) theView = new ViewEvaluateDiscussions();
		// Reset all fields so staff starts with a clean evaluation screen
		label_UserDetails.setText("User: " + theUser.getUserName());
		text_Student.clear();
		text_Thread.clear();
		text_MinPostWords.setText("5");
		text_MinReplyWords.setText("4");
		text_MinReplies.setText("2");
		text_Grade.clear();
		area_Result.clear();
		area_Feedback.clear();
		label_Status.setText("");
		label_Status.setText("");
		area_PostsMade.clear();
		area_RepliesMade.clear();

		theStage.setScene(theScene);
		theStage.show();
	}

	public ViewEvaluateDiscussions() {
		theRootPane = new Pane();
		theScene = new Scene(theRootPane, width, height);
		// Set up the page title
		label_PageTitle.setFont(Font.font("Arial", 28));
		label_PageTitle.setMinWidth(width);
		label_PageTitle.setAlignment(Pos.CENTER);
		label_PageTitle.setLayoutX(0);
		label_PageTitle.setLayoutY(20);

		label_UserDetails.setFont(Font.font("Arial", 18));
		label_UserDetails.setLayoutX(20);
		label_UserDetails.setLayoutY(20);
		// Student username input
		label_Student.setLayoutX(20);
		label_Student.setLayoutY(80);
		text_Student.setLayoutX(160);
		text_Student.setLayoutY(75);
		text_Student.setPrefWidth(200);
		// Thread name
		label_Thread.setLayoutX(20);
		label_Thread.setLayoutY(120);
		text_Thread.setLayoutX(160);
		text_Thread.setLayoutY(115);
		text_Thread.setPrefWidth(200);
		//Minimum post word count
		label_MinPostWords.setLayoutX(20);
		label_MinPostWords.setLayoutY(160);
		text_MinPostWords.setLayoutX(160);
		text_MinPostWords.setLayoutY(155);
		text_MinPostWords.setPrefWidth(80);
		// Minimum reply word count
		label_MinReplyWords.setLayoutX(20);
		label_MinReplyWords.setLayoutY(200);
		text_MinReplyWords.setLayoutX(160);
		text_MinReplyWords.setLayoutY(195);
		text_MinReplyWords.setPrefWidth(80);
		// Minimum replies
		label_MinReplies.setLayoutX(20);
		label_MinReplies.setLayoutY(240);
		text_MinReplies.setLayoutX(160);
		text_MinReplies.setLayoutY(235);
		text_MinReplies.setPrefWidth(80);
		// Button to run the discussion evaluation
		button_Evaluate.setLayoutX(20);
		button_Evaluate.setLayoutY(280);
		button_Evaluate.setOnAction((_) -> {
			ControllerEvaluateDiscussions.performEvaluate();
		});
		// Area showing the evaluation summary and suggested grade
		label_Result.setLayoutX(420);
		label_Result.setLayoutY(80);
		area_Result.setLayoutX(420);
		area_Result.setLayoutY(105);
		area_Result.setPrefWidth(320);
		area_Result.setPrefHeight(220);
		area_Result.setWrapText(true);
		area_Result.setEditable(false);
		// Grade field shown after evaluation
		label_Grade.setLayoutX(20);
		label_Grade.setLayoutY(340);
		text_Grade.setLayoutX(160);
		text_Grade.setLayoutY(335);
		text_Grade.setPrefWidth(80);

		label_Feedback.setLayoutX(20);
		label_Feedback.setLayoutY(380);
		area_Feedback.setLayoutX(20);
		area_Feedback.setLayoutY(405);
		area_Feedback.setPrefWidth(360);
		area_Feedback.setPrefHeight(120);
		area_Feedback.setWrapText(true);
		
		label_PostsMade.setLayoutX(420);
		label_PostsMade.setLayoutY(340);

		area_PostsMade.setLayoutX(420);
		area_PostsMade.setLayoutY(365);
		area_PostsMade.setPrefWidth(320);
		area_PostsMade.setPrefHeight(100);
		area_PostsMade.setWrapText(true);
		area_PostsMade.setEditable(false);

		label_RepliesMade.setLayoutX(420);
		label_RepliesMade.setLayoutY(475);

		area_RepliesMade.setLayoutX(420);
		area_RepliesMade.setLayoutY(500);
		area_RepliesMade.setPrefWidth(320);
		area_RepliesMade.setPrefHeight(50);
		area_RepliesMade.setWrapText(true);
		area_RepliesMade.setEditable(false);
		// Button to save the completed evaluation and feedback
		button_Save.setLayoutX(20);
		button_Save.setLayoutY(550);
		button_Save.setOnAction((_) -> {ControllerEvaluateDiscussions.performSave();});

		button_Return.setLayoutX(520);
		button_Return.setLayoutY(550);
		button_Return.setOnAction((_) -> {ControllerEvaluateDiscussions.performReturn();});

		button_Quit.setLayoutX(640);
		button_Quit.setLayoutY(550);
		button_Quit.setOnAction((_) -> {ControllerEvaluateDiscussions.performQuit();});
		// Status label used to show messages to staff
		label_Status.setLayoutX(160);
		label_Status.setLayoutY(555);

		theRootPane.getChildren().addAll(
				label_PageTitle, label_UserDetails, label_Status,
				label_Student, text_Student,
				label_Thread, text_Thread,
				label_MinPostWords, text_MinPostWords,
				label_MinReplyWords, text_MinReplyWords,
				label_MinReplies, text_MinReplies,
				label_PostsMade, area_PostsMade,
				label_RepliesMade, area_RepliesMade,
				button_Evaluate,
				label_Result, area_Result,
				label_Grade, text_Grade,
				label_Feedback, area_Feedback,
				button_Save, button_Return, button_Quit
				
		);
	}
}