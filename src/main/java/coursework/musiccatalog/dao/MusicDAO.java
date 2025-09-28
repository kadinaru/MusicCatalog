package coursework.musiccatalog.dao;

import coursework.musiccatalog.model.Album;
import java.util.List;

public interface MusicDAO {
    void addAlbum(Album album);
    void updateAlbum(Album album);
    void deleteAlbum(int id);
    Album getAlbumById(int id);
    List<Album> getAllAlbums();
}
