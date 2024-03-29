package recipes;

//import java.sql.Connection;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import recipes.entity.Category;
import recipes.entity.Ingredient;
//import recipes.dao.DbConnection;
import recipes.entity.Recipe;
import recipes.entity.Step;
import recipes.exception.DbException;
import recipes.service.RecipeService;

public class Recipes {

	private Scanner scanner = new Scanner(System.in);
	private RecipeService recipeService = new RecipeService();
	private Recipe curRecipe = null;

	// @formatter:off
	private List<String> operations = List.of(
			"1) Create and populate tables",
			"2) Add a recipe",
			"3) List recipes",
			"4) Select working recipe"
			);
	
	// @formatter:on

	public static void main(String[] args) {
		new Recipes().displayMenu();

	}
	

	private void displayMenu() {
		boolean done = false;
		
		while (!done) {
			
			try {
				int operation = getOperation();
				
				switch (operation) {
				case -1:
					done = exitMenu();
					break;
				
				case 1:
					createTables();
					break;
					
				case 2:
					addRecipe();
					break;
					
				case 3:
					displayAllRecipes();
					break;
					
				case 4:
					displayAllRecipes();
					displaySpecificRecipe();
					break;
					
				default:
					System.out.println("\n" + operation + " is not valid. Try again");
					break;
				}
			} catch(Exception e) {
				System.out.println("\nError: " + e.toString() + "\n Try again");
			}
		}
		
	}

	private void setCurrentRecipe(Recipe recipe) {
		this.curRecipe = recipe;
		
	}

	private void displaySpecificRecipe() {
		Integer recipeId = getIntInput("Enter a Recipe ID:");
		Recipe recipe = recipeService.findRecipeById(recipeId);
		this.setCurrentRecipe(recipe);
		
		System.out.println("\n----------------------------------------------------");
		System.out.println("************       Recipe Details      *************");
		System.out.println("----------------------------------------------------");
		
		System.out.println("Recipe ID: " + recipe.getRecipeId());
		System.out.println("Recipe Name: " + recipe.getRecipeName());
		System.out.println("Notes: " + recipe.getNotes());
		System.out.println("Number of Servings: " + recipe.getNumServings());
		System.out.println("Prep Time: " + recipe.getPrepTime());
		System.out.println("Cook Time: " + recipe.getCookTime());
		System.out.println("Created at: " + recipe.getCreatedAt());
		
		System.out.println("Ingredients:");
		List<Ingredient> ingredients = recipe.getIngredients();
		Iterator<Ingredient> iListIterator = ingredients.iterator();
		while (iListIterator.hasNext()) {
			Ingredient ingredient = iListIterator.next();
			System.out.println("   ID: " + ingredient.getIngredientId() + ", " + ingredient.getIngredientName());
			//System.out.println(ingredient.toString());
		}
		
		System.out.println("Steps:");
		List<Step> steps = recipe.getSteps();
		Iterator<Step> sListIterator = steps.iterator();
		while (sListIterator.hasNext()) {
			Step step = sListIterator.next();
			System.out.println("   ID: " + step.getStepId() + ", " + step.getStepText());
			//System.out.println(step.toString());
		}
		
		System.out.println("Categories:");
		List<Category> categories = recipe.getCategories();
		Iterator<Category> cListIterator = categories.iterator();
		while (cListIterator.hasNext()) {
			Category category = cListIterator.next();
			System.out.println("   ID: " + category.getCategoryId() + ", " + category.getCategoryName());
			//System.out.println(category.toString());
		}
		
	}

	private void displayAllRecipes() {
		
		System.out.println("\n----------------------------------------------------");
		System.out.println("************       Recipe List       ***************");
		System.out.println("----------------------------------------------------");
		
		List<Recipe> recipeList = recipeService.findAllRecipes();
		Iterator<Recipe> listIterator = recipeList.iterator();
		while (listIterator.hasNext()) {
			Recipe recipe = listIterator.next();
			System.out.println("Recipe ID: " + recipe.getRecipeId() + ", Recipe Name: " + recipe.getRecipeName());
		}
		
		System.out.println("----------------------------------------------------");
		System.out.println("Total # of Recipes: " + recipeList.size());
		System.out.println("----------------------------------------------------");
				
		
	}

	private void addRecipe() {
		String name = getStringInput("Enter the recipe name");	
		String notes = getStringInput("Enter the recipe notes");
		Integer numServings = getIntInput("Enter number of servings");
		Integer prepMinutes = getIntInput("Enter prep time in minutes");
		Integer cookMinutes = getIntInput("Enter cook time in minutes");
		
		LocalTime prepTime = minutesToLocalTime(prepMinutes);
		LocalTime cookTime = minutesToLocalTime(cookMinutes);
		
		Recipe recipe = new Recipe();
		
		recipe.setRecipeName(name);
		recipe.setNotes(notes);
		recipe.setNumServings(numServings);
		recipe.setPrepTime(prepTime);
		recipe.setCookTime(cookTime);
		
		Recipe dbRecipe = recipeService.addRecipe(recipe);
		System.out.println("You added this recipe:\n" + dbRecipe);
		this.curRecipe = recipeService.findRecipeById(dbRecipe.getRecipeId());
	}

	private LocalTime minutesToLocalTime(Integer numMinutes) {
		int min = Objects.isNull(numMinutes) ? 0 : numMinutes;
		int hours = min / 60;
		int minutes = min % 60;
		
		return LocalTime.of(hours, minutes);
	}

	private void createTables() {
		recipeService.createAndPopulateTables();
		System.out.println("\n Tables created and populated");
	}

	private boolean exitMenu() {
		System.out.println("\nExiting the menu.  TTFN!");
		return true;
	}

	private int getOperation() {
		printOperations();
		Integer op = getIntInput("\nEnter an operation number (press Enter to quit)");
		return Objects.isNull(op) ? -1 : op;
	}

	private void printOperations() {
		System.out.println();
		System.out.println("*** Here's what you can do:");

		operations.forEach(op -> System.out.println("   " + op));
	}

	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}

		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + "is not a valid number.");
		}
	}

	private Double getDoubleInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}

		try {
			return Double.parseDouble(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + "is not a valid number.");
		}
	}

	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String line = this.scanner.nextLine();
		return line.isBlank() ? null : line.trim();
	}

}
