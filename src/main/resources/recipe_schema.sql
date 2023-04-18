-- Specify the schema to be used.
use recipes;

-- Drop tables first so you can run this SQL script multile times to 
-- re-create the tables.  You always drop dependent (child) tables first.
DROP TABLE IF EXISTS ingredient;
DROP TABLE IF EXISTS step;
DROP TABLE IF EXISTS recipe_category;
DROP TABLE IF EXISTS unit;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS recipe;


-- Create recipe table.
create table recipe (
  recipe_id INT auto_increment not null,
  recipe_name VARCHAR(128) not null,
  notes TEXT,
  num_servings INT,
  prep_time TIME,
  cook_time TIME,
  created_at TIMESTAMP not null default CURRENT_TIMESTAMP,
  primary key (recipe_id)
);

-- Create category table.
CREATE TABLE category (
  category_id INT AUTO_INCREMENT NOT NULL,
  category_name VARCHAR(64) NOT null,
  primary key (category_id)
);

-- Create unit table.
CREATE TABLE unit (
  unit_id INT AUTO_INCREMENT NOT NULL,
  unit_name_singular VARCHAR(32) NOT NULL,
  unit_name_plural VARCHAR(34) NOT null,
  primary key (unit_id)
);

-- Join table for recipe and category tables.
create table recipe_category (
  recipe_id INT not null,
  category_id INT not null,
  foreign key (recipe_id) references recipe (recipe_id),
  foreign key(category_id) references category (category_id),
  unique key (recipe_id,
category_id)
);

-- Create step table.
CREATE TABLE step (
  step_id INT AUTO_INCREMENT NOT NULL,
  recipe_id INT NOT NULL,
  step_order INT NOT NULL,
  step_text TEXT NOT null,
  PRIMARY KEY (step_id),
  FOREIGN KEY (recipe_id) references recipe (recipe_id) on delete cascade
);

-- Create ingredient table.
CREATE TABLE ingredient (
  ingredient_id INT AUTO_INCREMENT NOT NULL,
  recipe_id INT NOT NULL,
  unit_id INT,
  ingredient_name VARCHAR(64),
  instruction VARCHAR(64),
  ingredient_order INT NOT NULL,
  amount DECIMAL(7, 2),
  primary key (ingredient_id),
  foreign key (recipe_id) references recipe (recipe_id) on delete cascade,
  foreign key (unit_id) references unit (unit_id) 
);