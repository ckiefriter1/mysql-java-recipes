package recipes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import provided.util.DaoBase;
import recipes.entity.Recipe;
import recipes.exception.DbException;

public class RecipeDao extends DaoBase {
	private static final String RECIPE_TABLE = "recipe";
	private static final String INGREDIENT_TABLE = "ingredient";
	private static final String UNIT_TABLE = "unit";
	private static final String STEP_TABLE = "step";
	private static final String CATEGORY_TABLE = "category";
	private static final String RECIPE_CATEGORY = "recipe_category";

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
			} catch (Exception e) {
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
			} catch (Exception e) {
				this.rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public Optional<Recipe> findRecipeById(Integer recipeId) {
		// @formatter:off
		String sql = "SELECT * FROM recipe WHERE recipe_id = ?";
		// @formatter:on
		
		Recipe recipe = null;

		try(Connection conn = DbConnection.getConnection()) {
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setInt(1, recipeId.intValue());

				try(ResultSet rs = stmt.executeQuery()) {
					//List<Recipe> recipes = new LinkedList<>();

			        if (rs.next()) {
			        	recipe = this.extract(rs, Recipe.class);
			        }
				}
			} catch (IndexOutOfBoundsException e) {
				
				// Handled a Service layer through a NotFoundException.
				//throw new DbException("*** The Recipe ID you selected is not a valid recipe.");
			} catch (Exception e) {
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
		return Optional.ofNullable(recipe);
	}

	public List<Recipe> findAllRecipes() {
		// @formatter:off
		String sql = "SELECT * FROM recipe";
		// @formatter:on

		try(Connection conn = DbConnection.getConnection()) {
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				
				try(ResultSet rs = stmt.executeQuery()) {
					List<Recipe> recipes = new LinkedList<>();

			        while(rs.next()) {
			          recipes.add(this.extract(rs, Recipe.class));
			        }

			        return recipes;
				}
			} catch (Exception e) {
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

}
