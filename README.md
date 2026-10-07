# Battle IQ

เว็บเกมตอบคำถามวัดความรู้ตามหมวดหมู่ ผู้เล่นสมัครสมาชิก เลือกหมวด แล้วตอบคำถาม 5 ข้อแบบจับเวลา ตอบถูกและตอบเร็วได้คะแนนมาก เล่นชนะต่อเนื่องได้โบนัส win streak คะแนนสะสมใช้ขึ้นเลเวลและจัดอันดับ และสามารถสร้างห้องให้เพื่อนเข้ามาแข่งด้วยคำถามชุดเดียวกันได้

พัฒนาด้วย Spring Boot ตาม Layered Architecture ใช้ SOLID และ Design Patterns กลุ่ม Behavioral (Strategy, Observer, Command) เป็นแกนของระบบคิดคะแนน การอัปเดตโปรไฟล์ และการส่งคำตอบ

---

## สมาชิกกลุ่ม

| ลำดับ | ชื่อ-นามสกุล | รหัสนักศึกษา | Section | Branch | หน้าที่รับผิดชอบ |
|:---:|:---|:---:|:---:|:---|:---|
| 1 | นายรัฐภูมิ เกิดพระจีน | 673380057-5 | 01 | `rattaphum_6733800575_01` | **ระบบเล่นเกม:** Entity ทั้งหมด, QuizSessionService, QuestionFactory, GlobalExceptionHandler, หน้าเล่นเกม, Docker |
| 2 | นายพีรพัฒน์ ป้องกันยา | 673380053-3 | 01 | `pheerapat_6733800533_01` | **ผู้ใช้และโปรไฟล์ (Observer):** สมัคร/login, BCrypt, GameCompletedEvent, ห้องแข่งขัน, ตั้งค่าฐานข้อมูล, แผนทดสอบ, README |
| 3 | นายก้องภพ โชควิริยะ | 673380030-5 | 01 | `kongpob_6733800305_01` | **คำถามและการส่งคำตอบ (Command):** CRUD คำถาม, SubmitAnswerCommand, Swagger, โครงหน้าเว็บ |
| 4 | นายธนกฤต ละครพล | 673380269-0 | 01 | `thanakit_6733802690_01` | **การคิดคะแนน (Strategy):** ScoringStrategy 3 แบบ, ผลการเล่นและประวัติ, Repository, schema.sql |
| 5 | นายณพวิทย์ วงษ์ประเสริฐ | 673380062-2 | 01 | `noppavit_6733802666_01` | **หมวดหมู่และ Leaderboard:** CRUD หมวดหมู่, Leaderboard, Controller ชุดแรก, Deploy |

---

## Tech Stack

| ส่วน | เทคโนโลยี |
|---|---|
| Backend | Java 17, Spring Boot 4.1 (Spring Web MVC, Spring Data JPA, Bean Validation) |
| Database | PostgreSQL บน Neon (cloud), Hibernate, `schema.sql` + `data.sql` |
| Frontend | Thymeleaf + JavaScript (fetch REST API) |
| Security | Spring Security Crypto (BCrypt) |
| API Docs | springdoc-openapi (Swagger UI) |
| Testing | JUnit 5, Mockito, Spring Boot Test, MockMvc |
| Build / Deploy | Maven Wrapper, Docker, docker-compose, Render |

---

## System Architecture

### Layered Architecture

```text
Presentation   controller/        REST Controller (/api/v1/...) และ controller/web (Thymeleaf page)
     ↓
Service        service/           Business logic, @Transactional, Design Patterns
     ↓
Repository     repository/        Spring Data JPA
     ↓
Domain         domain/entity      Entity 8 ตัว, domain/enums
+ DTO          dto/               Request / Response DTO แยกจาก Entity
+ Cross-cut    exception/, config/, event/
```

Controller ไม่เรียก Repository ตรง ทุกอย่างผ่าน Service และ Controller รับ-ส่งเฉพาะ DTO

### Design Patterns

| Pattern | ปัญหาที่แก้ | คลาส |
|---|---|---|
| **Strategy** | สูตรคิดคะแนนมีหลายแบบ (พื้นฐาน, โบนัสเวลา, ตัวคูณ streak) และต้องเพิ่มแบบใหม่ได้โดยไม่แก้ของเดิม | `service/scoring/ScoringStrategy`, `BasicScoringStrategy`, `TimeBonusScoringStrategy`, `StreakScoringStrategy`, `ScoringStrategySelector` |
| **Observer** | เมื่อจบเกมต้องอัปเดตโปรไฟล์ (คะแนนรวม เลเวล streak) โดยไม่ให้ `QuizSessionService` รู้จักโปรไฟล์ | `event/GameCompletedEvent`, `event/GameCompletionEventListener`, `UserProfileService` |
| **Command** | การตอบคำถาม 1 ข้อ ต้องตรวจถูกผิด คิดคะแนน บันทึก และกันตอบซ้ำ เป็น action ที่ห่อเป็นหน่วยเดียว | `service/command/Command`, `SubmitAnswerCommand`, `AnswerService` |
| Factory (เสริม) | สุ่มชุดคำถามตามหมวดหมู่ | `service/QuestionFactory` |

รายละเอียดอยู่ใน [doc/design-patterns.md](doc/design-patterns.md) และการวิเคราะห์ SOLID อยู่ใน [doc/solid-analysis.md](doc/solid-analysis.md)

---

## Database Design (ER Diagram)

```mermaid
erDiagram
    users ||--|| user_profiles : "has"
    users ||--o{ quiz_sessions : "plays"
    users ||--o{ rooms : "hosts"
    users ||--o{ room_players : "joins"
    rooms ||--o{ room_players : "contains"
    rooms }o--|| categories : "uses"
    categories ||--o{ questions : "has"
    categories ||--o{ quiz_sessions : "chosen in"
    quiz_sessions ||--o{ quiz_details : "has"
    questions ||--o{ quiz_details : "asked in"
    room_players |o--o| quiz_sessions : "plays as"

    users {
        bigint id PK
        varchar username UK
        varchar email UK
        varchar password
        timestamp created_at
    }
    user_profiles {
        bigint user_id PK_FK
        varchar full_name
        varchar avatar_url
        int total_score
        int level
        int current_streak
    }
    categories {
        bigint id PK
        varchar name UK
        varchar description
    }
    questions {
        bigint id PK
        bigint category_id FK
        text question_text
        varchar optiona
        varchar optionb
        varchar optionc
        varchar optiond
        varchar correct_answer
        int time_limit_seconds
    }
    quiz_sessions {
        bigint id PK
        bigint user_id FK
        bigint category_id FK
        int total_questions
        int total_score
        varchar status
        timestamp created_at
        timestamp completed_at
    }
    quiz_details {
        bigint id PK
        bigint session_id FK
        bigint question_id FK
        varchar user_ans
        boolean is_correct
        int time_taken_seconds
        int score_earned
    }
    rooms {
        bigint id PK
        varchar code UK
        bigint host_id FK
        bigint category_id FK
        varchar status
        timestamp created_at
        timestamp started_at
    }
    room_players {
        bigint id PK
        bigint room_id FK
        bigint user_id FK
        bigint session_id FK
        timestamp joined_at
    }
```

- **One-to-One:** `users` – `user_profiles` (ใช้ `@MapsId` แชร์ primary key)
- **One-to-Many:** `categories` → `questions`, `users` → `quiz_sessions`, `quiz_sessions` → `quiz_details`, `rooms` → `room_players`
- **Many-to-Many:** `users` ↔ `rooms` ผ่านตารางกลาง `room_players` ที่มีข้อมูลเพิ่ม (session ของผู้เล่นในห้องนั้น)
- Index และ constraint อยู่ใน [schema.sql](code/battle-iq/src/main/resources/schema.sql) ข้อมูลตั้งต้นอยู่ใน [data.sql](code/battle-iq/src/main/resources/data.sql) (3 หมวด 15 คำถาม)
- Data Dictionary และ diagram อื่นอยู่ใน [doc/diagrams/](doc/diagrams/)

---

## Installation & Setup

ต้องมี Java 17 ขึ้นไป ไม่ต้องติดตั้ง Maven (ใช้ Maven Wrapper)

```bash
git clone https://github.com/rattaphumk-tech/Battle-IQ.git
cd Battle-IQ/code/battle-iq
```

สร้างไฟล์ `.env` ในโฟลเดอร์ `code/battle-iq` จาก `.env.example`

```properties
DB_URL=jdbc:postgresql://<host>/<database>?sslmode=require
DB_USERNAME=<username>
DB_PASSWORD=<password>
```

ใช้ PostgreSQL ที่ไหนก็ได้ (ทีมใช้ Neon) ตารางและข้อมูลตั้งต้นจะถูกสร้างอัตโนมัติตอนสตาร์ตครั้งแรก

---

## How to Run

```bash
cd code/battle-iq
./mvnw spring-boot:run          # Windows: .\mvnw.cmd spring-boot:run
```

เปิด `http://localhost:8080` (เปลี่ยนพอร์ตด้วยตัวแปร `PORT`)

รันด้วย Docker (จากรากของ repo)

```bash
docker compose up --build
```

### วิธีเล่น

1. สมัครสมาชิกที่ `/register` แล้วเลือกหมวดหมู่ที่ `/categories`
2. ตอบคำถาม 5 ข้อ แต่ละข้อมีเวลาจำกัด ตอบถูกได้ 10 คะแนน + โบนัสตามเวลาที่เหลือสูงสุด 10
3. เล่นชนะต่อเนื่อง (streak ≥ 2) ได้ตัวคูณเพิ่ม 10% ต่อ streak สูงสุด 50%
4. คะแนนสะสมขึ้นเลเวลทุก 100 คะแนน ดูอันดับที่ `/leaderboard`
5. แข่งกับเพื่อน: สร้างห้องที่ `/rooms` ส่งรหัส 6 ตัวให้เพื่อน เจ้าของห้องกดเริ่ม ทุกคนได้คำถามชุดเดียวกันและเห็นอันดับในห้องแบบสด

---

## API Documentation

Swagger UI: `http://localhost:8080/swagger-ui.html` (OpenAPI JSON ที่ `/v3/api-docs`)

| Method | Endpoint | หน้าที่ |
|---|---|---|
| POST | `/api/v1/users/register` | สมัครสมาชิก (สร้างโปรไฟล์อัตโนมัติ) |
| POST | `/api/v1/users/login` | เข้าสู่ระบบ |
| GET | `/api/v1/users/{id}` | ข้อมูลผู้ใช้ |
| GET / PUT | `/api/v1/profiles/user/{userId}` | ดู / แก้โปรไฟล์ |
| GET / POST | `/api/v1/categories` | รายการ / เพิ่มหมวดหมู่ |
| GET / PUT / DELETE | `/api/v1/categories/{id}` | ดู / แก้ / ลบหมวดหมู่ |
| GET | `/api/v1/questions?page=&size=&sort=` | คำถามแบบแบ่งหน้าและเรียงลำดับ |
| GET | `/api/v1/questions/category/{categoryId}` | คำถามตามหมวด (ไม่ส่งเฉลย) |
| POST | `/api/v1/questions` | เพิ่มคำถาม |
| PUT / DELETE | `/api/v1/questions/{id}` | แก้ / ลบคำถาม |
| POST | `/api/v1/quiz-sessions/start` | เริ่มรอบการเล่น (สุ่ม 5 ข้อ) |
| GET | `/api/v1/quiz-sessions/{id}/questions` | คำถามของรอบ |
| POST | `/api/v1/quiz-sessions/{id}/answers` | ส่งคำตอบ 1 ข้อ |
| POST | `/api/v1/quiz-sessions/{id}/complete` | จบรอบ รวมคะแนนฝั่ง server |
| GET | `/api/v1/quiz-sessions/{id}/result` | สรุปผลและเฉลยทุกข้อ |
| GET | `/api/v1/users/{userId}/quiz-sessions` | ประวัติการเล่น |
| GET | `/api/v1/leaderboard?page=&size=` | อันดับผู้เล่น |
| POST | `/api/v1/rooms` | สร้างห้องแข่ง |
| GET | `/api/v1/rooms/{code}` | สถานะห้องและผู้เล่น |
| POST | `/api/v1/rooms/{code}/join` | เข้าร่วมห้อง |
| POST | `/api/v1/rooms/{code}/start` | เจ้าของห้องเริ่มแข่ง |

Status code: 200 สำเร็จ, 201 สร้างแล้ว, 204 ลบแล้ว, 400 ข้อมูลไม่ถูกต้อง, 401 รหัสผ่านผิด, 404 ไม่พบ, 409 ขัดแย้ง (ชื่อซ้ำ, จบเกมซ้ำ, ห้องเริ่มแล้ว), 415 Content-Type ผิด, 500 ข้อผิดพลาดภายใน ทุก error ตอบในรูปแบบเดียวกัน

```json
{ "status": 409, "message": "Username already exists: somchai", "timestamp": "2026-10-07T18:00:00" }
```

---

## How to Run Tests

```bash
cd code/battle-iq
./mvnw test
```

ต้องมีไฟล์ `.env` เพราะเทสระดับ integration ต่อฐานข้อมูลจริง (rollback ทุกเทส) ผลล่าสุด 54 กรณี ผ่านทั้งหมด

แผนการทดสอบ รายการ test case และรายงาน HTML อยู่ใน [test/](test/)

```bash
./mvnw surefire-report:report-only    # สร้าง target/reports/surefire.html
```

---

## Deployment URL

ยังไม่ได้ deploy (รอ Render) — ใส่ URL ที่นี่เมื่อ deploy เสร็จ

---

## Project Structure

```text
Battle-IQ/
├── code/battle-iq/                 # Spring Boot project
│   ├── src/main/java/com/battleiq/
│   │   ├── config/                 # PasswordConfig (BCrypt), OpenApiConfig
│   │   ├── controller/             # REST Controller
│   │   │   └── web/                # Thymeleaf page controller
│   │   ├── service/
│   │   │   ├── scoring/            # Strategy pattern
│   │   │   └── command/            # Command pattern
│   │   ├── event/                  # Observer pattern (ApplicationEvent)
│   │   ├── repository/
│   │   ├── domain/entity/
│   │   ├── domain/enums/
│   │   ├── dto/
│   │   └── exception/              # Custom exception + GlobalExceptionHandler
│   ├── src/main/resources/
│   │   ├── templates/              # หน้าเว็บ Thymeleaf
│   │   ├── static/                 # css, js
│   │   ├── schema.sql, data.sql
│   │   └── application.properties
│   └── src/test/java/              # Unit + Integration test
├── test/                           # แผนการทดสอบ, test case, รายงาน
├── doc/
│   ├── diagrams/
│   ├── solid-analysis.md
│   └── design-patterns.md
├── img/
├── Dockerfile
├── docker-compose.yml
└── README.md
```
