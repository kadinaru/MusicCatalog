package coursework.musiccatalog.dao;

import coursework.musiccatalog.model.Album;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CSVMusicDAO implements MusicDAO {

    private static final String FILE = "src/main/resources/db/musiccatalog.csv";

    @Override
    public void addAlbum(Album album) {
        List<Album> all = getAllAlbums();
        int nextId = all.stream().mapToInt(Album::getId).max().orElse(0) + 1;
        album.setId(nextId);
        all.add(album);
        saveAll(all);
    }

    @Override
    public void updateAlbum(Album album) {
        List<Album> all = getAllAlbums();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == album.getId()) {
                all.set(i, album);
                break;
            }
        }
        saveAll(all);
    }

    @Override
    public void deleteAlbum(int id) {
        List<Album> all = getAllAlbums();
        all.removeIf(a -> a.getId() == id);
        saveAll(all);
    }

    @Override
    public Album getAlbumById(int id) {
        return getAllAlbums().stream().filter(a -> a.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<Album> getAllAlbums() {
        List<Album> list = new ArrayList<>();
        File f = new File(FILE);
        if (!f.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 6) {
                    list.add(new Album(
                            Integer.parseInt(p[0]),
                            p[1],
                            p[2],
                            p[3],
                            LocalDate.parse(p[4]),
                            p[5]
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void saveAll(List<Album> list) {
        File f = new File(FILE);
        try {
            if (!f.exists()) f.getParentFile().mkdirs();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f, false))) {
                for (Album a : list) {
                    bw.write(String.join(",",
                            String.valueOf(a.getId()),
                            a.getTitle(),
                            a.getArtist(),
                            a.getGenre(),
                            a.getReleaseDate().toString(),
                            a.getStatus()
                    ));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
