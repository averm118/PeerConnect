package guiCommon;

import java.util.Arrays;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

/** Small JavaFX builders that keep view classes readable and visually consistent. */
public final class UiFactory {
    private UiFactory() {
    }

    public static Label heading(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("pc-heading");
        label.setWrapText(true);
        return label;
    }

    public static Label body(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("pc-body");
        label.setWrapText(true);
        return label;
    }

    public static Label caption(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("pc-caption");
        label.setWrapText(true);
        return label;
    }

    public static Label badge(String text, String... styleClasses) {
        Label badge = new Label(text);
        badge.getStyleClass().add("pc-badge");
        badge.getStyleClass().addAll(styleClasses);
        return badge;
    }

    public static Button action(ActionSpec spec) {
        Button button = new Button(spec.label());
        button.getStyleClass().add("pc-button");
        button.getStyleClass().addAll(spec.styleClasses());
        button.setMnemonicParsing(false);
        button.setAccessibleRole(AccessibleRole.BUTTON);
        button.setAccessibleText(spec.label());
        if (spec.iconLiteral() != null && !spec.iconLiteral().isBlank()) {
            button.setGraphic(new FontIcon(spec.iconLiteral()));
        }
        if (spec.action() != null) {
            button.setOnAction(_ -> spec.action().run());
        }
        return button;
    }

    public static Button iconButton(String accessibleText, String iconLiteral, Runnable action, String... styleClasses) {
        Button button = action(ActionSpec.of("", iconLiteral, action, styleClasses));
        button.getStyleClass().add("pc-icon-button");
        button.setTooltip(new Tooltip(accessibleText));
        button.setAccessibleText(accessibleText);
        return button;
    }

    public static VBox card(Node... children) {
        VBox box = new VBox(12);
        box.getStyleClass().add("pc-card");
        box.getChildren().addAll(children);
        return box;
    }

    public static VBox section(String title, Node... children) {
        VBox box = new VBox(10);
        box.getStyleClass().add("pc-section");
        Label heading = new Label(title);
        heading.getStyleClass().add("pc-section-title");
        box.getChildren().add(heading);
        box.getChildren().addAll(children);
        return box;
    }

    public static HBox actions(Node... children) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getStyleClass().add("pc-actions");
        box.getChildren().addAll(children);
        return box;
    }

    public static GridPane formGrid() {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("pc-form-grid");
        grid.setHgap(14);
        grid.setVgap(12);
        ColumnConstraints labels = new ColumnConstraints();
        labels.setMinWidth(145);
        labels.setPrefWidth(170);
        ColumnConstraints inputs = new ColumnConstraints();
        inputs.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().setAll(labels, inputs);
        return grid;
    }

    public static void formRow(GridPane grid, int row, String labelText, Node field) {
        Label label = new Label(labelText);
        label.getStyleClass().add("pc-field-label");
        label.setLabelFor(field);
        label.setAlignment(Pos.CENTER_RIGHT);
        label.setMaxWidth(Double.MAX_VALUE);
        grid.add(label, 0, row);
        grid.add(field, 1, row);
        GridPane.setHgrow(field, Priority.ALWAYS);
    }

    public static TextField textField(String prompt) {
        TextField field = new TextField();
        prepareInput(field, prompt);
        return field;
    }

    public static PasswordField passwordField(String prompt) {
        PasswordField field = new PasswordField();
        prepareInput(field, prompt);
        return field;
    }

    public static void prepareInput(TextField field, String prompt) {
        field.setPromptText(prompt);
        field.getStyleClass().add("pc-input");
        field.setMaxWidth(Double.MAX_VALUE);
        field.setAccessibleRole(AccessibleRole.TEXT_FIELD);
    }

    public static void prepareTextArea(TextArea area, String prompt) {
        area.setPromptText(prompt);
        area.getStyleClass().add("pc-text-area");
        area.setWrapText(true);
        area.setMaxWidth(Double.MAX_VALUE);
        area.setAccessibleRole(AccessibleRole.TEXT_AREA);
    }

    public static void prepareCombo(ComboBox<?> combo) {
        combo.getStyleClass().add("pc-combo");
        combo.setMaxWidth(Double.MAX_VALUE);
        combo.setAccessibleRole(AccessibleRole.COMBO_BOX);
    }

    public static void prepareList(ListView<?> listView, String accessibleText) {
        listView.getStyleClass().add("pc-list");
        listView.setAccessibleText(accessibleText);
        listView.setAccessibleRole(AccessibleRole.LIST_VIEW);
    }

    public static ScrollPane scroll(Node content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.getStyleClass().add("pc-scroll");
        scroll.setFitToWidth(true);
        scroll.setPadding(Insets.EMPTY);
        return scroll;
    }

    public static Region spacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    public static VBox stack(double spacing, Node... children) {
        VBox box = new VBox(spacing);
        box.getChildren().addAll(Arrays.asList(children));
        return box;
    }
}
