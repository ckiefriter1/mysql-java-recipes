package recipes;

import java.sql.Connection;

import recipes.dao.DbConnection;

public class Recipes {

	public static void main(String[] args) {
		Connection conn = DbConnection.getConnection();

	}

}
