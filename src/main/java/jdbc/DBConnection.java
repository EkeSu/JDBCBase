package jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by eke on 2017/12/6.
 */
public class DBConnection {
    public static void main(String[] args) throws SQLException, IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("找不到驱动类！");
            e.printStackTrace();
        }

        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get(System.getProperty("user.dir") + "/src/main/resources/jdbc.properties"))) {
            properties.load(in);
        }

        System.out.println(System.getProperty("user.dir"));

        String driver = properties.getProperty("jdbc.driver");
        String server = properties.getProperty("server");
        String port = properties.getProperty("port");
        String database = properties.getProperty("database");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");

        if (driver != null) System.setProperty("jdbc.driver", driver);

        String url = server + ":" + port + "/" + database;
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("数据库链接成功！");
        } catch (Exception e) {
            System.out.println("数据库链接失败");
            e.printStackTrace();
        }
    }
}
