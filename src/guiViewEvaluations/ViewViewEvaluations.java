package guiViewEvaluations;

import java.util.List;

import applicationMain.FoundationsMain;
import database.FeedbackDatabase;
import entityClasses.Feedback;
import entityClasses.User;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
/**
 * This class displays the evaluation page to students.
 *
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */
public class ViewViewEvaluations {

	private static double width = FoundationsMain.WINDOW_WIDTH;
	private static double height = FoundationsMain.WINDOW_HEIGHT;

	private static ViewViewEvaluations theView;

	public static FeedbackDatabase theFeedbackDatabase = applicationMain.FoundationsMain.feedbackDatabase;

	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;
	protected static Scene theScene;

	protected static Label label_PageTitle = new Label("View Evaluations");
	protected static Label label_UserDetails = new Label();
	protected static Label label_Evaluations = new Label("Evaluated Threads:");
	protected static Label label_Details = new Label("Evaluation Details:");
	// List of evaluations and details display
	protected static ListView<String> listView_Evaluations = new ListView<>();
	protected static TextArea area_Details = new TextArea();

	protected static Button button_Return = new Button("Return");
	protected static Button button_Quit = new Button("Quit");
	// Stores evaluation objects currently shown
	private static List<Feedback> currentEvaluations;
	
	/**
	 * Displays the View Evaluations page for the current student.
	 * Loads all thread evaluations assigned to the student.
	 * 
	 * @param ps the stage where the page is displayed
	 * @param user the currently logged-in student
	 */

	public static void displayViewEvaluations(Stage ps, User user) {
		theStage = ps;
		theUser = user;

		if (theView == null) theView = new ViewViewEvaluations();

		label_UserDetails.setText("User: " + theUser.getUserName());
		area_Details.clear();
		// Load evaluations for this student
		currentEvaluations = theFeedbackDatabase.getFeedbackByRecipientAndType(
				theUser.getUserName(),
				"THREAD_EVALUATION"
		);

		java.util.List<String> items = new java.util.ArrayList<>();

		for (Feedback f : currentEvaluations) {
			String body = f.getBody();
			// Extract thread name from feedback text
			String threadName = extractThreadName(body);
			items.add("Thread: " + threadName + "  |  Evaluation ID: " + f.getId());
		}
		// Display evaluations in list view
		listView_Evaluations.setItems(FXCollections.observableArrayList(items));

		theStage.setScene(theScene);
		theStage.show();
	}

	/**
	 * Extracts the thread name from the feedback body text.
	 * 
	 * @param body the feedback text
	 * @return the thread name or "Unknown" if not found
	 */

	private static String extractThreadName(String body) {
		if (body == null) return "Unknown";

		String marker = "Thread: ";
		int start = body.indexOf(marker);
		if (start == -1) return "Unknown";

		start += marker.length();
		int end = body.indexOf("\n", start);
		if (end == -1) end = body.length();

		return body.substring(start, end).trim();
	}
	/**
	 * builds the View Evaluations page layout
	 */

	public ViewViewEvaluations() {
		theRootPane = new Pane();
		theScene = new Scene(theRootPane, width, height);
		// Page title formatting
		label_PageTitle.setFont(Font.font("Arial", 28));
		label_PageTitle.setMinWidth(width);
		label_PageTitle.setAlignment(Pos.CENTER);
		label_PageTitle.setLayoutX(0);
		label_PageTitle.setLayoutY(20);
		// User label formatting
		label_UserDetails.setFont(Font.font("Arial", 18));
		label_UserDetails.setLayoutX(20);
		label_UserDetails.setLayoutY(20);
		// Evaluation list label
		label_Evaluations.setLayoutX(20);
		label_Evaluations.setLayoutY(80);
		// List showing evaluated threads
		listView_Evaluations.setLayoutX(20);
		listView_Evaluations.setLayoutY(105);
		listView_Evaluations.setPrefWidth(300);
		listView_Evaluations.setPrefHeight(420);

		label_Details.setLayoutX(360);
		label_Details.setLayoutY(80);
		// Area showing full evaluation details
		area_Details.setLayoutX(360);
		area_Details.setLayoutY(105);
		area_Details.setPrefWidth(390);
		area_Details.setPrefHeight(420);
		area_Details.setWrapText(true);
		area_Details.setEditable(false);
		// When a thread is selected, show its full feedback
		listView_Evaluations.setOnMouseClicked((_) -> {
			int index = listView_Evaluations.getSelectionModel().getSelectedIndex();
			if (index >= 0 && index < currentEvaluations.size()) {
				area_Details.setText(currentEvaluations.get(index).getBody());
			}
		});

		button_Return.setLayoutX(360);
		button_Return.setLayoutY(550);
		button_Return.setOnAction((_) -> {
			ControllerViewEvaluations.performReturn();
		});

		button_Quit.setLayoutX(500);
		button_Quit.setLayoutY(550);
		button_Quit.setOnAction((_) -> {
			ControllerViewEvaluations.performQuit();
		});

		theRootPane.getChildren().addAll(
				label_PageTitle,
				label_UserDetails,
				label_Evaluations,
				listView_Evaluations,
				label_Details,
				area_Details,
				button_Return,
				button_Quit
		);
	}
}