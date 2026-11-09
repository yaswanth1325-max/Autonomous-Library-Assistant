CREATE DATABASE IF NOT EXISTS autonomous_library_robot
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE autonomous_library_robot;

-- Spring Data JPA creates/updates the books and robot_tasks tables
-- automatically because spring.jpa.hibernate.ddl-auto=update.
