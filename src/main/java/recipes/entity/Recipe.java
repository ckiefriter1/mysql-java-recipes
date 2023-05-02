package recipes.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Recipe {
	private Integer recipeId;
	private String recipeName;
	private String notes;
	private Integer numServings;
	private LocalTime prepTime;
	private LocalTime cookTime;
	private LocalDateTime createdAt;
	
	private List<Ingredient> ingredients = new LinkedList<>();
	private List<Step> steps = new LinkedList<>();
	private List<category> categories = new LinkedList<>();
}
