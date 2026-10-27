# Campus Equipment Booking Service
Java 17 / Spring Boot REST API for a CI/CD lab assessment.

## Build and test
`mvn clean test`

## Run
`mvn spring-boot:run` — port 8080.

## Endpoints
- `GET /api/equipment`
- `GET /api/equipment/{id}`
- `POST /api/bookings`
- `GET /api/bookings/{id}`
- `POST /api/bookings/{id}/cancel`
- `GET /actuator/health`

Inventory is in memory and resets on restart. No database is required.
# Campus Equipment Booking Service

## 1. Project Overview

The Campus Equipment Booking Service is a Java Spring Boot REST API for managing campus equipment and booking requests. It maintains equipment availability and booking records, supports booking cancellation, and exposes a health-check endpoint using Spring Boot Actuator.

### Technologies Used
- Java 21
- Spring Boot
- Maven
- Git and GitHub
- Jenkins Pipeline
- Docker
- Kubernetes using Minikube

## 2. Assigned Change Request

**Change Request: Fix booking capacity validation and ensure sequential inventory consistency.**

The original implementation contained an off-by-one error in booking capacity validation. It allowed a booking quantity that was one unit greater than the available inventory.

The validation was corrected to reject any request whose quantity exceeds the currently available inventory. The service also rejects zero or negative quantities.

The booking operation updates inventory only after the request passes validation and creates a confirmed booking only for a successful request. Synchronization ensures that sequential booking operations use the latest inventory value.

### Tests Added and Updated

The test suite verifies:
- A valid booking reduces available inventory.
- Zero quantity is rejected.
- Blank borrower IDs and unknown equipment are rejected.
- A booking equal to the available inventory is accepted.
- A booking exceeding available inventory is rejected.
- A rejected booking does not change inventory or create a confirmed booking.
- Sequential bookings within the available inventory are accepted.
- Sequential bookings that collectively exceed inventory are rejected.
- Cancellation restores inventory only once.

## 3. Build and Test

### Run Tests

From the project root directory:

```bash
mvn clean test
```

### Package the Application

```bash
mvn clean package
```

The executable JAR is generated at:

```text
target/campus-equipment-booking-1.0.0.jar
```

## 4. Docker Build and Run

### Build the Docker Image

Run Maven packaging first, then execute:

```bash
docker build -t campus-equipment-booking:set-g .
```

Verify the image:

```bash
docker images campus-equipment-booking
```

### Run the Container

Port 8080 may already be occupied by Jenkins. To run the application locally on port 8081:

```bash
docker run -d --name campus-booking-app -p 8081:8080 campus-equipment-booking:set-g
```

Verify the running container:

```bash
docker ps
```

### Health Check

Open the following URL in a browser:

```text
http://localhost:8081/actuator/health
```

Alternatively, run:

```bash
curl.exe --max-time 30 http://localhost:8081/actuator/health
```

Expected response:

```json
{
  "status": "UP",
  "groups": ["liveness", "readiness"]
}
```

## 5. Jenkins CI Pipeline

A `Jenkinsfile` is maintained in the project root. The Jenkins Pipeline retrieves the source code from GitHub, runs the Maven test stage, packages the application, and archives the generated JAR.

### Pipeline Stages
1. Checkout the GitHub repository and selected feature branch.
2. Execute `mvn clean test`.
3. Execute `mvn package`.
4. Archive the generated JAR as a Jenkins build artifact.

The pipeline was executed successfully, with the build finishing with `SUCCESS`.

## 6. Kubernetes Deployment Using Minikube

Ensure Minikube is running:

```bash
minikube status
kubectl get nodes
```

Load the locally built Docker image into Minikube:

```bash
minikube image load campus-equipment-booking:set-g
```

Apply the Kubernetes manifests:

```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

Verify the Deployment, Pods, and Service:

```bash
kubectl get deployments
kubectl get pods
kubectl get services
```

### Access the Application

The Kubernetes Service is a `ClusterIP` Service on port 8080. Forward it to local port 8082:

```bash
kubectl port-forward service/campus-equipment-booking-service 8082:8080
```

Keep this command running in one terminal. In another terminal, run:

```bash
curl.exe --max-time 30 http://localhost:8082/actuator/health
```

The expected health status is `UP`.

### Scale to Two Replicas

```bash
kubectl scale deployment campus-equipment-booking --replicas=2
kubectl get deployments
kubectl get pods
```

The Deployment should show `2/2` ready replicas, with both application Pods running.

## 7. Git Workflow and Evidence

The feature branch used for this change is:

```text
feature/sequential-booking-inventory
```

### Git Evidence

Run the following commands and capture their output:

```bash
git branch --show-current
git log --oneline -5
git status
```

The history should demonstrate the initial commit and the meaningful commits for the capacity-validation fix, sequential-booking tests, and Jenkins pipeline. The final working tree should be clean after all intended changes are committed.

### Maven Evidence

Command:

```bash
mvn clean test
```

Verified result:
- Tests run: 10
- Failures: 0
- Errors: 0
- Skipped: 0
- Build result: `BUILD SUCCESS`

### Docker Evidence

Commands:

```bash
docker images campus-equipment-booking
docker ps
curl.exe --max-time 30 http://localhost:8081/actuator/health
```

Verified result:
- Image: `campus-equipment-booking:set-g`
- Container: `campus-booking-app`
- Container status: Running
- Health status: `UP`

### Kubernetes Evidence

Commands:

```bash
kubectl get deployments
kubectl get pods
kubectl get services
```

Verified result:
- Deployment: `campus-equipment-booking`
- Ready replicas: `2/2`
- Both application Pods: `Running` and ready
- Service: `campus-equipment-booking-service`
- Health endpoint through port forwarding: `UP`

### Jenkins Evidence

Capture the successful Jenkins build's Console Output showing:
- GitHub checkout
- Maven tests
- Maven packaging
- JAR artifact archiving
- Final status: `SUCCESS`

## 8. Conclusion

The booking capacity defect was fixed and verified with automated tests. The application was packaged successfully, built into the required Docker image, and deployed to Minikube. The Kubernetes Deployment was scaled to two ready replicas, and the health endpoint returned `UP`. Jenkins successfully automated the checkout, test, and packaging stages.
