# Distributed Task Scheduling & Execution Engine

A robust, production-ready distributed job queue system built with **Java** and **Spring Boot**. This project implements the **Database-as-a-Queue** pattern, designed to handle asynchronous tasks (Emails, PDF Generation, File Uploads) across multiple worker nodes with high consistency and fault tolerance.



## 🚀 Key Features

* **Distributed Locking:** Implements the `SELECT FOR UPDATE SKIP LOCKED` mechanism (MySQL 8.0+) to allow multiple worker instances to poll the database concurrently without race conditions or double-processing.
* **Exponential Backoff & Retries:** Automatically calculates retry intervals using an exponential strategy ($10 \times 2^{attempt}$ seconds), preventing "thundering herd" issues during downstream service failures.
* **State Machine Architecture:** Manages complex job lifecycles through 7 distinct statuses: `CREATED`, `SCHEDULED`, `RUNNING`, `SUCCESS`, `FAILED`, and `DLQ`.
* **Dead Letter Queue (DLQ):** Automatically isolates jobs that have exhausted their `max_attempts` for manual audit and debugging.
* **Heartbeat/Lock Safety:** Uses a `locked_until` timestamp to ensure that if a worker crashes mid-task, the job becomes available for re-processing after the lock expires.

## 🛠 Tech Stack

* **Language:** Java 17
* **Framework:** Spring Boot 3.x
* **Persistence:** Spring Data JPA / Hibernate
* **Database:** MySQL 8.0+ (Required for Native SQL locking features)
* **Build Tool:** Maven

## 🏗 System Design & Workflow

The system follows a decoupled architecture to ensure high availability and scalability:

1.  **Job Submission:** Clients submit tasks via a REST API. Jobs are persisted in the `CREATED` state with specific `run_at` timestamps and `priority` levels.
2.  **Scheduling Phase:** A background service periodically identifies due jobs and transitions them to the `SCHEDULED` state.
3.  **Worker Execution:** * Workers perform a "Pick & Lock" operation using an atomic transaction.
    * The `SKIP LOCKED` clause ensures each worker gets a unique set of jobs without waiting for other workers to release locks.
    * Workers update the job status to `RUNNING` and set a 2-minute execution lock (`locked_until`).



## 🚦 Getting Started

### Prerequisites
* JDK 17
* MySQL 8.0+

### Installation & Setup

1.  **Clone the Repository:**
    ```bash
    git clone [https://github.com/nikhilkumar12161/Distributed-System.git](https://github.com/nikhilkumar12161/Distributed-System.git)
    cd Distributed-System
    ```

2.  **Database Configuration:**
    Create a database named `distributedSystem` and update your `src/main/resources/application.properties`:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/distributedSystem
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    ```

3.  **Run the Application:**
    ```bash
    mvn spring-boot:run
    ```

## 🧪 API Documentation

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/jobs/add-job` | Enqueue a new task (Email, PDF, etc.) |
| `GET` | `/jobs` | Retrieve all jobs (Supports filtering by status/type) |
| `GET` | `/jobs/update` | Bulk update job statuses for maintenance |

## 📉 Error Handling & Reliability

* **Transactional Integrity:** Every state transition (e.g., `SCHEDULED` → `RUNNING`) is wrapped in a `@Transactional` block to prevent data corruption.
* **Failure Logging:** The system catches exceptions during execution, logs the error message, and increments the `attempt_count` to trigger the backoff logic.
* **Retry Logic:** If a job fails, its next `run_at` time is pushed forward based on the number of attempts already made.

---

### **Engineering Trade-offs: DB-as-a-Queue vs. Message Brokers**
While systems like RabbitMQ or Kafka are standard for high-volume messaging, this architecture was chosen because:
1.  **Transactional Enqueuing:** The job is only committed to the queue if the surrounding business logic transaction succeeds (Atomicity).
2.  **Observability:** Using standard SQL, developers can easily monitor, query, and manually re-run failed jobs without specialized CLI tools.
3.  **Simplicity:** Reduces infrastructure overhead by eliminating the need to manage a separate broker cluster for mid-range workloads.
