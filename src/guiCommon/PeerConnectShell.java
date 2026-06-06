package guiCommon;

import applicationMain.FoundationsMain;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

/** Shared app shell used by modernized PeerConnect views. */
public final class PeerConnectShell {
    private PeerConnectShell() {
    }

    public static Scene scene(ScreenSpec spec, Node content) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("pc-shell");
        root.setTop(header(spec));
        root.setCenter(content);
        Scene scene = new Scene(root, FoundationsMain.WINDOW_WIDTH, FoundationsMain.WINDOW_HEIGHT);
        PeerConnectTheme.attach(scene);
        return scene;
    }

    public static void show(Stage stage, Scene scene, String title) {
        PeerConnectTheme.attach(scene);
        stage.setMinWidth(FoundationsMain.MIN_WINDOW_WIDTH);
        stage.setMinHeight(FoundationsMain.MIN_WINDOW_HEIGHT);
        stage.setResizable(true);
        stage.setTitle(title == null || title.isBlank() ? "PeerConnect" : title);
        stage.setScene(scene);
        stage.show();
    }

    private static Node header(ScreenSpec spec) {
        HBox header = new HBox(16);
        header.getStyleClass().add("pc-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 28, 16, 28));

        Label icon = new Label();
        icon.getStyleClass().add("pc-header-icon");
        if (spec.iconLiteral() != null && !spec.iconLiteral().isBlank()) {
            icon.setGraphic(new FontIcon(spec.iconLiteral()));
        }

        Label title = new Label(spec.title());
        title.getStyleClass().add("pc-title");
        Label subtitle = new Label(spec.subtitle() == null ? "" : spec.subtitle());
        subtitle.getStyleClass().add("pc-subtitle");
        VBox copy = new VBox(2, title, subtitle);
        copy.setMinWidth(0);
        HBox.setHgrow(copy, Priority.ALWAYS);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox meta = new HBox(8);
        meta.setAlignment(Pos.CENTER_RIGHT);
        if (spec.roleName() != null && !spec.roleName().isBlank()) {
            meta.getChildren().add(UiFactory.badge(spec.roleName(), "pc-badge-role"));
        }
        if (spec.user() != null) {
            meta.getChildren().add(UiFactory.badge(spec.user().getUserName(), "pc-badge-user"));
        }

        header.getChildren().addAll(icon, copy, spacer, meta);
        return header;
    }
}
