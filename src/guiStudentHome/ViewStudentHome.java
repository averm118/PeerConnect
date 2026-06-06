package guiStudentHome;

import database.Database;
import entityClasses.User;
import guiCommon.ActionSpec;
import guiCommon.PeerConnectShell;
import guiCommon.ScreenSpec;
import guiCommon.UiFactory;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewStudentHome {
    protected static Label label_PageTitle = new Label();
    protected static Label label_UserDetails = new Label();
    protected static Button button_UpdateThisUser = UiFactory.action(
            ActionSpec.of("Account", "bi-person", ControllerStudentHome::performUpdate, "pc-button-secondary"));

    protected static Button button_GoToForum = UiFactory.action(
            ActionSpec.of("Open Forum", "bi-chat-dots", ControllerStudentHome::goToForum));
    protected static Button button_ViewEvaluations = UiFactory.action(
            ActionSpec.of("View Evaluations", "bi-clipboard-check", ControllerStudentHome::goToViewEvaluations));

    protected static Button button_Logout = UiFactory.action(
            ActionSpec.of("Logout", "bi-box-arrow-left", ControllerStudentHome::performLogout, "pc-button-secondary"));
    protected static Button button_Quit = UiFactory.action(
            ActionSpec.of("Quit", "bi-x", ControllerStudentHome::performQuit, "pc-button-secondary"));

    private static ViewStudentHome theView;
    private static Database theDatabase = applicationMain.FoundationsMain.database;
    protected static Stage theStage;
    protected static User theUser;
    private static Scene theStudentHomeScene;
    protected static final int theRole = 3;

    public static void displayStudentHome(Stage ps, User user) {
        theStage = ps;
        theUser = user;
        if (theView == null) {
            theView = new ViewStudentHome();
        }
        theDatabase.getUserAccountDetails(user.getUserName());
        applicationMain.FoundationsMain.activeHomePage = theRole;
        label_UserDetails.setText("Signed in as " + theUser.getUserName());
        PeerConnectShell.show(theStage, theStudentHomeScene, "PeerConnect: Student Home");
    }

    private ViewStudentHome() {
        label_PageTitle.setText("Student Home");
        label_PageTitle.getStyleClass().add("pc-heading");
        label_UserDetails.getStyleClass().add("pc-body");

        VBox forumCard = UiFactory.card(
                UiFactory.section("Discussion",
                        UiFactory.body("Jump into course threads, follow unread replies, and continue conversations."),
                        UiFactory.actions(button_GoToForum)));
        VBox feedbackCard = UiFactory.card(
                UiFactory.section("Feedback",
                        UiFactory.body("Review evaluations and private staff feedback when it becomes available."),
                        UiFactory.actions(button_ViewEvaluations)));

        HBox cards = new HBox(18, forumCard, feedbackCard);
        HBox.setHgrow(forumCard, Priority.ALWAYS);
        HBox.setHgrow(feedbackCard, Priority.ALWAYS);
        forumCard.setMaxWidth(Double.MAX_VALUE);
        feedbackCard.setMaxWidth(Double.MAX_VALUE);

        HBox footer = UiFactory.actions(button_UpdateThisUser, UiFactory.spacer(), button_Logout, button_Quit);
        VBox screen = new VBox(18, UiFactory.card(label_PageTitle, label_UserDetails), cards, footer);
        screen.getStyleClass().add("pc-screen");
        screen.setAlignment(Pos.TOP_CENTER);

        theStudentHomeScene = PeerConnectShell.scene(
                ScreenSpec.of("Student Home", "Read, reply, and track feedback across your discussions.",
                        theUser, "Student", "bi-book"),
                UiFactory.scroll(screen));
    }
}
