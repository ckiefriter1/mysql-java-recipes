SHOW DATABASES;

SHOW TABLES;

DESC recipe;

SELECT *
FROM recipe r;

SELECT COUNT(*)
FROM recipe r;

SELECT *
FROM recipe r
LIMIT 3;

SELECT recipe_id, recipe_name
FROM   recipe;

SELECT i.ingredient_name
FROM ingredient i
WHERE i.recipe_id = 4;

select * from recipe where recipe_id = 7;
select * from ingredient where recipe_id = 7;
select * from step where recipe_id =7;
select * from recipe_category where recipe_id =7;