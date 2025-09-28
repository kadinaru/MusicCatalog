package coursework.musiccatalog.dao;

import coursework.musiccatalog.model.MusicItem;
import coursework.musiccatalog.model.MusicItem.Type;
import coursework.musiccatalog.model.Album;
import coursework.musiccatalog.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SQLiteMusicDAO implements MusicDAO {

    public SQLiteMusicDAO() {
        try (Connection conn = DatabaseUtil.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS music_items (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT," +
                            "artist TEXT," +
                            "genre TEXT," +
                            "releaseDate TEXT," +
                            "status TEXT," +
                            "type TEXT)"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addAlbum(Album album) {
        MusicItem item = new MusicItem(
                0, album.getTitle(), album.getArtist(),
                album.getGenre(), album.getReleaseDate(), album.getStatus(), Type.ALBUM
        );
        addMusicItem(item);
    }

    public void addMusicItem(MusicItem item) {
        String sql = "INSERT INTO music_items(name, artist, genre, releaseDate, status, type) VALUES(?,?,?,?,?,?)";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getArtist());
            ps.setString(3, item.getGenre());
            ps.setString(4, item.getReleaseDate().toString());
            ps.setString(5, item.getStatus());
            ps.setString(6, item.getType().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMusicItem(MusicItem item) {
        String sql = "UPDATE music_items SET name=?, artist=?, genre=?, releaseDate=?, status=?, type=? WHERE id=?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getArtist());
            ps.setString(3, item.getGenre());
            ps.setString(4, item.getReleaseDate().toString());
            ps.setString(5, item.getStatus());
            ps.setString(6, item.getType().name());
            ps.setInt(7, item.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateAlbum(Album album) {
        MusicItem item = new MusicItem(
                album.getId(), album.getTitle(), album.getArtist(),
                album.getGenre(), album.getReleaseDate(), album.getStatus(), Type.ALBUM
        );
        updateMusicItem(item);
    }

    @Override
    public void deleteAlbum(int id) {
        String sql = "DELETE FROM music_items WHERE id=?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Album getAlbumById(int id) {
        String sql = "SELECT * FROM music_items WHERE id=? AND type='ALBUM'";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Album(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("artist"),
                        rs.getString("genre"),
                        LocalDate.parse(rs.getString("releaseDate")),
                        rs.getString("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Album> getAllAlbums() {
        List<Album> list = new ArrayList<>();
        String sql = "SELECT * FROM music_items WHERE type='ALBUM'";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Album(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("artist"),
                        rs.getString("genre"),
                        LocalDate.parse(rs.getString("releaseDate")),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<MusicItem> getAllMusicItems() {
        List<MusicItem> list = new ArrayList<>();
        String sql = "SELECT * FROM music_items";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new MusicItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("artist"),
                        rs.getString("genre"),
                        LocalDate.parse(rs.getString("releaseDate")),
                        rs.getString("status"),
                        Type.valueOf(rs.getString("type"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
