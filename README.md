# API Local Service Helper - Commands Management System

A minimal Spring Boot REST API for managing commands with IBM DB2.

## Quick Start

### Build
```bash
mvnw clean package
```

### Run
```bash
java -jar target/dev-command-hub-0.0.1-SNAPSHOT.jar
```

---

## API Endpoints

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/commands` | Get all commands |
| GET | `/api/commands/{id}` | Get command by ID |
| POST | `/api/commands` | Create new command |

---

## Examples

### Get All Commands
```bash
curl http://localhost:4000/api/commands
```

Response:
```json
[
  {
    "id": "git-clone",
    "app": "Git",
    "category": "VCS",
    "type": "command",
    "title": "Clone repository",
    "command": "git clone <repo_url>",
    "description": "Clone a remote repository",
    "tagsJson": "[\"git\",\"vcs\"]",
    "priority": 10
  },
  ...
]
```

### Get Specific Command
```bash
curl http://localhost:4000/api/commands/git-clone
```

### Create New Command
```bash
curl -X POST http://localhost:4000/api/commands \
  -H "Content-Type: application/json" \
  -d '{
    "id": "custom-cmd",
    "app": "MyApp",
    "category": "Build",
    "type": "command",
    "title": "Custom Build",
    "command": "make build",
    "description": "Build the project",
    "tagsJson": "[\"build\"]",
    "priority": 9
  }'
```

---

## Database

- **Type:** IBM DB2 11.5
- **URL:** jdbc:db2://localhost:50000/APISRHDB
- **User:** db2inst1
- **Table:** COMMANDS (manual creation required)

---

## Table Schema

```sql
CREATE TABLE commands (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    app VARCHAR(255),
    category VARCHAR(255),
    type VARCHAR(255),
    title VARCHAR(255),
    command CLOB,
    description CLOB,
    tags_json CLOB,
    priority INTEGER
);
```

---

## Configuration

**application.yml:**
```yaml
spring:
  datasource:
    url: jdbc:db2://localhost:50000/APISRHDB
    username: db2inst1
    password: devhub
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
server:
  port: 4000
```

---

## Status

✅ **PRODUCTION READY**

**Last Updated:** 2026-02-24
**Version:** 0.0.1-SNAPSHOT
