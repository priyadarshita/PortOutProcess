package mnp.org;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JDBCConnection {
	
	public Connection Connection_DB() {
		
		Connection connection = null;
		String url = "jdbc:mysql://127.0.0.1:3306/customer-order";
		String username = "root";
		String password = "server@396332";

		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
			try {
				connection = DriverManager.getConnection(url, username, password);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("Connection established");
		
	
		return connection;
}
	
}
