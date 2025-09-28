package coursework.musiccatalog.controller;

import coursework.musiccatalog.model.Album;
import coursework.musiccatalog.model.MusicItem;
import coursework.musiccatalog.service.MusicService;
import coursework.musiccatalog.dao.SQLiteMusicDAO;
import coursework.musiccatalog.dao.CSVMusicDAO;
import coursework.musiccatalog.dao.SpotifyMusicDAOAdapter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class MusicController {

    @FXML private TableView<MusicItem> albumTable;
    @FXML private TableColumn<MusicItem, String> titleColumn;
    @FXML private TableColumn<MusicItem, String> artistColumn;
    @FXML private TableColumn<MusicItem, String> genreColumn;
    @FXML private TableColumn<MusicItem, LocalDate> releaseDateColumn;
    @FXML private TableColumn<MusicItem, String> statusColumn;
    @FXML private TableColumn<MusicItem, String> typeColumn;
    @FXML private TextField filterGenreField;
    @FXML private ChoiceBox<String> sourceChoiceBox;
    @FXML private ChoiceBox<Integer> decadeChoiceBox;

    @FXML private HBox titleBar;
    private Delta dragDelta = new Delta();

    private MusicService musicService;
    private ObservableList<MusicItem> musicList;

    @FXML
    private void initialize() {
        // Настройка таблицы
        albumTable.setEditable(false);

        titleColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
        artistColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getArtist()));
        genreColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getGenre()));
        statusColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));
        typeColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getType().name()));
        releaseDateColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getReleaseDate()));

        albumTable.setRowFactory(tv -> new TableRow<MusicItem>() {
            @Override
            protected void updateItem(MusicItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) setStyle("");
                else if ("Неактуальный".equalsIgnoreCase(item.getStatus())) setStyle("-fx-background-color: #696969;");
                else setStyle("");
            }

            {
                setOnMouseClicked(event -> {
                    if (!isEmpty() && event.getClickCount() == 2) {
                        openEditDialog(getItem());
                    }
                });
            }
        });

        // ChoiceBox источника
        sourceChoiceBox.setItems(FXCollections.observableArrayList("SQLite", "CSV", "SpotifyAPI"));
        sourceChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && musicService != null) switchSource(newVal);
        });

        // ChoiceBox десятилетий
        decadeChoiceBox.setItems(FXCollections.observableArrayList(1980, 1990, 2000, 2010, 2020));
        decadeChoiceBox.getSelectionModel().selectFirst();

        // Перетаскивание окна
        if (titleBar != null) {
            titleBar.setOnMousePressed(event -> {
                Stage stage = (Stage) titleBar.getScene().getWindow();
                dragDelta.x = stage.getX() - event.getScreenX();
                dragDelta.y = stage.getY() - event.getScreenY();
            });
            titleBar.setOnMouseDragged(event -> {
                Stage stage = (Stage) titleBar.getScene().getWindow();
                stage.setX(event.getScreenX() + dragDelta.x);
                stage.setY(event.getScreenY() + dragDelta.y);
            });
        }
    }

    public void setMusicService(MusicService service) {
        this.musicService = service;
        sourceChoiceBox.getSelectionModel().selectFirst();
    }

    private void switchSource(String source) {
        if (musicService == null) return;
        switch (source) {
            case "SQLite" -> musicService.setMusicDAO(new SQLiteMusicDAO());
            case "CSV" -> musicService.setMusicDAO(new CSVMusicDAO());
            case "SpotifyAPI" -> musicService.setMusicDAO(new SpotifyMusicDAOAdapter());
        }
        musicService.updateStatuses();
        loadMusicItems();
    }

    @FXML
    public void loadMusicItems() {
        if (musicService == null) return;
        List<MusicItem> all = musicService.getAllMusicItems();
        musicList = FXCollections.observableArrayList(all);
        albumTable.setItems(musicList);
    }

    @FXML
    private void addAlbum() {
        if (musicService == null) return;
        Album a = new Album();
        a.setTitle("Новый альбом");
        a.setArtist("Неизвестно");
        a.setGenre("Неизвестно");
        a.setReleaseDate(LocalDate.now());
        a.setStatus("Актуальный");
        musicService.addAlbum(a);
        loadMusicItems();
    }

    @FXML
    private void deleteAlbum() {
        if (musicService == null) return;
        MusicItem sel = albumTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            musicService.deleteAlbum(sel.getId());
            loadMusicItems();
        }
    }

    @FXML
    private void filterByGenre() {
        if (musicList == null) return;
        String g = filterGenreField.getText();
        if (g == null || g.isBlank()) loadMusicItems();
        else {
            List<MusicItem> filtered = musicList.stream()
                    .filter(item -> item.getGenre() != null && item.getGenre().equalsIgnoreCase(g))
                    .collect(Collectors.toList());
            musicList.setAll(filtered);
        }
    }

    @FXML
    private void sortByReleaseDate() {
        if (musicList == null) return;
        List<MusicItem> sorted = musicList.stream()
                .sorted((a, b) -> a.getReleaseDate().compareTo(b.getReleaseDate()))
                .collect(Collectors.toList());
        musicList.setAll(sorted);
    }

    @FXML
    private void generatePlaylist() {
        if (musicService == null) return;
        Integer decade = decadeChoiceBox.getValue();
        if (decade == null) return;

        List<MusicItem> playlist = musicService.generatePlaylistByDecade(decade);
        musicList.setAll(playlist);
    }

    // Открытие диалога редактирования
    private void openEditDialog(MusicItem item) {
        Dialog<MusicItem> dialog = new Dialog<>();
        dialog.setTitle("Редактировать альбом");

        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/musiccatalog.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        TextField titleField = new TextField(item.getName());
        TextField artistField = new TextField(item.getArtist());
        TextField genreField = new TextField(item.getGenre());
        DatePicker releaseDatePicker = new DatePicker(item.getReleaseDate());

        VBox content = new VBox(10,
                new Label("Название:"), titleField,
                new Label("Артист:"), artistField,
                new Label("Жанр:"), genreField,
                new Label("Дата релиза:"), releaseDatePicker
        );
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                item.setName(titleField.getText());
                item.setArtist(artistField.getText());
                item.setGenre(genreField.getText());
                item.setReleaseDate(releaseDatePicker.getValue());
                updateItem(item);
                return item;
            }
            return null;
        });

        dialog.showAndWait();
    }


    private void updateItem(MusicItem item) {
        if (musicService == null) return;
        if (musicService.getAllAlbums().stream().anyMatch(a -> a.getId() == item.getId())) {
            Album album = new Album(item.getId(), item.getName(), item.getArtist(), item.getGenre(), item.getReleaseDate(), item.getStatus());
            musicService.updateAlbum(album);
        }
        albumTable.refresh();
    }

    // Кнопки управления окном
    @FXML private void minimizeWindow() {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML private void maximizeWindow() {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    @FXML private void closeWindow() {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.close();
    }

    private static class Delta { double x, y; }
}
