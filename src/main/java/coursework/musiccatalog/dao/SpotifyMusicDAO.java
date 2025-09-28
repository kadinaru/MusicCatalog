package coursework.musiccatalog.dao;

import coursework.musiccatalog.model.MusicItem;
import coursework.musiccatalog.model.MusicItem.Type;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.control.Alert;

public class SpotifyMusicDAO {
    private String token;

    public SpotifyMusicDAO() {
        token = SpotifyAuth.getAccessToken();
    }

    public List<MusicItem> getAllMusicItems() {
        List<MusicItem> list = new ArrayList<>();
        if (token == null) return list;

        try {
            URL url = new URL("https://api.spotify.com/v1/browse/new-releases?limit=10");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestMethod("GET");

            JsonObject root = JsonParser.parseReader(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
            JsonArray items = root.getAsJsonObject("albums").getAsJsonArray("items");

            int idCounter = 1;

            for (int i = 0; i < items.size(); i++) {
                JsonObject aj = items.get(i).getAsJsonObject();

                // Альбом
                String albumName = aj.get("name").getAsString();
                String release = aj.get("release_date").getAsString();
                LocalDate date = LocalDate.parse(release);
                JsonObject firstArtist = aj.getAsJsonArray("artists").get(0).getAsJsonObject();
                String artist = firstArtist.get("name").getAsString();
                String artistId = firstArtist.get("id").getAsString();

                // Жанр артиста
                String genre = "Unknown";
                try {
                    URL artistUrl = new URL("https://api.spotify.com/v1/artists/" + artistId);
                    HttpURLConnection artistConn = (HttpURLConnection) artistUrl.openConnection();
                    artistConn.setRequestProperty("Authorization", "Bearer " + token);
                    artistConn.setRequestMethod("GET");

                    JsonObject artistDetails = JsonParser.parseReader(new InputStreamReader(artistConn.getInputStream())).getAsJsonObject();
                    JsonArray genresArray = artistDetails.getAsJsonArray("genres");
                    if (genresArray != null && genresArray.size() > 0) genre = genresArray.get(0).getAsString();
                } catch (Exception e) {
                }

                // Добавляем альбом
                list.add(new MusicItem(idCounter++, albumName, artist, genre, date, "Active", Type.ALBUM));

                String albumId = aj.get("id").getAsString();

                // Добавляем треки альбома
                try {
                    URL tracksUrl = new URL("https://api.spotify.com/v1/albums/" + albumId + "/tracks");
                    HttpURLConnection tracksConn = (HttpURLConnection) tracksUrl.openConnection();
                    tracksConn.setRequestProperty("Authorization", "Bearer " + token);
                    tracksConn.setRequestMethod("GET");

                    JsonObject tracksRoot = JsonParser.parseReader(new InputStreamReader(tracksConn.getInputStream())).getAsJsonObject();
                    JsonArray tracks = tracksRoot.getAsJsonArray("items");

                    for (int t = 0; t < tracks.size(); t++) {
                        JsonObject track = tracks.get(t).getAsJsonObject();
                        String trackName = track.get("name").getAsString();
                        list.add(new MusicItem(idCounter++, trackName, artist, genre, date, "Active", Type.TRACK));
                    }
                } catch (Exception e) {
                }
            }
        } catch (IOException | RuntimeException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Подключение к Spotify API");
            alert.setHeaderText("Не удалось подключиться");
            alert.setContentText("Для работы со Spotify API требуется активное VPN-соединение.");
            alert.showAndWait();
        }

        return list;
    }
}
