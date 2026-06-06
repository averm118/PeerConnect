package guiCommon;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.scene.Scene;

/** Central theme installation and stylesheet wiring for PeerConnect. */
public final class PeerConnectTheme {
    private static boolean installed;

    private PeerConnectTheme() {
    }

    public static void install() {
        if (installed) {
            return;
        }
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        installed = true;
    }

    public static void attach(Scene scene) {
        install();
        String css = PeerConnectTheme.class.getResource("/applicationMain/application.css").toExternalForm();
        if (!scene.getStylesheets().contains(css)) {
            scene.getStylesheets().add(css);
        }
    }
}
