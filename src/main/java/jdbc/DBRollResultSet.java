package jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by eke on 2017/12/6.
 */
public class DBRollResultSet {
    public static void main(String[] args) throws SQLException, IOException {
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

            try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                String command = "insert into a_dept(name) values('1'),('2')";
                int count = statement.executeUpdate(command);
                System.out.println("受影响行数：" + count);

                String selectSql = "select * from a_dept";

                try (ResultSet resultSet = statement.executeQuery(selectSql)) {
                    while (resultSet.next()) {
                        System.out.print("修改前id " + resultSet.getInt("id") + "的值是: ");
                        System.out.println(resultSet.getString("name"));
                        resultSet.updateString("name", "我是修改过的");
                        resultSet.updateRow();
                        System.out.print("修改后id " + resultSet.getInt("id") + "的值是: ");
                        System.out.println(resultSet.getString("name"));
                    }

                    resultSet.moveToInsertRow();
                    resultSet.updateString("name", "我是插入的");
                    resultSet.insertRow();
                    resultSet.beforeFirst();

                    System.out.println("打印出所有的值：");

                    while (resultSet.next()) {
                        System.out.print(resultSet.getInt("id") + ": ");
                        System.out.println(resultSet.getString("name"));
                    }
                }
            }
        }
    }
}
