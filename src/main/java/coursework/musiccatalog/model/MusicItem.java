package coursework.musiccatalog.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MusicItem {
    public enum Type { ALBUM, TRACK }

    private int id;
    private String name;
    private String artist;
    private String genre;
    private LocalDate releaseDate;
    private String status;
    private Type type;

    // История изменений
    private List<String> changeHistory = new ArrayList<>();

    public MusicItem(int id, String name, String artist, String genre, LocalDate releaseDate, String status, Type type) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.status = status;
        this.type = type;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getArtist() { return artist; }
    public String getGenre() { return genre; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public String getStatus() { return status; }
    public Type getType() { return type; }
    public List<String> getChangeHistory() { return changeHistory; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) {
        if (!name.equals(this.name)) changeHistory.add("Название: " + this.name + " -> " + name);
        this.name = name;
    }
    public void setArtist(String artist) {
        if (!artist.equals(this.artist)) changeHistory.add("Артист: " + this.artist + " -> " + artist);
        this.artist = artist;
    }
    public void setGenre(String genre) {
        if (!genre.equals(this.genre)) changeHistory.add("Жанр: " + this.genre + " -> " + genre);
        this.genre = genre;
    }
    public void setReleaseDate(LocalDate releaseDate) {
        if (!releaseDate.equals(this.releaseDate)) changeHistory.add("Дата релиза: " + this.releaseDate + " -> " + releaseDate);
        this.releaseDate = releaseDate;
    }
    public void setStatus(String status) {
        if (!status.equals(this.status)) changeHistory.add("Статус: " + this.status + " -> " + status);
        this.status = status;
    }
    public void setType(Type type) { this.type = type; }
}
