package recipes.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class DbConnectionTest {

	public static void main(String[] args) {
		
		try (Connection conn = DbConnection.getConnection()) {
			System.out.println("*** Database Connection established");
			System.out.println("Current Schema: " + conn.getCatalog());
		} catch (SQLException e) {
			System.out.println(e);
		}

	}

}
