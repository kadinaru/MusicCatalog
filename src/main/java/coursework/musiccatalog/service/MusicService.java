package coursework.musiccatalog.service;

import coursework.musiccatalog.dao.MusicDAO;
import coursework.musiccatalog.model.Album;
import coursework.musiccatalog.model.MusicItem;
import coursework.musiccatalog.dao.SQLiteMusicDAO;
import coursework.musiccatalog.dao.CSVMusicDAO;
import coursework.musiccatalog.dao.SpotifyMusicDAOAdapter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class MusicService {

    private MusicDAO dao;

    public MusicService(MusicDAO dao) {
        this.dao = dao;
    }

    public void setMusicDAO(MusicDAO dao) {
        this.dao = dao;
    }

    public void addAlbum(Album a) {
        dao.addAlbum(a);
    }

    public void updateAlbum(Album a) {
        dao.updateAlbum(a);
    }

    public void deleteAlbum(int id) {
        dao.deleteAlbum(id);
    }

    public List<Album> getAllAlbums() {
        return dao.getAllAlbums();
    }

    public List<MusicItem> getAllMusicItems() {
        if (dao instanceof SQLiteMusicDAO) {
            return ((SQLiteMusicDAO) dao).getAllMusicItems();
        } else if (dao instanceof SpotifyMusicDAOAdapter) {
            return ((SpotifyMusicDAOAdapter) dao).getAllMusicItems();
        } else if (dao instanceof CSVMusicDAO) {
            // Преобразуем альбомы в MusicItem (тип ALBUM)
            return dao.getAllAlbums().stream()
                    .map(a -> new MusicItem(
                            a.getId(),
                            a.getTitle(),
                            a.getArtist(),
                            a.getGenre(),
                            a.getReleaseDate(),
                            a.getStatus(),
                            MusicItem.Type.ALBUM
                    ))
                    .collect(Collectors.toList());
        }
        return getAllAlbums().stream()
                .map(a -> new MusicItem(
                        a.getId(),
                        a.getTitle(),
                        a.getArtist(),
                        a.getGenre(),
                        a.getReleaseDate(),
                        a.getStatus(),
                        MusicItem.Type.ALBUM
                ))
                .collect(Collectors.toList());
    }


    // Обновление статусов
    public void updateStatuses() {
        if (!(dao instanceof SQLiteMusicDAO || dao instanceof CSVMusicDAO)) {
            return;
        }

        LocalDate now = LocalDate.now();
        getAllAlbums().forEach(a -> {
            if (a.getReleaseDate() != null && a.getReleaseDate().isBefore(now.minusMonths(6)))
                a.setStatus("Неактуальный");
            else
                a.setStatus("Актуальный");

            try {
                dao.updateAlbum(a);
            } catch (Exception ignored) {}
        });
    }

    // Генерация плейлиста по десятилетию
    public List<MusicItem> generatePlaylistByDecade(int decadeStartYear) {
        int decadeEndYear = decadeStartYear + 9;
        return getAllMusicItems().stream()
                .filter(item -> {
                    LocalDate d = item.getReleaseDate();
                    return d != null && d.getYear() >= decadeStartYear && d.getYear() <= decadeEndYear;
                })
                .collect(Collectors.toList());
    }
}
