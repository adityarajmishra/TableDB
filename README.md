# SQL-Like Command-Line Database

A lightweight, thread-safe SQL-inspired database implementation in Java that supports basic SQL operations through a command-line interface.

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)

![Java Version](https://img.shields.io/badge/Java-17-blue)

![License](https://img.shields.io/badge/license-MIT-green)

## 🚀 Features

- Create and manage tables with defined schemas

- Support for basic data types (INT, STRING)

- CRUD operations (Create, Read, Update, Delete)

- Thread-safe operations

- Persistence support

- Concurrent access handling

- Command-line interface

- Modern Java features (Streams, Lambdas)

## 📋 Requirements

- Java 17 or higher

- Gradle 8.5 or higher

- 512MB RAM minimum

- Any OS (Windows/Linux/MacOS)

## 🛠 Installation

1\. Clone the repository:

```bash

git clone https://github.com/yourusername/sql-database.git

cd sql-database

```

2\. Build the project:

```bash

./gradlew clean build

```

3\. Run the application:

```bash

./gradlew run

```

## 💡 Usage

The database supports the following SQL-like commands:

### Creating a Table

```sql

CREATE_TABLE employees (id INT, name STRING, department STRING)

```

### Inserting Data

```sql

INSERT INTO employees VALUES (1, "John Doe", "HR")

```

### Querying Data

```sql

SELECT * FROM employees WHERE department = "HR"

```

### Updating Data

```sql

UPDATE employees SET department = "Finance" WHERE id = 1

```

### Deleting Data

```sql

DELETE FROM employees WHERE id = 1

```

### Listing Tables

```sql

SHOW TABLES

```

### Stopping the Database

```sql

STOP

```

## 📝 Example Session

```sql

> CREATE_TABLE users (id INT, name STRING)

SUCCESS

> INSERT INTO users VALUES (1, "John Doe")

SUCCESS

> SELECT * FROM users WHERE id = 1

1, John Doe

> UPDATE users SET name = "Jane Doe" WHERE id = 1

UPDATED 1

> DELETE FROM users WHERE id = 1

DELETED 1

> STOP

Goodbye!

```

## 🏗 Architecture

The project follows a modular architecture with the following key components:

```

src/

├── main/

│   └── java/

│       └── com/

│           └── tabledb/

│               ├── command/     # Command processing

│               ├── condition/   # Query conditions

│               ├── core/        # Core database logic

│               ├── exception/   # Custom exceptions

│               ├── model/       # Data models

│               └── util/        # Utilities

```

### Key Components:

- **CommandProcessor**: Handles command parsing and execution

- **Database**: Core database operations

- **Table**: Table structure and operations

- **Condition**: Query condition handling

- **Row**: Data storage structure

## 🔒 Thread Safety

The implementation ensures thread safety through:

- Use of `ConcurrentHashMap` for table storage

- `ReadWriteLock` for table operations

- Atomic operations for critical sections

- Proper synchronization for file operations

## ⚡ Performance

- O(1) table lookups

- O(n) for full table scans

- O(log n) for indexed operations

- Efficient memory usage through smart data structures

## 🧪 Testing

Run the test suite:

```bash

./gradlew test

```

The project includes:

- Unit tests

- Integration tests

- Concurrency tests

- Edge case handling

## 🔧 Advanced Configuration

The database can be configured through environment variables:

```bash

DB_MAX_TABLES=100          # Maximum number of tables

DB_MAX_ROWS=1000000       # Maximum rows per table

DB_PERSIST_PATH=/data/db  # Data persistence location

```

## 🤝 Contributing

1\. Fork the repository

2\. Create a feature branch

3\. Commit your changes

4\. Push to the branch

5\. Create a Pull Request


## 🙏 Acknowledgments

- Inspired by traditional SQL databases

- Built with modern Java practices

- Uses industry-standard design patterns

## 🐛 Bug Reporting

Please report bugs by:

1\. Creating a minimal reproduction case

2\. Opening an issue with clear steps to reproduce

3\. Including relevant system information

4\. Providing logs if applicable

## 📊 Project Status

Currently in active development with:

- All basic SQL operations implemented

- Thread safety ensured

- Comprehensive test coverage

- Production-ready core features

## 🔜 Roadmap

Planned features for future releases:

1\. **Enhanced Data Types**

   - FLOAT/DOUBLE support

   - DATE/TIME support

   - BOOLEAN support

2\. **Performance Improvements**

   - Indexing support

   - Query optimization

   - Caching layer

3\. **Additional Features**

   - Transaction support

   - Backup/restore capabilities

   - Network interface

4\. **Developer Tools**

   - CLI improvements

   - Admin dashboard

   - Monitoring tools
