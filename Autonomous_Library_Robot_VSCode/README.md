# Autonomous Library Robot — VS Code project

A starter full-stack project using **Java 17 + Spring Boot 3.2 + Spring Data JPA + MySQL**.
It includes a small browser dashboard served by Spring Boot, book catalogue CRUD, and a robot-task queue.

> Scope note: the uploaded PDF is a Java/Maven/MySQL troubleshooting conversation, not a complete functional specification. This ZIP therefore provides a practical starter for the Autonomous Library Robot project, based on the project name and the book/robot workflow. It does not implement physical robot control, RFID hardware, motor drivers, mapping, or ESP32 communication yet.

## 1. Requirements

- JDK 17
- Apache Maven 3.8+ (your PDF shows Maven 3.10.0 and Java 17)
- MySQL Server 8+
- VS Code with Extension Pack for Java

Check versions in a new VS Code terminal:

```powershell
java -version
mvn -version
```

## 2. Create the MySQL database

Open MySQL Workbench or the MySQL command line and execute:

```sql
CREATE DATABASE autonomous_library_robot;
```

Or run `database/create_database.sql`.

## 3. Configure the MySQL password

Open `src/main/resources/application.properties`.

The project uses environment variables:
- `DB_URL` defaults to localhost database `autonomous_library_robot` and includes `allowPublicKeyRetrieval=true`.
- `DB_USERNAME` defaults to `root`.
- `DB_PASSWORD` defaults to blank.

For a quick local run, edit this line and place your actual MySQL password after the colon:

```properties
spring.datasource.password=${DB_PASSWORD:}
```

Example (do not commit a real password to Git):

```properties
spring.datasource.password=your_actual_mysql_password
```

Alternatively, set it in PowerShell for the current terminal session:

```powershell
$env:DB_PASSWORD="your_actual_mysql_password"
```

The JDBC URL already includes:

```text
allowPublicKeyRetrieval=true&useSSL=false
```

This addresses the `Public Key Retrieval is not allowed` error shown in the uploaded PDF. If your MySQL setup requires SSL, configure SSL properly instead of disabling it.

## 4. Run in VS Code

1. Extract the ZIP.
2. VS Code → File → Open Folder → select `Autonomous_Library_Robot_VSCode`.
3. Open Terminal → New Terminal.
4. Confirm the terminal is at the project root (the folder containing `pom.xml`).
5. Start the application:

```powershell
mvn spring-boot:run
```

The first run downloads Maven dependencies. Keep the terminal open while the server runs.

Open the dashboard:

```text
http://localhost:8081
```

If port 8081 is already in use, change `server.port` in `application.properties`.

## 5. REST API endpoints

### Books

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/books` | List all books |
| GET | `/api/books?search=robot` | Search title or author |
| GET | `/api/books/{id}` | Get one book |
| POST | `/api/books` | Add a book |
| PUT | `/api/books/{id}` | Update a book |
| DELETE | `/api/books/{id}` | Delete a book |

Example POST body:

```json
{
  "title": "Introduction to Robotics",
  "author": "John Craig",
  "isbn": "9780000000000",
  "shelfCode": "A-03",
  "status": "AVAILABLE"
}
```

### Robot tasks

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/tasks` | List tasks |
| POST | `/api/tasks` | Create a task |
| PATCH | `/api/tasks/{id}/status` | Update task status |

Example POST body:

```json
{
  "taskType": "FETCH",
  "bookId": 1,
  "destination": "Counter 1"
}
```

Allowed task types used by the dashboard: `GUIDE`, `FETCH`, `RESHELVE`, `COURIER`.

Allowed task statuses: `PENDING`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`.

## 6. Database tables

Hibernate creates/updates these tables automatically:

- `books`: id, title, author, isbn, shelf_code, status
- `robot_tasks`: id, task_type, book_id, destination, status, created_at

For a development project, `spring.jpa.hibernate.ddl-auto=update` is enabled. For production, use database migrations instead.

## 7. Common issues

### `mvn` is not recognized

Your PDF shows Maven installed at:

```text
C:\Program Files\apache-maven-3.10.0-bin\apache-maven-3.10.0\bin\mvn.cmd
```

Open a new terminal and run:

```powershell
where mvn
mvn -version
```

If `mvn` is not recognized, add the Maven `bin` folder to the Windows Path, then close and reopen VS Code.

### `Public Key Retrieval is not allowed`

Confirm that the JDBC URL in `application.properties` contains:

```text
allowPublicKeyRetrieval=true&useSSL=false
```

Also verify MySQL is running, the database exists, and username/password are correct.

### `Access denied for user 'root'`

The MySQL password or username is incorrect. Update `DB_USERNAME` and `DB_PASSWORD`.

### `Communications link failure`

Make sure MySQL Server is running and listening on port 3306. The app's web server uses port 8081; MySQL normally uses 3306.

### Java version

The Maven output in your PDF shows `javac [debug release 17]` and Spring Boot starting with Java `17.0.18`. Java 17 is not the cause of the MySQL public-key error.

## 8. Hardware integration is a separate next step

This starter stores tasks in MySQL; it does **not** send commands to an ESP32 or operate motors. A hardware integration layer will need a communication method (for example, REST over Wi-Fi or MQTT), a defined message format, and robot-side firmware. Add that only after the backend and dashboard work correctly.
