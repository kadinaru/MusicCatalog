package coursework.musiccatalog.dao;

import coursework.musiccatalog.model.Album;
import coursework.musiccatalog.model.MusicItem;

import java.util.List;
import java.util.stream.Collectors;

public class SpotifyMusicDAOAdapter implements MusicDAO {

    private final SpotifyMusicDAO spotifyDAO;

    public SpotifyMusicDAOAdapter() {
        this.spotifyDAO = new SpotifyMusicDAO();
    }

    @Override
    public void addAlbum(Album album) {
        throw new UnsupportedOperationException("SpotifyAPI - запись недоступна");
    }

    @Override
    public void updateAlbum(Album album) {
        throw new UnsupportedOperationException("SpotifyAPI - запись недоступна");
    }

    @Override
    public void deleteAlbum(int id) {
        throw new UnsupportedOperationException("SpotifyAPI - запись недоступна");
    }

    @Override
    public Album getAlbumById(int id) {
        return getAllAlbums().stream().filter(a -> a.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<Album> getAllAlbums() {
        return spotifyDAO.getAllMusicItems().stream()
                .filter(item -> item.getType() == MusicItem.Type.ALBUM)
                .map(item -> new Album(
                        item.getId(),
                        item.getName(),
                        item.getArtist(),
                        item.getGenre(),
                        item.getReleaseDate(),
                        item.getStatus()
                ))
                .collect(Collectors.toList());
    }

    public List<MusicItem> getAllMusicItems() {
        return spotifyDAO.getAllMusicItems();
    }
}
