package guiManageInvitations;

import java.util.List;

import database.Database;
import entityClasses.User;
import guiCommon.ActionSpec;
import guiCommon.PeerConnectShell;
import guiCommon.ScreenSpec;
import guiCommon.UiFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ViewManageInvitations {
    private static ViewManageInvitations theView;
    public static Database theDatabase = applicationMain.FoundationsMain.database;
    private static Timeline refreshTimer;

    protected static Stage theStage;
    protected static User theUser;
    public static Scene theManageInvitationsScene = null;

    protected static Label label_PageTitle = new Label("Outstanding Invitations");
    protected static Label label_UserDetails = new Label();
    protected static ListView<String> listView_Invitations = new ListView<>();

    protected static Button button_Return = UiFactory.action(
            ActionSpec.of("Return", "bi-arrow-left", ControllerManageInvitations::performReturn,
                    "pc-button-secondary"));
    protected static Button button_Delete = UiFactory.action(
            ActionSpec.of("Delete Selected", "bi-trash", ControllerManageInvitations::performDelete,
                    "pc-button-danger"));
    protected static Button button_Quit = UiFactory.action(
            ActionSpec.of("Quit", "bi-x", ControllerManageInvitations::performQuit,
                    "pc-button-secondary"));

    public static void displayManageInvitations(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theView == null) {
            theView = new ViewManageInvitations();
        }

        populateInvitationList();
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        refreshTimer = new Timeline(new KeyFrame(Duration.minutes(1), _ -> populateInvitationList()));
        refreshTimer.setCycleCount(Timeline.INDEFINITE);
        refreshTimer.play();

        PeerConnectShell.show(theStage, theManageInvitationsScene, "PeerConnect: Invitations");
    }

    protected static void stopTimer() {
        if (refreshTimer != null) {
            refreshTimer.stop();
            refreshTimer = null;
        }
    }

    public ViewManageInvitations() {
        label_PageTitle.getStyleClass().add("pc-heading");
        label_UserDetails.getStyleClass().add("pc-body");
        UiFactory.prepareList(listView_Invitations, "Outstanding invitations");

        VBox listCard = UiFactory.card(UiFactory.section("Invitation codes", listView_Invitations));
        VBox.setVgrow(listView_Invitations, Priority.ALWAYS);
        VBox.setVgrow(listCard, Priority.ALWAYS);

        HBox footer = UiFactory.actions(button_Delete, UiFactory.spacer(), button_Return, button_Quit);
        VBox screen = new VBox(18, UiFactory.card(label_PageTitle, label_UserDetails), listCard, footer);
        screen.getStyleClass().add("pc-screen");
        screen.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(listCard, Priority.ALWAYS);

        theManageInvitationsScene = PeerConnectShell.scene(
                ScreenSpec.of("Invitations", "Track active invitation codes and remove stale entries.",
                        theUser, "Admin", "bi-envelope-open"),
                screen);
    }

    public static void populateInvitationList() {
        List<String> invitationList = theDatabase.getInvitationListEnriched();
        listView_Invitations.setItems(FXCollections.observableArrayList(invitationList));
        label_UserDetails.setText("Logged in as " + theUser.getUserName());
    }
}
