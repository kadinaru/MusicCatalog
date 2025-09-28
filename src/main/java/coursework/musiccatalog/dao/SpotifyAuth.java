package coursework.musiccatalog.dao;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

public class SpotifyAuth {
    private static final String CLIENT_ID = "7ebd9727482646b7860e77151339fce0";
    private static final String CLIENT_SECRET = "6e0dd6cd4f4247c1a955c74442dfade0";
    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";

    public static String getAccessToken() {
        try {
            String auth = CLIENT_ID + ":" + CLIENT_SECRET;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TOKEN_URL))
                    .header("Authorization", "Basic " + encodedAuth)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Ошибка авторизации Spotify: " + response.body());
                return null;
            }

            String body = response.body();
            int start = body.indexOf("access_token\":\"") + 15;
            int end = body.indexOf("\"", start);
            return body.substring(start, end);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
