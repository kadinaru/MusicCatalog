package coursework.musiccatalog;

import coursework.musiccatalog.controller.MusicController;
import coursework.musiccatalog.dao.MusicDAO;
import coursework.musiccatalog.dao.SQLiteMusicDAO;
import coursework.musiccatalog.service.MusicService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 500);
        scene.getStylesheets().add(getClass().getResource("/css/musiccatalog.css").toExternalForm());


        MusicController controller = loader.getController();

        MusicDAO dao = new SQLiteMusicDAO();
        MusicService service = new MusicService(dao);
        controller.setMusicService(service);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Music Catalog");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
