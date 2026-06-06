package guiChangePassword;

import guiCommon.ActionSpec;
import guiCommon.UiFactory;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Password-change view with live validation feedback.
 *
 * @author Lynn Robert Carter
 */
public class ViewChangePassword {
    static private Label label_Password = new Label("New password");
    static protected TextField text_Password = new PasswordField();

    static protected Label label_errPassword = new Label();
    static protected Label noInputFound = new Label();
    static private TextFlow errPassword;
    static protected Text errPasswordPart1 = new Text();
    static protected Text errPasswordPart2 = new Text();
    static protected Label errPasswordPart3 = new Label();

    static protected Label validPassword = new Label();
    static protected Label label_Requirements =
            new Label("A valid password must satisfy the following requirements:");
    static protected Label label_UpperCase = new Label();
    static protected Label label_LowerCase = new Label();
    static protected Label label_NumericDigit = new Label();
    static protected Label label_SpecialChar = new Label();
    static protected Label label_LongEnough = new Label();
    static protected Label label_ShortEnough = new Label();

    static protected Button button_Finish = UiFactory.action(
            ActionSpec.of("Save Password", "bi-check2",
                    ControllerChangePasssword::handleButtonPress));

    private static boolean listenerInstalled;

    public static void view(Pane theRoot) {
        theRoot.getChildren().clear();
        theRoot.getStyleClass().add("pc-shell");

        UiFactory.prepareInput(text_Password, "Enter a new password");
        text_Password.clear();
        if (!listenerInstalled) {
            text_Password.textProperty().addListener((observable, oldValue, newValue) ->
                    ModelChangePassword.updatePassword());
            listenerInstalled = true;
        }

        label_Password.getStyleClass().add("pc-field-label");
        label_errPassword.getStyleClass().add("pc-caption");
        noInputFound.getStyleClass().add("pc-caption");
        errPasswordPart3.getStyleClass().add("pc-caption");
        validPassword.getStyleClass().add("pc-body");
        label_Requirements.getStyleClass().add("pc-section-title");
        for (Label label : new Label[] {
                label_UpperCase, label_LowerCase, label_NumericDigit,
                label_SpecialChar, label_LongEnough, label_ShortEnough }) {
            label.getStyleClass().add("pc-body");
            label.setWrapText(true);
        }

        errPasswordPart1.setFill(Color.BLACK);
        errPasswordPart2.setFill(Color.RED);
        errPassword = new TextFlow(errPasswordPart1, errPasswordPart2);
        errPassword.getStyleClass().add("pc-body");

        noInputFound.setText("No input text found!");
        noInputFound.setTextFill(Color.RED);
        label_errPassword.setTextFill(Color.RED);
        validPassword.setText("");
        button_Finish.setDisable(true);
        resetAssessments();

        GridPane form = UiFactory.formGrid();
        UiFactory.formRow(form, 0, "Password", text_Password);

        VBox status = UiFactory.stack(8,
                noInputFound,
                label_errPassword,
                errPassword,
                errPasswordPart3,
                validPassword);

        VBox requirements = UiFactory.stack(8,
                label_Requirements,
                label_UpperCase,
                label_LowerCase,
                label_NumericDigit,
                label_SpecialChar,
                label_LongEnough,
                label_ShortEnough);

        VBox screen = new VBox(18,
                UiFactory.card(
                        UiFactory.heading("Update Password"),
                        UiFactory.body("Choose a password that satisfies every requirement before saving.")),
                UiFactory.card(UiFactory.section("Password", form)),
                UiFactory.card(UiFactory.section("Validation", status)),
                UiFactory.card(UiFactory.section("Requirements", requirements)),
                UiFactory.actions(button_Finish));
        screen.getStyleClass().add("pc-screen");
        screen.setAlignment(Pos.TOP_CENTER);
        screen.prefWidthProperty().bind(theRoot.widthProperty());
        screen.prefHeightProperty().bind(theRoot.heightProperty());

        theRoot.getChildren().add(screen);
    }

    static protected void resetAssessments() {
        label_UpperCase.setText("At least one upper case letter - Not yet satisfied");
        label_UpperCase.setTextFill(Color.RED);

        label_LowerCase.setText("At least one lower case letter - Not yet satisfied");
        label_LowerCase.setTextFill(Color.RED);

        label_NumericDigit.setText("At least one numeric digit - Not yet satisfied");
        label_NumericDigit.setTextFill(Color.RED);

        label_SpecialChar.setText("At least one special character - Not yet satisfied");
        label_SpecialChar.setTextFill(Color.RED);

        label_LongEnough.setText("At least eight characters - Not yet satisfied");
        label_LongEnough.setTextFill(Color.RED);

        label_ShortEnough.setText("Less than sixteen characters - Not yet satisfied");
        label_ShortEnough.setTextFill(Color.RED);
    }
}
