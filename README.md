# 🎮 Battle IQ - Ultimate Trivia & Gamified Learning Platform

Battle IQ เป็นเว็บแอปพลิเคชันแข่งขันวัดความรู้ตามหมวดหมู่แบบเรียลไทม์ ที่ผสานระบบ Dynamic Difficulty, Instant Fun-Facts และ Gamification (Level, Win Streak, Badges) เข้าด้วยกัน มุ่งเน้นการออกแบบด้วยสถาปัตยกรรม Layered Architecture, SOLID Principles, Design Patterns และ Spring Boot Transactional Management อย่างเป็นระบบ

---

## 👥 สมาชิกกลุ่ม

| ลำดับ | ชื่อ-นามสกุล | รหัสนักศึกษา | Section | Branch | หน้าที่รับผิดชอบ |
| :---: | :--- | :---: | :---: | :--- | :--- |
| 1 | นายกิตติศักดิ์ ใจดี | 66xxxxxxxx-1 | 01 | `kittisak_66xxxxxxxx1_01` | **Software Architect & Lead UML:** Requirements Analysis, System Architecture, Domain Model, ERD Design |
| 2 | นายธนกฤต มั่นคง | 66xxxxxxxx-2 | 01 | `thanakrit_66xxxxxxxx2_01` | **Core Quiz Engine:** Question Factory, Dynamic Difficulty Logic, Session Management |
| 3 | นางสาวปรารถนา ดีจริง | 66xxxxxxxx-3 | 01 | `prarthana_66xxxxxxxx3_01` | **Scoring & Gamification:** Strategy Pattern (Scoring Engine), Win Streak Multiplier, Leveling System |
| 4 | นายพีรพัฒน์ สุขสันต์ | 66xxxxxxxx-4 | 01 | `peerapat_66xxxxxxxx4_01` | **Observer & Integration Services:** Event Listener for Badges/Leaderboard, External Knowledge API Adapter |
| 5 | นางสาววรินทร สายทอง | 66xxxxxxxx-5 | 01 | `warinthorn_66xxxxxxxx5_01` | **Spring Boot Core & Security/Transactions:** RestController, JPA Repositories, `@Transactional` Management, Proxy Interceptor |

---

## 🛠️ Tech Stack

* **Backend Framework:** Java 17 / Spring Boot 3.x
* **Build Tool:** Apache Maven
* **Database:** PostgreSQL / MySQL (Spring Data JPA + Hibernate)
* **Frontend:** Thymeleaf / React (REST API Integration)
* **API Documentation:** OpenAPI 3.0 / Swagger UI
* **Testing:** JUnit 5 + Mockito
* **Deployment & DevOps:** Docker, Docker Compose, Render / Railway

---

## 🏗️ System Architecture & SOLID Principles

### 1. Strict Layered Architecture
โปรเจกต์นี้แยก Layer ชัดเจนและห้ามเรียกข้าม Layer:
`Presentation (Controller) -> Service Layer -> Repository Layer -> Entity / Domain` (พร้อมการแปลงข้อมูลผ่าน DTO & Mappers)

### 2. Applied Design Patterns (GoF)
เลือกเน้น **Behavioral Patterns** (3 แบบหลัก) เพื่อแก้ปัญหาทางตรงตามข้อกำหนดรายวิชา:

* **Strategy Pattern (`ScoringStrategy`):** คำนวณคะแนนยืดหยุ่นตาม Win Streak, เวลาที่เหลือ และความยากคำถาม
* **Observer Pattern (`GameCompletionEventListener`):** ส่ง Event อัปเดต EXP, Badge และ Leaderboard อัตโนมัติเมื่อผู้เล่นเล่นจบเกม
* **Command Pattern (`SubmitAnswerCommand`):** แคปซูล Action การตอบคำถามเพื่อรองรับ Validation และ Audit Log

*(ส่วนประกอบเพิ่มเติม: **Factory Pattern** สำหรับสร้าง Object คำถามประเภทต่างๆ และ **Proxy Pattern** สำหรับ Caching คำถามยอดนิยม)*

---

## 🗂️ Database Design (ER Diagram)

ระบบประกอบด้วย **6 ตารางหลัก** (ตรงตามเกณฑ์ข้อกำหนด):
1. `users` - ข้อมูลผู้ใช้งานหลัก
2. `user_profiles` - ข้อมูลโปรไฟล์เพิ่มเติม (**One-to-One** กับ `users`)
3. `categories` - หมวดหมู่คำถาม (**One-to-Many** กับ `questions`)
4. `questions` - คลังคำถามและเกร็ดความรู้ (Fun Facts)
5. `quiz_sessions` - ประวัติการเข้าเล่นแต่ละรอบ (**One-to-Many** จาก `users`)
6. `quiz_details` - รายละเอียดการตอบแต่ละข้อ (**One-to-Many** จาก `quiz_sessions`)

*(ดูรายละเอียดเพิ่มเติมได้ที่ `doc/diagrams/erd_diagram.png` และ Data Dictionary ในโฟลเดอร์ `doc/`)*

---

## 📂 Project Structure

```text
Battle-IQ/
├── code/                   # Source code (Spring Boot + Frontend)
│   ├── src/main/java/com/battleiq/
│   │   ├── config/
│   │   ├── controller/api/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── domain/entity/
│   │   ├── dto/
│   │   └── exception/
├── test/                   # JUnit 5 & Mockito Unit Tests
├── doc/                    # Documentation ทั้งหมด
│   ├── diagrams/           # Use Case, Class, Sequence, ERD, Activity Diagrams
│   ├── slide/              # Presentation Slide (.pptx / .pdf)
│   ├── solid-analysis.md   # วิเคราะห์ SOLID Principles รายไฟล์
│   └── design-patterns.md  # ตารางและคำอธิบาย Design Patterns
├── img/                    # รูปภาพประกอบและ Screenshots
├── Dockerfile
├── docker-compose.yml
└── README.md
