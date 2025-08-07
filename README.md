## 🧪 Local Dev (Backend)

### Requirements

* Docker
* Python 3

### Run Services

```bash
# Start specific service
python scripts/up.py <service>

# Start all services (IAM, Patient, Test Order) in detached mode
python scripts/up.py all
```

### Reset Services

```bash
# Reset specific service after code changes (stops, removes, rebuilds, starts)
python scripts/reset.py <service>

# Reset all services
python scripts/reset.py all
```

### Stream Logs

```bash
# Stream logs for a specific service
python scripts/log.py <service>

# Stream logs for all services
python scripts/log.py all
```

### Stop Services

```bash
docker compose down        # Stop services
docker compose down -v     # Stop and remove volumes
```

### Cleanup Docker Environment

```bash
# Remove all containers, images, and volumes
# Note that this will make the next time you spin up services slower
python scripts/wipe.py
```

### Access Endpoints

* IAM: `http://localhost:8080/iam`
* Patient: `http://localhost:8081/patient`
* Test Order: `http://localhost:8082/testorder`

### Database

All services share a single PostgreSQL database:

| DB          | Port | User     | Pass      | Database     |
| ----------- | ---- | -------- | --------- | ------------ |
| postgres    | 5432 | postgres | bavui4444 | microservices |
