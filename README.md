## 🧪 Local Dev (Backend)

### Requirements

* Docker
* Python 3

### Run Services

```bash
# Start specific service and its DB (e.g., IAM)
python3 scripts/up.py iam

# Start Test Order service and its DB
python3 scripts/up.py testorder

# Start all services (IAM, Patient, Test Order) and their DBs in detached mode
python3 scripts/up.py all
```

### Stream Logs

```bash
# Stream logs for a specific service (e.g., patient-service)
python3 scripts/log.py patient

# Stream logs for all services
python3 scripts/log.py all
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
python3 scripts/wipe.py
```

### Access Endpoints

  * IAM: `http://localhost:8080` (or `http://localhost:8080/iam` if context-path is set)
  * Patient: `http://localhost:8081` (or `http://localhost:8081/patient` if context-path is set)
  * Test Order: `http://localhost:8082`

### DB Credentials

| DB          | Port | User     | Pass      |
| ----------- | ---- | -------- | --------- |
| iam-db      | 5432 | postgres | bavui4444 |
| patient-db  | 5433 | postgres | bavui4444 |
| testorder-db| 5434 | postgres | bavui4444 |
