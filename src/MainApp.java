import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class MainApp extends Application {

    public static final String APP_TITLE = "NOVA";
    public static final double DEFAULT_WIDTH = 1280;
    public static final double DEFAULT_HEIGHT = 800;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/resources/BrowserView.fxml")
        );

        Scene scene = new Scene(loader.load(), DEFAULT_WIDTH, DEFAULT_HEIGHT);

        scene.getStylesheets().add(
            Objects.requireNonNull(
                getClass().getResource("/resources/css/browser.css")
            ).toExternalForm()
        );

        // Window icons — multiple sizes so Windows can pick the best one
        // for the title bar (16/32) vs taskbar (32/48) vs alt-tab (256).
        loadIcon(primaryStage, "/resources/icons/nova-icon-16.png");
        loadIcon(primaryStage, "/resources/icons/nova-icon-32.png");
        loadIcon(primaryStage, "/resources/icons/nova-icon-48.png");
        loadIcon(primaryStage, "/resources/icons/nova-icon-64.png");
        loadIcon(primaryStage, "/resources/icons/nova-icon-128.png");
        loadIcon(primaryStage, "/resources/icons/nova-icon-256.png");

        primaryStage.setTitle(APP_TITLE);
        primaryStage.setWidth(900);
        primaryStage.setHeight(600);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(550);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadIcon(Stage stage, String path) {
        try (InputStream in = getClass().getResourceAsStream(path)) {
            if (in != null) {
                stage.getIcons().add(new Image(in));
            }
        } catch (Exception e) {
            // Icon is cosmetic — fail silently if a size is missing
            System.err.println("[NOVA] Could not load icon: " + path);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
