package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by eke on 2017/12/6.
 */
public class DBCRUD {
    public static void main(String[] args) throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("找不到驱动类！");
            e.printStackTrace();
        }

        String url = "jdbc:mysql://localhost:3306/eke";
        String userName = "root";
        String password = "123456";

        try (Connection connection = DriverManager.getConnection(url, userName, password)) {
            System.out.println("数据库链接成功！");

            try (Statement statement = connection.createStatement()) {
                String command = "insert into a_dept(name) values('eke'),('joke')";
                int count = statement.executeUpdate(command);

                System.out.println("受影响行数：" + count);

                String selectSql = "select * from a_dept";

                try (ResultSet resultSet = statement.executeQuery(selectSql)) {
                    while (resultSet.next()) {
                        System.out.print("id:" + resultSet.getInt("id"));
                        System.out.println("  name:" + resultSet.getString("name"));
                    }
                }
            }
        }
    }
}
