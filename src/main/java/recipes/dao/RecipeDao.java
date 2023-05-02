package recipes.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import provided.util.DaoBase;
import recipes.exception.DbException;

public class RecipeDao extends DaoBase {

	/*
	 * This method will execute a batch of SQL commands from the List of commands
	 */
	public void executeBatch(List<String> sqlBatch) {
		try (Connection conn = DbConnection.getConnection()) {
			this.startTransaction(conn);
			
			try (Statement stmt = conn.createStatement()) {
				for (String sql : sqlBatch) {
					stmt.addBatch(sql);
				}
				stmt.executeBatch();
				this.commitTransaction(conn);
			}
			catch (Exception e) {
				this.rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}
}
