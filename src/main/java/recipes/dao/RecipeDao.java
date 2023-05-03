package recipes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.List;

import provided.util.DaoBase;
import recipes.entity.Recipe;
import recipes.exception.DbException;

public class RecipeDao extends DaoBase {
	private static final String RECIPE_TABLE     = "recipe";
	private static final String INGREDIENT_TABLE = "ingredient";
	private static final String UNIT_TABLE       = "unit";
	private static final String STEP_TABLE       = "step";
	private static final String CATEGORY_TABLE   = "category";
	private static final String RECIPE_CATEGORY  = "recipe_category";

	/*
	 * method t insert new records in the Recipe table.
	 */
	public Recipe insertRecipe(Recipe recipe) {
		// @formatter:off
		String sql = "" 
				+ "INSERT INTO " + RECIPE_TABLE + " "
				+ "(recipe_name, notes, num_servings, prep_time, cook_time) "
				+ "VALUES " 
				+ "(?, ?, ?, ?, ?)";
		// @formatter:on
				
		try (Connection conn = DbConnection.getConnection()) {
			this.startTransaction(conn);
			
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				this.setParameter(stmt, 1, recipe.getRecipeName(), String.class);
				this.setParameter(stmt, 2, recipe.getNotes(), String.class);
				this.setParameter(stmt, 3, recipe.getNumServings(), Integer.class);
				this.setParameter(stmt, 4, recipe.getPrepTime(), LocalTime.class);
				this.setParameter(stmt, 5, recipe.getCookTime(), LocalTime.class);
				
				stmt.executeUpdate();
				Integer recipeId = this.getLastInsertId(conn, RECIPE_TABLE);
				
				this.commitTransaction(conn);
				recipe.setRecipeId(recipeId);
				return recipe;
			}
			catch (Exception e) {
				this.rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

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
