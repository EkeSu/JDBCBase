package jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Properties;

/**
 * Created by eke on 2017/12/6.
 */
public class DBTransactional {
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

            try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                String command = "insert into a_dept(name) values('1'),('2')";
                int count = statement.executeUpdate(command);
                System.out.println("受影响行数：" + count);

                String selectSql = "select * from a_dept";
                String updateSql = "update a_dept set name=? where id=?";
                String insertSql = "insert into a_dept(name) values(?)";

                try (ResultSet resultSet = statement.executeQuery(selectSql)) {
                    while (resultSet.next()) {
                        System.out.print("修改前id " + resultSet.getInt("id") + "的值是: ");
                        System.out.println(resultSet.getString("name"));
                    }

                    resultSet.beforeFirst();
                    connection.setAutoCommit(false);

                    while (resultSet.next()) {
                        PreparedStatement updateState = connection.prepareStatement(updateSql);
                        updateState.setString(1, "I am new" + resultSet.getInt("id"));
                        updateState.setInt(2, resultSet.getInt("id"));
                        updateState.executeUpdate();
                    }

                    Savepoint savepoint = connection.setSavepoint();
                    PreparedStatement updateState = connection.prepareStatement(insertSql);

                    updateState.setString(1, "我是插入的");
                    updateState.executeUpdate();

                    try {
                        PreparedStatement updateStateFail = connection.prepareStatement(insertSql);
                        updateStateFail.setString(1, "我是插入的,但是我太长了，所以我是插不进去的，会报错！");
                        updateStateFail.executeUpdate();

                    } catch (Exception e) {
                        connection.rollback(savepoint); // 回滚到指定节点，若是没有指定节点则全部回滚
                        connection.releaseSavepoint(savepoint);  // 节点必须释放
                        System.err.println(e.getMessage());
                    }

                    connection.commit();

                    Statement newStatement = connection.createStatement();
                    ResultSet newSet = newStatement.executeQuery(selectSql);

                    System.out.println("打印出所有的值：");

                    while (newSet.next()) {
                        System.out.print(newSet.getInt("id") + ": ");
                        System.out.println(newSet.getString("name"));
                    }
                }
            }
        }
    }
}
