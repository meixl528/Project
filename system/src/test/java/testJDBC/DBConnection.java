package testJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
/**
 * @name        DBConnection
 * @description 使用mysql数据库驱动连接数据库
 * @version
 */
public class DBConnection {
	
	static String driver = "com.mysql.jdbc.Driver";
	// localhost指本机，也可以用本地ip地址代替，3306为MySQL数据库的默认端口号，“user”为要连接的数据库名
	static String url = "jdbc:mysql://192.168.10.31:3306/springmybaitis";
	// 填入数据库的用户名跟密码
	static String dbname = "root";
	static String dbpass = "123456";
	
	//main测试
	/*public static void main(String[] args) {
		getConn();
	}*/
	
	//查询
	public static List<User> executeSelect(String sql){
		List<User> list = new ArrayList<>();
		try {
			Connection conn = getConn();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);// 执行sql语句并返回结果集
			User user;
			while (rs.next())// 对结果集进行遍历输出
			{
				//存储到user对象
				user = new User();
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password"));
				list.add(user);
			}
			
			if (st != null) st.close();
			if (conn != null) conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}// 创建sql执行对象
		return list;
		
	}
	
	//新郑
	public static int executeUpdate(String sql){
		int rs = 0;
		try {
			Connection conn = getConn();
			Statement st = conn.createStatement();
			rs = st.executeUpdate(sql);// 执行sql语句并返回结果集
			
			if (st != null) st.close();
			if (conn != null) conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}// 创建sql执行对象
		return rs;
	}
	
	/**
	 * 连接数据库
	 * @return
	 */
	public static Connection getConn(){
		Connection con = null;
		try {
			Class.forName(driver);// 加载驱动程序，此处运用隐式注册驱动程序的方法
			con = DriverManager.getConnection(url, dbname, dbpass);// 创建连接对象
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return con;
	}
}
