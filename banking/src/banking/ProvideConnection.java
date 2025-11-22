	    package banking;

		    import java.sql.Connection;
		    import java.sql.DriverManager;
		    import java.sql.SQLException;

		    public class ProvideConnection {

		        public static Connection giveusConnection() throws SQLException, ClassNotFoundException {
		            // Load MySQL JDBC driver
		            Class.forName("com.mysql.cj.jdbc.Driver");

		            // Database connection details
		            String url = "jdbc:mysql://localhost:3306/bankdb"; // <-- replace with your DB name
		            String user = "root";   // <-- replace with your MySQL username
		            String pass = "tiger";  // <-- replace with your MySQL password

		            // Create and return the connection
		            return DriverManager.getConnection(url, user, pass);
		        }
		    }





		
	


