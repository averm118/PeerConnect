package guiStaffHome;

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

public class ViewStaffHome {
    protected static Label label_PageTitle = new Label();
    protected static Label label_UserDetails = new Label();
    protected static Button button_UpdateThisUser = UiFactory.action(
            ActionSpec.of("Account", "bi-person", ControllerStaffHome::performUpdate, "pc-button-secondary"));

    protected static Button button_GoToForum = UiFactory.action(
            ActionSpec.of("Open Forum", "bi-chat-dots", ControllerStaffHome::goToStaffForum));
    protected static Button button_AdminRequests = UiFactory.action(
            ActionSpec.of("Admin Requests", "bi-inboxes", ControllerStaffHome::goToAdminRequests));
    protected static Button button_EvaluateDiscussions = UiFactory.action(
            ActionSpec.of("Evaluate Discussions", "bi-clipboard-check", ControllerStaffHome::goToEvaluateDiscussions));

    protected static Button button_Logout = UiFactory.action(
            ActionSpec.of("Logout", "bi-box-arrow-left", ControllerStaffHome::performLogout, "pc-button-secondary"));
    protected static Button button_Quit = UiFactory.action(
            ActionSpec.of("Quit", "bi-x", ControllerStaffHome::performQuit, "pc-button-secondary"));

    private static ViewStaffHome theView;
    private static Database theDatabase = applicationMain.FoundationsMain.database;
    protected static Stage theStage;
    protected static User theUser;
    private static Scene theViewStaffHomeScene;
    protected static final int theRole = 2;

    public static void displayStaffHome(Stage ps, User user) {
        theStage = ps;
        theUser = user;
        if (theView == null) {
            theView = new ViewStaffHome();
        }
        theDatabase.getUserAccountDetails(user.getUserName());
        applicationMain.FoundationsMain.activeHomePage = theRole;
        label_UserDetails.setText("Signed in as " + theUser.getUserName());
        PeerConnectShell.show(theStage, theViewStaffHomeScene, "PeerConnect: Staff Home");
    }

    private ViewStaffHome() {
        label_PageTitle.setText("Staff Home");
        label_PageTitle.getStyleClass().add("pc-heading");
        label_UserDetails.getStyleClass().add("pc-body");

        VBox forumCard = UiFactory.card(
                UiFactory.section("Forum moderation",
                        UiFactory.body("Review discussions, create posts, manage threads, and handle flagged content."),
                        UiFactory.actions(button_GoToForum)));
        VBox evaluationCard = UiFactory.card(
                UiFactory.section("Evaluation",
                        UiFactory.body("Grade discussion activity and share private feedback with students."),
                        UiFactory.actions(button_EvaluateDiscussions)));
        VBox requestCard = UiFactory.card(
                UiFactory.section("Requests",
                        UiFactory.body("Open shared requests for admin follow-up or reopen closed issues."),
                        UiFactory.actions(button_AdminRequests)));

        HBox cards = new HBox(18, forumCard, evaluationCard, requestCard);
        for (javafx.scene.Node card : cards.getChildren()) {
            HBox.setHgrow(card, Priority.ALWAYS);
            ((VBox) card).setMaxWidth(Double.MAX_VALUE);
        }

        HBox footer = UiFactory.actions(button_UpdateThisUser, UiFactory.spacer(), button_Logout, button_Quit);
        VBox screen = new VBox(18, UiFactory.card(label_PageTitle, label_UserDetails), cards, footer);
        screen.getStyleClass().add("pc-screen");
        screen.setAlignment(Pos.TOP_CENTER);

        theViewStaffHomeScene = PeerConnectShell.scene(
                ScreenSpec.of("Staff Home", "Guide discussions, evaluate participation, and coordinate requests.",
                        theUser, "Staff", "bi-person-badge"),
                UiFactory.scroll(screen));
    }
}
