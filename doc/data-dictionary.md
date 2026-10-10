# Data Dictionary

ฐานข้อมูล PostgreSQL 8 ตาราง ชื่อคอลัมน์ตามที่ Hibernate สร้างจริง (ตัวเลือก A–D เป็น `optiona`–`optiond` ไม่มีขีดล่าง)

## users — บัญชีผู้ใช้

| คอลัมน์ | ชนิด | Null | Key | คำอธิบาย |
|---|---|---|---|---|
| id | bigint identity | N | PK | รหัสผู้ใช้ |
| username | varchar(50) | N | UK | ชื่อผู้ใช้ a-z 0-9 _ ความยาว 3–50 ใช้ login |
| email | varchar(100) | N | UK | อีเมล |
| password | varchar(255) | N | | รหัสผ่านที่เข้ารหัสด้วย BCrypt |
| created_at | timestamp | Y | | เวลาสมัคร ตั้งอัตโนมัติ (`@PrePersist`) |

## user_profiles — ข้อมูลเกมของผู้ใช้

| คอลัมน์ | ชนิด | Null | Key | คำอธิบาย |
|---|---|---|---|---|
| user_id | bigint | N | PK, FK → users.id | ใช้ PK เดียวกับ users (`@MapsId`) |
| full_name | varchar(255) | Y | | ชื่อที่แสดง ≤ 100 ตัวอักษร (ตรวจที่ DTO) |
| avatar_url | varchar(255) | Y | | ลิงก์รูปโปรไฟล์ |
| total_score | integer | Y | | คะแนนสะสมทุกรอบ เริ่ม 0 |
| level | integer | Y | | เลเวล = total_score / 100 + 1 เริ่ม 1 |
| current_streak | integer | Y | | จำนวนรอบติดกันที่ได้คะแนน > 0 เริ่ม 0 |

## categories — หมวดหมู่คำถาม

| คอลัมน์ | ชนิด | Null | Key | คำอธิบาย |
|---|---|---|---|---|
| id | bigint identity | N | PK | รหัสหมวด |
| name | varchar(100) | N | UK | ชื่อหมวด 2–50 ตัวอักษร |
| description | varchar(255) | Y | | คำอธิบาย |

## questions — คำถาม

| คอลัมน์ | ชนิด | Null | Key | คำอธิบาย |
|---|---|---|---|---|
| id | bigint identity | N | PK | รหัสคำถาม |
| category_id | bigint | N | FK → categories.id | หมวดของคำถาม |
| question_text | text | N | | โจทย์ ≤ 1000 ตัวอักษร |
| optiona | varchar(255) | N | | ตัวเลือก A |
| optionb | varchar(255) | N | | ตัวเลือก B |
| optionc | varchar(255) | N | | ตัวเลือก C |
| optiond | varchar(255) | N | | ตัวเลือก D |
| correct_answer | varchar(1) | N | | เฉลย A / B / C / D |
| time_limit_seconds | integer | Y | | เวลาต่อข้อ 5–120 วินาที ค่าเริ่มต้น 15 |

## quiz_sessions — รอบการเล่น

| คอลัมน์ | ชนิด | Null | Key | คำอธิบาย |
|---|---|---|---|---|
| id | bigint identity | N | PK | รหัสรอบ |
| user_id | bigint | N | FK → users.id | ผู้เล่น |
| category_id | bigint | N | FK → categories.id | หมวดที่เล่น |
| total_questions | integer | Y | | จำนวนข้อ (สูงสุด 5) |
| total_score | integer | Y | | คะแนนรวม คำนวณจาก quiz_details ตอนจบ |
| status | varchar(255) | N | | IN_PROGRESS / COMPLETED / ABANDONED |
| created_at | timestamp | Y | | เวลาเริ่ม |
| completed_at | timestamp | Y | | เวลาจบ ว่างถ้ายังไม่จบ |

## quiz_details — คำตอบรายข้อ

| คอลัมน์ | ชนิด | Null | Key | คำอธิบาย |
|---|---|---|---|---|
| id | bigint identity | N | PK | |
| session_id | bigint | N | FK → quiz_sessions.id | รอบที่สังกัด |
| question_id | bigint | N | FK → questions.id | คำถาม |
| user_ans | varchar(255) | Y | | คำตอบ A–D ว่างถ้ายังไม่ตอบหรือหมดเวลา |
| is_correct | boolean | Y | | ตอบถูกหรือไม่ เริ่ม false |
| time_taken_seconds | integer | Y | | เวลาที่ใช้ตอบ |
| score_earned | integer | Y | | คะแนนข้อนี้จาก ScoringStrategy เริ่ม 0 |

## rooms — ห้องแข่งขัน

| คอลัมน์ | ชนิด | Null | Key | คำอธิบาย |
|---|---|---|---|---|
| id | bigint identity | N | PK | |
| code | varchar(6) | N | UK | รหัสเข้าห้อง 6 ตัว ไม่ใช้ 0 O 1 I |
| host_id | bigint | N | FK → users.id | เจ้าของห้อง |
| category_id | bigint | N | FK → categories.id | หมวดที่แข่ง |
| status | varchar(20) | N | | WAITING / IN_PROGRESS / FINISHED |
| created_at | timestamp | Y | | เวลาสร้างห้อง |
| started_at | timestamp | Y | | เวลาเริ่มแข่ง |

## room_players — ผู้เล่นในห้อง (ตารางกลาง users ↔ rooms)

| คอลัมน์ | ชนิด | Null | Key | คำอธิบาย |
|---|---|---|---|---|
| id | bigint identity | N | PK | |
| room_id | bigint | N | FK → rooms.id | ห้อง |
| user_id | bigint | N | FK → users.id | ผู้เล่น |
| session_id | bigint | Y | FK → quiz_sessions.id | รอบของผู้เล่นคนนี้ในห้อง ว่างจนกว่าห้องจะเริ่ม |
| joined_at | timestamp | Y | | เวลาเข้าห้อง |
| (room_id, user_id) | | | UK | 1 คนอยู่ในห้องเดียวกันได้ครั้งเดียว |
