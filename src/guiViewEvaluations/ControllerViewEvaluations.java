package guiViewEvaluations;

/*******
 * <p> Title: ControllerViewEvaluations Class. </p>
 * 
 * <p> Description: This class controls actions on the View Evaluations page.
 * It allows the student to return to the student home page or exit the program.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 * 
 */
public class ControllerViewEvaluations {

	public ControllerViewEvaluations() {}

	/**
	 * This method returns the student to the Student Home page.
	 */

	protected static void performReturn() {
		guiStudentHome.ViewStudentHome.displayStudentHome(
				ViewViewEvaluations.theStage,
				ViewViewEvaluations.theUser
		);
	}

	protected static void performQuit() {
		System.exit(0);
	}
}