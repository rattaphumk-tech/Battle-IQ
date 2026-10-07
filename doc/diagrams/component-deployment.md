# Component Diagram

```mermaid
flowchart TB
    subgraph Browser["เบราว์เซอร์ผู้เล่น"]
        UI[Thymeleaf pages<br/>templates/*.html]
        JS[app.js + script ในแต่ละหน้า<br/>fetch REST API, localStorage]
        UI --> JS
    end

    subgraph App["Spring Boot Application (battle-iq.jar)"]
        direction TB
        WEB[controller/web<br/>PageController ส่ง template]
        REST[controller<br/>REST Controller /api/v1]
        EXC[exception<br/>GlobalExceptionHandler]
        SVC[service<br/>User, Profile, Category, Question,<br/>QuizSession, Answer, QuizResult,<br/>Leaderboard, Room]
        PAT[service/scoring + service/command + event<br/>Strategy · Command · Observer]
        REPO[repository<br/>Spring Data JPA]
        DOM[domain/entity + enums]
        CFG[config<br/>PasswordConfig, OpenApiConfig]
        SWG[springdoc<br/>/swagger-ui.html]

        REST --> SVC
        SVC --> PAT
        SVC --> REPO
        REPO --> DOM
        REST -.-> EXC
        SWG -.-> REST
        SVC -.-> CFG
    end

    subgraph DB["Neon (PostgreSQL)"]
        T[(8 ตาราง<br/>schema.sql + data.sql)]
    end

    JS -- "HTTP JSON" --> REST
    UI -- "HTTP GET หน้า" --> WEB
    REPO -- "JDBC / Hibernate" --> T
```

| Component | หน้าที่ | ขึ้นกับ |
|---|---|---|
| Thymeleaf pages | หน้าเว็บ 13 หน้า โครงร่างจาก `fragments/layout.html` | app.js |
| app.js | เรียก API, เก็บสถานะ login, เมนูผู้ใช้ | REST API |
| REST Controller | รับ-ส่ง DTO ตรวจ `@Valid` | Service |
| GlobalExceptionHandler | แปลง exception เป็น `ErrorResponseDTO` รูปแบบเดียว | — |
| Service | business logic และ transaction | Repository, Pattern components |
| Strategy / Command / Observer | คิดคะแนน, ส่งคำตอบ, อัปเดตโปรไฟล์หลังจบเกม | Service, Domain |
| Repository | เข้าถึงฐานข้อมูล | Domain, PostgreSQL |
| Config | BCrypt `PasswordEncoder`, ข้อมูล OpenAPI | — |

---

# Deployment Diagram

```mermaid
flowchart LR
    subgraph Client["เครื่องผู้เล่น"]
        BR[Web Browser<br/>Chrome / Edge / มือถือ]
    end

    subgraph Render["Render.com (Web Service, Docker)"]
        direction TB
        IMG[Docker image<br/>eclipse-temurin:17-jre]
        JAR[battle-iq.jar<br/>Spring Boot + Tomcat<br/>PORT จาก env]
        IMG --> JAR
    end

    subgraph Neon["Neon (Serverless PostgreSQL, ap-southeast-1)"]
        PG[(neondb<br/>SSL required)]
    end

    subgraph GitHub["GitHub"]
        REPO[rattaphumk-tech/Battle-IQ<br/>branch main]
    end

    BR -- "HTTPS :443" --> JAR
    JAR -- "JDBC SSL :5432<br/>DB_URL / DB_USERNAME / DB_PASSWORD" --> PG
    REPO -- "auto deploy เมื่อ push" --> IMG
```

| Node | รายละเอียด |
|---|---|
| เครื่องผู้เล่น | เบราว์เซอร์ใดก็ได้ ไม่ต้องติดตั้ง สถานะ login เก็บใน localStorage |
| Render Web Service | build จาก `Dockerfile` ที่ราก repo (multi-stage: build ด้วย JDK 17 แล้วรันด้วย JRE 17) อ่านพอร์ตจากตัวแปร `PORT` ตัวแปรฐานข้อมูลตั้งในหน้า Environment ของ Render แผนฟรีจะหลับเมื่อไม่มีการใช้งาน |
| Neon | PostgreSQL แบบ serverless ทีมใช้ฐานเดียวกันทั้ง dev และ production ตารางถูกสร้างอัตโนมัติจาก `schema.sql` / Hibernate |
| GitHub | เก็บโค้ด Render ดึงจาก `main` เมื่อมีการ push |

การรันในเครื่องนักพัฒนาใช้โครงเดียวกัน แต่แทน Render ด้วย `./mvnw spring-boot:run` หรือ `docker compose up` และอ่านค่าฐานข้อมูลจากไฟล์ `.env`
