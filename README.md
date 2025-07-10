## 🧪 Local Dev (Backend)

### Requirements

* Docker
* Python 3

### Run services

```bash
# IAM service + DB
python3 scripts/up.py iam

# Patient service + DB
python3 scripts/up.py patient

# All services
python3 scripts/up.py all
```

### Stop services

```bash
docker compose down     # stop
docker compose down -v  # stop + remove volumes
```

### Access

* IAM: `http://localhost:8080/iam`
* Patient: `http://localhost:8081/patient`

### DB Credentials

| DB         | Port | User     | Pass      |
| ---------- | ---- | -------- | --------- |
| iam-db     | 5432 | postgres | bavui4444 |
| patient-db | 5433 | postgres | bavui4444 |
