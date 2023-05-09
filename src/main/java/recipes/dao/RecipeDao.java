package recipes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import provided.util.DaoBase;
import recipes.entity.Category;
import recipes.entity.Ingredient;
import recipes.entity.Recipe;
import recipes.entity.Step;
import recipes.entity.Unit;
import recipes.exception.DbException;

public class RecipeDao extends DaoBase {
	private static final String RECIPE_TABLE = "recipe";
	private static final String INGREDIENT_TABLE = "ingredient";
	private static final String UNIT_TABLE = "unit";
	private static final String STEP_TABLE = "step";
	private static final String CATEGORY_TABLE = "category";
	private static final String RECIPE_CATEGORY_TABLE = "recipe_category";

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


	public List<Recipe> findAllRecipes() {
		// @formatter:off
		String sql = "SELECT * FROM recipe ORDER by recipe_name";
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

	
	public Optional<Recipe> findRecipeById(Integer recipeId) {
		// @formatter:off
		String sql = "SELECT * FROM recipe WHERE recipe_id = ?";
		// @formatter:on
		
		Recipe recipe = null;

		try(Connection conn = DbConnection.getConnection()) {
			this.startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setInt(1, recipeId.intValue());

				try(ResultSet rs = stmt.executeQuery()) {
					//List<Recipe> recipes = new LinkedList<>();

			        if (rs.next()) {
			        	recipe = this.extract(rs, Recipe.class);
			        }
				}
				
				// Get other dependent data that's part of the recipe.
				if (Objects.nonNull(recipe)) {
					recipe.getIngredients().addAll(fetchRecipeIngredients(conn, recipeId));
					recipe.getSteps().addAll(fetchRecipeSteps(conn, recipeId));
					recipe.getCategories().addAll(fetchRecipeCategories(conn, recipeId));
				}
			} catch (IndexOutOfBoundsException e) {
				
				// Handled a Service layer through a NotFoundException.
				//throw new DbException("*** The Recipe ID you selected is not a valid recipe.");
			} catch (Exception e) {
				this.rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
		return Optional.ofNullable(recipe);
	}

	private List<Category> fetchRecipeCategories(Connection conn, Integer recipeId) throws SQLException {
		// @formatter:off
		String sql = "SELECT c.* " 
		           + "  FROM " + RECIPE_CATEGORY_TABLE + " rc " 
		           + "JOIN " + CATEGORY_TABLE + " c USING (category_id) "
				   + "WHERE rc.recipe_id = ? "
		           + "ORDER by c.category_name";
		// @formatter:on

		try(PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, recipeId.intValue());
			
			try(ResultSet rs = stmt.executeQuery()) {
				List<Category> categories = new LinkedList<>();

		        while(rs.next()) {
		          categories.add(this.extract(rs, Category.class));
		        }

		        return categories;
			}
		} catch (Exception e) {
			throw new DbException(e);
		}

	}

	private List<Step> fetchRecipeSteps(Connection conn, Integer recipeId) throws SQLException {
		// @formatter:off
		String sql = "SELECT * " 
		           + "  FROM " + STEP_TABLE + " " 
				   + "WHERE recipe_id = ? "
		           + "ORDER by step_order";
		// @formatter:on

		try(PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, recipeId.intValue());
			
			try(ResultSet rs = stmt.executeQuery()) {
				List<Step> steps = new LinkedList<>();

		        while(rs.next()) {
		          steps.add(this.extract(rs, Step.class));
		        }

		        return steps;
			}
		} catch (Exception e) {
			throw new DbException(e);
		}

	}

	private List<Ingredient> fetchRecipeIngredients(Connection conn, Integer recipeId) throws SQLException {
		// @formatter:off
		String sql = "SELECT i.*, u.unit_name-singular, u.unit_name-plural " 
		           + "  FROM " + INGREDIENT_TABLE + " i " 
				   + "LEFT JOIN " + UNIT_TABLE + " u USING (unit_id) "
				   + "WHERE recipe_id = ? "
		           + "ORDER by i.ingredient_order";
		// @formatter:on

		try(PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, recipeId.intValue());
			
			try(ResultSet rs = stmt.executeQuery()) {
				List<Ingredient> ingredients = new LinkedList<>();

		        while(rs.next()) {
		          Ingredient ingredient = this.extract(rs, Ingredient.class);
		          Unit unit = this.extract(rs, Unit.class);
		          
		          ingredient.setUnit(unit);
		          ingredients.add(ingredient);
		        }

		        return ingredients;
			}
		} catch (Exception e) {
			throw new DbException(e);
		}

	}

}
