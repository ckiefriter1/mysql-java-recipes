package recipes.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import recipes.dao.RecipeDao;
import recipes.entity.Recipe;
import recipes.exception.DbException;

public class RecipeService {
	
	private static final String SCHEMA_FILE = "recipe_schema.sql";
	private static final String DATA_FILE = "recipe_data.sql";
	
	private RecipeDao recipeDao = new RecipeDao();

	public RecipeService() {		
	}
	
	public Recipe addRecipe(Recipe recipe) {
		return recipeDao.insertRecipe(recipe);
	}

	public void createAndPopulateTables() {
		loadFromFile(SCHEMA_FILE);
		loadFromFile(DATA_FILE);
		//System.out.println("Database tables created for Recipes database...");
	}

	private void loadFromFile(String fileName) {
		String content = readFileContent(fileName);
		List<String> sqlStatements = convertContentToSqlStatements(content);
		
		//sqlStatements.forEach(line -> System.out.println(line));
		recipeDao.executeBatch(sqlStatements);
		
	}

	private String readFileContent(String fileName) {
		try {
			Path path = Paths.get(getClass().getClassLoader().getResource(fileName).toURI());
			return Files.readString(path);
		} catch (URISyntaxException | IOException e) {
			throw new DbException(e);
		}
	}

	private List<String> convertContentToSqlStatements(String content) {
		content =  removeComments(content);
		content = replaceWhitespaceSequencesWithSingleSpace(content);
		return extractLinesFromContent(content);
	}

	private String removeComments(String content) {
		StringBuilder builder = new StringBuilder(content);
		int commentPos = 0;
		
		while((commentPos = builder.indexOf("-- ", commentPos)) != -1) {
			int eolPos = builder.indexOf("\n", commentPos + 1);
			
			if(eolPos == -1) {
				builder.replace(commentPos,  builder.length(), "");    // Case where comment is the last line of the file.
			}
			else {
				builder.replace(commentPos,  eolPos + 1, "");
			}
		}
		
		return builder.toString();
	}

	private String replaceWhitespaceSequencesWithSingleSpace(String content) {
		return content.replaceAll("\\s", " ");  // Uses regular expression to find all whitespace and replace with a single space.
	}

	private List<String> extractLinesFromContent(String content) {
		List<String> lines = new LinkedList<String>();
		
		while (!content.isEmpty()) {
			int semicolon = content.indexOf(";");
			
			if(semicolon == -1) {
				if (!content.isBlank()) {
					lines.add(content);
				}
				content = "";   // Causes us to exit loop.
			}
			else {
				lines.add(content.substring(0, semicolon).trim());  // pull out next line and add to list.
				content = content.substring(semicolon + 1);
			}
		}
		return lines;
	}

	/*
	public static void main (String[] args) {
		new RecipeService().createAndPopulateTables();
	}
	*/

}
