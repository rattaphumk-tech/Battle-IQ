# 🎮 Battle IQ - Ultimate Quiz & Knowledge Battle Platform 

> **Battle IQ** คือแอปพลิเคชันเกมแข่งขันวัดความรู้ตามหมวดหมู่ที่เลือก (Trivia & Knowledge Battle Platform) ที่รวมเอาความสนุกของการตอบคำถามจับเวลา การเพิ่มระดับความยากแบบ Dynamic, ระบบ Gamification (Level, Win Streak, Badges) และความรู้รอบตัวหลังตอบคำถาม (Fun Facts & Educational Insights) เข้าไว้ด้วยกัน

พัฒนาขึ้นเพื่อเป็น **Final Project** รายวิชา *Principles of Software Design and Development* โดยมุ่งเน้นการประยุกต์ใช้ **Software Architecture, Design Patterns (GoF), GRASP, High-Level SOLID Principles** และ **Spring Boot Transactional Operations** อย่างครบถ้วนและเป็นรูปธรรม

---

## 🎯 Key Features & Highlights

* **🧠 Multi-Category Trivia:** เลือกเล่นตามหมวดหมู่ความรู้ที่สนใจ (เช่น Science, History, Tech, Pop Culture, General Knowledge)
* **⏱️ Dynamic Difficulty & Timer:** ระบบปรับความยากของคำถามตามประสิทธิภาพของผู้เล่น ยิ่ง Win Streak สูง คำถามจะยิ่งท้าทายขึ้นพร้อมเวลาตอบที่บีบหัวใจ
* **💡 Instant Learning (Bite-sized Knowledge):** ทุกๆ คำตอบ (ทั้งถูกและผิด) จะแสดงเกร็ดความรู้สั้นๆ (Educational Insights / Fun Facts) ให้ผู้เล่นได้เรียนรู้เสมอ
* **🔥 Gamification System:**
  * ระบบ **Level & EXP System** สะสมประสบการณ์จากการเล่น
  * ระบบ **Win Streak Multiplier** โบนัสคะแนนคูณตามจำนวนข้อที่ตอบถูกติดต่อกัน
  * **Badges & Achievements** ปลดล็อกเหรียญเกียรติยศเมื่อทำตามเงื่อนไขสำเร็จ
* **🏆 Global & Friend Leaderboards:** ตารางคะแนนจัดอันดับความรอบรู้
* **🛡️ Sound Architecture & Design Patterns:** โครงสร้างโค้ดถูกออกแบบตามหลักสถาปัตยกรรมซอฟต์แวร์ที่ดี รองรับการขยายระบบในอนาคต

---

## 🏗️ Software Architecture & Design Patterns

โปรเจกต์นี้ได้รับการออกแบบโดยใช้ **Design Patterns** และหลักการทาง **Software Engineering** เพื่อแก้ปัญหาในแต่ละส่วนอย่างสมเหตุสมผล:

### 🌟 Applied Design Patterns (GoF)

| Design Pattern | Category | Usage in Battle IQ |
| :--- | :--- | :--- |
| **Strategy Pattern** | Behavioral | ใช้คำนวณคะแนนและ EXP (`ScoringStrategy`) โดยปรับตาม Win Streak, เวลาที่เหลือ และระดับความยากของคำถาม |
| **Factory / Abstract Factory** | Creational | ใช้สร้างประเภทคำถามต่างรูปแบบ (`QuestionFactory` -> Multiple Choice, True/False, Image-based Question) |
| **Observer Pattern** | Behavioral | ระบบ Event Listener เมื่อจบเกม (`GameCompletionObserver`) เพื่ออัปเดต Level/EXP, บันทึก Win Streak, ปลดล็อก Badges และส่งสถิติไป Leaderboard |
| **Decorator Pattern** | Structural | เพิ่มเอฟเฟกต์หรือพลังพิเศษของ Power-ups/Boosters (เช่น `DoubleExpDecorator`, `TimeExtensionDecorator`, `FiftyFiftyDecorator`) |
| **Command Pattern** | Behavioral | แคปซูล Action การตอบคำถาม (`SubmitAnswerCommand`) เพื่อรองรับการตรวจทาน ประมวลผลเวลา และบันทึกประวัติการตอบ (Undo/Replay Stream) |
| **Proxy Pattern** | Structural | ทำ `QuestionCacheProxy` เพื่อลด Load จาก Database ในคำถามยอดนิยม และใช้เป็น Security Proxy ในการตรวจสอบสิทธิ์การเล่น |
| **Adapter Pattern** | Structural | เชื่อมต่อกับ External Fact/Knowledge API หรือระบบ Analytics ภายนอก |

### 💎 SOLID & GRASP Principles Applied
* **Single Responsibility Principle (SRP):** แยก Service ชัดเจน เช่น `QuizSessionService`, `ScoringEngine`, `AchievementManager`, `UserProgressService`
* **Open/Closed Principle (OCP):** เพิ่มรูปแบบคำถามหรือคำนวณคะแนนแบบใหม่ได้โดยการสร้าง Strategy / Factory ใหม่โดยไม่ต้องแก้ไขโค้ดเดิม
* **Dependency Inversion Principle (DIP):** ทุก Service เรียกใช้ผ่าน Abstraction / Interface
* **High Cohesion & Low Coupling:** โมดูลเกม การคำนวณ และการเก็บข้อมูลแยกกันอย่างเด็ดขาดตามหลัก GRASP

---

## 🛠️ Tech Stack & Tools

* **Backend Framework:** Java / Spring Boot 3.x
* **Build Tool:** Apache Maven
* **Database:** MySQL / PostgreSQL
* **ORM / Persistence:** Spring Data JPA / Hibernate (พร้อมการจัดการ `@Transactional` สำหรับ Score & Inventory Consistency)
* **Version Control:** Git & GitHub
* **UML & Design Tools:** Draw.io / PlantUML (Use Case, Class Diagram, Sequence Diagram, Activity Diagram)

---

## 🗂️ Database & Transaction Safety

* **Transaction Management:** ใช้ `@Transactional` ในขั้นตอนสำคัญ เช่น การบันทึกผลการเล่น (Quiz Completion), การตัดหัก Item Boosters, และการอัปเดต User Level/Streak เพื่อป้องกันปัญหา Race Condition และ Data Inconsistency
* **Database Relationships:** ออกแบบ ER Diagram ตามหลัก 3NF รองรับความสัมพันธ์แบบ `One-to-Many` (User -> QuizSessions), `Many-to-Many` (User -> Badges, Question -> Category)

---

## 👥 Team Members & Responsibilities (Group of 5)

| Name | Role | Core Responsibilities |
| :--- | :--- | :--- |
| **นายรัฐภูมิ เกิดพระจีน ** | **Software Architect & Lead UML** | Requirement Analysis, UML Modeling (Use Case, Class Diagram, Sequence Diagram), Database Schema Design & ERD |
| **นายธนกฤต ละครพล** | **Core Quiz & Dynamic Engine** | Question Factory, Dynamic Difficulty Logic, Game Session Management, Command Pattern for Submissions |
| **นายณพวิทย์ วงษ์ประเสริฐ** | **Scoring & Gamification Logic** | Scoring Strategy, Level/EXP Multiplier, Win Streak Tracker, Decorator Pattern for Boosters |
| **นายพีรพัฒน์ ป้องกันยา** | **Event Observer & Integration** | Observer Pattern for Achievements/Leaderboards, Fact Insights Provider, External Adapter Services |
| **นายก้องภพ โชควิริยะ** | **Spring Boot Core & Security/Proxy** | Spring Boot Controllers, Security & Caching Proxy, `@Transactional` Management, Database Repositories |

---

## 🚀 Getting Started

### Prerequisites
* Java JDK 17 or higher
* Apache Maven 3.8+
* MySQL 8.0+

### Installation & Run

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/battle-iq.git
   cd battle-iq
   ```

2. **Configure Database:**
   แก้ไขไฟล์ `src/main/resources/application.yml` ตั้งค่า Database Connection:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/battle_iq_db
       username: your_username
       password: your_password
     jpa:
       hibernate:
         ddl-auto: update
   ```

3. **Build & Run Application:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

---

## 📄 License
This project is developed as part of the *Principles of Software Design and Development* course. All rights reserved.
