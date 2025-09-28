package coursework.musiccatalog.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String DB_FOLDER = "src/main/resources/db";
    private static final String DB_FILE = "musiccatalog.db";
    private static final String URL;

    static {
        File f = new File(DB_FOLDER);
        if (!f.exists()) f.mkdirs();
        URL = "jdbc:sqlite:" + DB_FOLDER + File.separator + DB_FILE;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
