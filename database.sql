CREATE DATABASE IF NOT EXISTS project_hybrid_store
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE project_hybrid_store;

-- Spring Boot / Hibernate creates and updates the product and store_orders tables
-- from the Java entity classes on the first run.
