use recipes;

-- Grilled Steak
INSERT INTO recipe (recipe_name, notes, num_servings, prep_time, cook_time) VALUES ('Grilled Steak', 'My favorite grilled steak recipe.', 4, '00:20', '00:40');

INSERT INTO ingredient (recipe_id, unit_id, ingredient_name, instruction, ingredient_order, amount) VALUES (7, 4, 'chocolate cake mix', null, 1, 16.25);
INSERT INTO ingredient (recipe_id, unit_id, ingredient_name, instruction, ingredient_order, amount) VALUES (7, 4, 'yellow cake mix', null, 2, 16.25);
INSERT INTO ingredient (recipe_id, unit_id, ingredient_name, instruction, ingredient_order, amount) VALUES (7, 4, 'vanilla instant pudding mix', null, 3, 5.1);
INSERT INTO ingredient (recipe_id, unit_id, ingredient_name, instruction, ingredient_order, amount) VALUES (7, 4, 'vanilla sandwich cookies', null, 4, 25);
INSERT INTO ingredient (recipe_id, unit_id, ingredient_name, instruction, ingredient_order, amount) VALUES (7, null, 'green food coloring', null, 5, null);
INSERT INTO ingredient (recipe_id, unit_id, ingredient_name, instruction, ingredient_order, amount) VALUES (7, 8, 'Tootsie Rolls', null, 6, 15);
INSERT INTO ingredient (recipe_id, unit_id, ingredient_name, instruction, ingredient_order, amount) VALUES (7, null, 'new kitty litter pan', null, 7, 1);
INSERT INTO ingredient (recipe_id, unit_id, ingredient_name, instruction, ingredient_order, amount) VALUES (7, null, 'new Pooper Scooper', null, 8, 1);

INSERT INTO step (recipe_id, step_order, step_text) VALUES (7, 1, 'Prepare cake mixes and bake according to directions (any size pans but 13×9 is easiest). Prepare pudding mix and chill until ready to assemble. Crumble sandwich cookies in small batches in food processor, scraping often. Set aside all but about 1/4 cup. To the 1/4 cup cookie crumbs, add a few drops green food coloring and mix using fingers until an even green color.');
INSERT INTO step (recipe_id, step_order, step_text) VALUES (7, 2, 'When cakes are cooled to room temperature, crumble into the new litter box. Toss with half the remaining cookie crumbs and the chilled pudding. Mix in just enough of the pudding to moisten it.');
INSERT INTO step (recipe_id, step_order, step_text) VALUES (7, 3, 'You don’t want it soggy. Combine gently.');
INSERT INTO step (recipe_id, step_order, step_text) VALUES (7, 4, 'Put three unwrapped Tootsie rolls in a microwave safe dish and heat until soft and pliable. Shape ends so they are no longer blunt, curving slightly. Repeat with 3 more Tootsie rolls and bury in mixture. Sprinkle the other half of cookie crumbs over top.');
INSERT INTO step (recipe_id, step_order, step_text) VALUES (7, 5, 'Scatter the green cookie crumbs lightly over the top. (This is supposed to look like the chlorophyll in kitty litter.) Heat 3 Tootsie Rools in the microwave until almost melted. Scrape them on top of the cake. Sprinkle with cookie crumbs. Spread remaining Tootsie Rolls over the top. Take one and heat until pliable. Hang it over the side of the kitty litter box, sprinkling it lightly with cookie crumbs. Serve with new pooper scooper.');

INSERT INTO recipe_category (recipe_id, category_id) VALUES (7, 2);
INSERT INTO recipe_category (recipe_id, category_id) VALUES (7, 3);

commit;



