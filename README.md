# API Local Service Helper - Commands Management System

A Spring Boot REST API for managing commands with IBM DB2, containerized with Docker and Kubernetes-ready.

**Status:** ✅ Production-Ready | 🐳 Docker Ready | ☸️ Kubernetes Ready

---

## 🚀 Quick Start (3 Commands)

```bash
# 1. Create environment file
Copy-Item .env.local .env

# 2. Edit with your database credentials
notepad .env

# 3. Deploy with one command
.\deploy.bat up
```

**App will be running at:** `http://localhost:4000`

---

## 📋 Prerequisites

- **Windows 10/11** with Docker Desktop
- **Java 17+** (or use Maven wrapper `mvnw.cmd`)
- **IBM DB2** running (e.g., on port 50000)
- **Git** (for version control)

---

## 🔧 Setup Instructions

### Step 1: Clone Repository
```bash
git clone <repository-url>
cd api-local-service-helper
```

### Step 2: Create Environment File
```powershell
# Create .env from template
Copy-Item .env.local .env
```

### Step 3: Configure Database Credentials
```powershell
# Edit .env with your database details
notepad .env
```

Update these values:
```env
DB_URL=your_actual_url
DB_USER=your_actual_user
DB_PASSWORD=your_actual_password
SERVER_PORT=4000
```

**Note:** Use `host.docker.internal` to connect Docker container to host machine's database

### Step 4: Deploy
```powershell
# Deploy with automated script (Windows)
.\deploy.bat up

# OR manual deployment
mvnw.cmd clean package -DskipTests
docker build -t api-local-service:latest .
docker-compose -f docker-compose.local.yml up -d
```

### Step 5: Verify Deployment
```powershell
# Check container status
docker ps

# View logs
docker-compose -f docker-compose.local.yml logs api-local-service

# Test API
curl http://localhost:4000/api/commands
```

---

## 🎮 Common Commands

```powershell
# Deploy
.\deploy.bat up

# Stop
.\deploy.bat down

# Restart
.\deploy.bat restart

# View logs
.\deploy.bat logs

# Test API
.\deploy.bat test
```

---

## 📚 API Endpoints

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/commands` | Get all commands |
| GET | `/api/commands/{id}` | Get command by ID |
| POST | `/api/commands` | Create new command |
| PUT | `/api/commands/{id}` | Update command |
| DELETE | `/api/commands/{id}` | Delete command |

### Examples

**Get All Commands:**
```bash
curl http://localhost:4000/api/commands
```

**Get Specific Command:**
```bash
curl http://localhost:4000/api/commands/git-clone
```

**Create New Command:**
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
    "priority": 9
  }'
```

**Health Check:**
```bash
curl http://localhost:4000/actuator/health
```

---

## 🗄️ Database Configuration

- **Type:** IBM DB2 11.5+
- **Host:** localhost (or your server)
- **Port:**  your_db_port (default 50000)
- **Database:** your_db_name
- **Default User:**  your_db_user

### Table Schema

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

## 🔐 Environment Variables

All configuration is managed through environment variables (no hardcoded secrets):

| Variable | Required | Example                                             |
|----------|----------|-----------------------------------------------------|
| `DB_URL` | Yes | `jdbc:db2://host.docker.internal:50000/{you_table}` |
| `DB_USER` | Yes | `your_db_user`                                      |
| `DB_PASSWORD` | Yes | Your database password                              |
| `SERVER_PORT` | No | `4000`                                              |

**Setup:** Create `.env` file in project root with these values

---

## 🐳 Docker Deployment

### Local Docker Deployment
```powershell
# Build image
docker build -t api-local-service:latest .

# Deploy with Docker Compose
docker-compose -f docker-compose.local.yml up -d

# View status
docker ps
```

### Docker Compose (All-in-One)
```powershell
# Deploy (loads .env automatically)
docker-compose -f docker-compose.local.yml up -d

# Stop
docker-compose -f docker-compose.local.yml down

# View logs
docker-compose -f docker-compose.local.yml logs -f api-local-service
```

---

## ☸️ Kubernetes/Rancher Deployment

For production deployment to Rancher or Kubernetes:

1. **Push Docker image to registry:**
   ```powershell
   docker tag api-local-service:latest your-registry/api-local-service:v1.0
   docker push your-registry/api-local-service:v1.0
   ```

2. **Use Kubernetes manifest:**
   ```bash
   kubectl apply -f k8s-deployment.yaml
   ```

3. **Configure secrets in Rancher UI** (recommended over hardcoding)

See `LOCAL_DEPLOYMENT_GUIDE.md` for detailed Rancher instructions.

---

## 📖 Documentation

| Document | Purpose |
|----------|---------|
| **QUICK_START.md** | 5-minute setup guide |
| **LOCAL_DEPLOYMENT_GUIDE.md** | Complete deployment reference |
| **ENVIRONMENT_VARIABLES.md** | Environment variable setup |
| **DEPLOYMENT_CHECKLIST.md** | Step-by-step verification |
| **k8s-deployment.yaml** | Kubernetes deployment manifest |

---

## 🆘 Troubleshooting

### "Connection refused" Error
```
✓ Verify DB2 is running: netstat -ano | findstr :50000
✓ Check .env credentials are correct
✓ Use host.docker.internal instead of localhost for Docker
```

### "Port 4000 already in use"
```
✓ Edit .env and change: SERVER_PORT=8080
✓ Or kill process: taskkill /PID <PID> /F
```

### "Docker not found"
```
✓ Install Docker Desktop: https://www.docker.com/products/docker-desktop
✓ Restart PowerShell after installation
```

### Container keeps restarting
```
✓ Check logs: docker-compose -f docker-compose.local.yml logs
✓ Verify database connectivity
✓ Ensure credentials in .env are correct
```

See `LOCAL_DEPLOYMENT_GUIDE.md` → Troubleshooting for more help.

---

## 🔒 Security Notes

- ✅ **No hardcoded passwords** - Use `.env` file (git-ignored)
- ✅ **Template provided** - `.env.local` is safe to commit
- ✅ **Secure by default** - Environment variables system in place
- ⚠️ **Never commit .env** - It contains your password!

---

## 📁 Project Structure

```
api-local-service-helper/
├── src/
│   ├── main/java/io/devhub/apilocalservicehelper/
│   │   ├── DevCommandHubApplication.java
│   │   └── commandservice/
│   │       ├── Command.java
│   │       ├── CommandRepository.java
│   │       ├── CommandService.java
│   │       └── CommandController.java
│   └── main/resources/
│       └── application.properties
├── docker-compose.local.yml      # Docker Compose config
├── Dockerfile                     # Container definition
├── k8s-deployment.yaml            # Kubernetes manifest
├── deploy.bat                     # Deployment script
├── .env.local                     # Environment template
├── .env                           # Your credentials (don't commit!)
└── pom.xml                        # Maven configuration
```

---

## 🏗️ Technology Stack

- **Framework:** Spring Boot 3.5.4
- **Language:** Java 17
- **Database:** IBM DB2
- **Container:** Docker & Docker Compose
- **Orchestration:** Kubernetes/Rancher Ready
- **Build:** Maven 3.8+

---

## ✅ Deployment Checklist

Before deployment:
- [ ] Docker Desktop installed and running
- [ ] `.env` file created from `.env.local`
- [ ] Database credentials configured in `.env`
- [ ] Database is accessible/running
- [ ] Port 4000 is available

After deployment:
- [ ] Container is running: `docker ps`
- [ ] API responds: `curl http://localhost:4000/api/commands`
- [ ] Logs show no errors: `.\deploy.bat logs`
- [ ] Health check passes: `curl http://localhost:4000/actuator/health`

---

## 📝 Development

### Run Tests
```bash
mvnw.cmd test
```

### Build Without Tests
```bash
mvnw.cmd clean package -DskipTests
```

### Run Locally (No Docker)
```powershell
$env:DB_PASSWORD = "your_password"
$env:DB_URL = "jdbc:db2://localhost:50000/APISRHDB"
java -jar target/dev-command-hub-0.0.1-SNAPSHOT.jar
```

### Run in IDE
1. Create run configuration in IntelliJ/Eclipse
2. Add environment variables (see ENVIRONMENT_VARIABLES.md)
3. Or use EnvFile plugin to load from `.env`

---

## 📞 Support

- **Setup Issues:** See `QUICK_START.md`
- **Deployment Issues:** See `LOCAL_DEPLOYMENT_GUIDE.md`
- **Environment Variables:** See `ENVIRONMENT_VARIABLES.md`
- **All Documentation:** See `INDEX.md`

---

## 📄 License

[Your License Here]

---

## 👥 Contributors

[Your Team/Contributors Here]

---

**Last Updated:** February 25, 2026  
**Version:** 0.0.1-SNAPSHOT  
**Status:** ✅ Production Ready
