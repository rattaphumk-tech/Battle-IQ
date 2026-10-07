# การทดสอบ Battle IQ

เอกสารนี้สรุปแผนการทดสอบ รายการ test case และวิธีรันเทสของโปรเจกต์ ตามแนวทาง SQA: กำหนดวัตถุประสงค์และขอบเขตก่อน แบ่งระดับการทดสอบ ออกแบบกรณีทดสอบด้วยเทคนิคที่อ้างอิงได้ ผูกทุกกรณีกับข้อกำหนด และเก็บหลักฐานผลการรัน

โค้ดเทสอยู่ที่ `code/battle-iq/src/test/java` ตามโครงสร้างมาตรฐานของ Maven โฟลเดอร์นี้เก็บแผน รายการกรณีทดสอบ และรายงานผล

## 1. วัตถุประสงค์

- ยืนยันว่า business logic ของแต่ละ service ทำงานถูกต้องตามกติกาเกม
- ยืนยันว่า REST API ตอบ status code และรูปแบบ error ตามใบงานข้อ 7 (200 / 201 / 400 / 404 / 409)
- ป้องกันการถดถอย (regression) เมื่อสมาชิกหลายคนแก้โค้ดพร้อมกัน

## 2. ขอบเขต

| อยู่ในขอบเขต | นอกขอบเขต |
|---|---|
| service ทุกตัวใน `com.battleiq.service` | ประสิทธิภาพ / โหลด |
| REST API ส่วนผู้ใช้และโปรไฟล์ | ความปลอดภัยเชิงลึก (ยังไม่มีระบบสิทธิ์) |
| validation ของ request DTO | หน้าเว็บแบบอัตโนมัติ (ทดสอบด้วยมือ ดูข้อ 6) |

## 3. ระดับการทดสอบ

| ระดับ | เครื่องมือ | วิธี | ไฟล์ |
|---|---|---|---|
| Unit | JUnit 5 + Mockito | mock repository และ dependency ทั้งหมด ทดสอบ logic ของ class เดียว | `*ServiceTest`, `*CommandTest`, `*StrategyTest` |
| Integration (API) | Spring Boot Test + MockMvc | ยิง HTTP ผ่าน controller จริง ต่อฐานข้อมูลจริง rollback ทุกเทส | `UserApiIntegrationTest` |
| System | ทดสอบด้วยมือผ่านหน้าเว็บ | เล่นเกมครบรอบตาม checklist ข้อ 6 | — |
| Smoke | Spring Boot Test | แอปสตาร์ตและต่อฐานข้อมูลได้ | `BattleIqApplicationTests` |

## 4. เทคนิคออกแบบกรณีทดสอบ

- **Equivalence Partitioning** แบ่งข้อมูลเข้าเป็นกลุ่มที่ควรได้ผลเหมือนกัน เช่น ตอบถูก / ตอบผิด, ชื่อซ้ำ / ไม่ซ้ำ, session กำลังเล่น / จบแล้ว
- **Boundary Value Analysis** ทดสอบค่าขอบ เช่น รหัสผ่าน 5 และ 6 ตัว, ชื่อ 101 ตัว, streak 5 (เพดานตัวคูณ), เวลาเหลือ 0
- **Negative Testing** ส่งข้อมูลที่ไม่ควรผ่าน เช่น อีเมลผิดรูปแบบ, user ที่ไม่มี, ตอบคำถามซ้ำ
- **State-based Testing** ตรวจการเปลี่ยนสถานะ เช่น จบ session ซ้ำต้องถูกปฏิเสธ, streak กลับเป็น 0 เมื่อได้คะแนน 0
- **Information Hiding Check** ตรวจว่าข้อมูลที่ไม่ควรรั่ว (รหัสผ่าน) ไม่อยู่ใน response

## 5. รายการกรณีทดสอบ

สถานะอ้างอิงผลการรันล่าสุดในข้อ 8

### 5.1 Unit — ผู้ใช้ (`UserServiceTest`, pheerapat)

| ID | กรณี | เทคนิค | ผลที่คาด | สถานะ |
|---|---|---|---|---|
| TC-U01 | สมัครสำเร็จ | EP | สร้าง `UserProfile` พร้อมกัน รหัสผ่านถูกเข้ารหัส level 1 คะแนน 0 | ผ่าน |
| TC-U02 | ชื่อผู้ใช้ซ้ำ | EP / Negative | `ConflictException` ไม่เรียก save | ผ่าน |
| TC-U03 | อีเมลซ้ำ | EP / Negative | `ConflictException` ไม่เรียก save | ผ่าน |
| TC-U04 | login ถูกต้อง | EP | คืนข้อมูลผู้ใช้ | ผ่าน |
| TC-U05 | รหัสผ่านผิด | Negative | `InvalidCredentialsException` | ผ่าน |
| TC-U06 | ไม่มีผู้ใช้นี้ | Negative / Security | error ข้อความเดียวกับรหัสผิด ไม่เรียก matcher | ผ่าน |
| TC-U07 | หา user ไม่เจอ | Negative | `ResourceNotFoundException` | ผ่าน |
| TC-U08 | user ไม่มีโปรไฟล์ | Boundary (null) | ไม่พัง ค่าโปรไฟล์เป็น null | ผ่าน |

### 5.2 Unit — โปรไฟล์ (`UserProfileServiceTest`, pheerapat)

| ID | กรณี | เทคนิค | ผลที่คาด | สถานะ |
|---|---|---|---|---|
| TC-P01 | ได้ 45 คะแนนจาก 80 | State | คะแนนรวม 125, level 2, streak +1 | ผ่าน |
| TC-P02 | ได้ 0 คะแนน | Boundary / State | คะแนนรวมเท่าเดิม streak กลับเป็น 0 | ผ่าน |
| TC-P03 | แก้ชื่อและรูป | EP | ค่าใหม่ถูกบันทึก | ผ่าน |
| TC-P04 | โปรไฟล์ไม่มี | Negative | `ResourceNotFoundException` | ผ่าน |

### 5.3 Unit — การคิดคะแนน (`ScoringStrategyTest`, thanakit)

| ID | กรณี | เทคนิค | ผลที่คาด | สถานะ |
|---|---|---|---|---|
| TC-S01 | ตอบทันที (เหลือเวลาเต็ม) | Boundary | 20 คะแนน | ผ่าน |
| TC-S02 | ใช้เวลาเกินกำหนด | Boundary | 10 คะแนน (ไม่ติดลบ) | ผ่าน |
| TC-S03 | streak 3 และ streak 9 | Boundary (เพดาน 5) | 26 และ 30 | ผ่าน |
| TC-S04 | เลือกสูตรตาม streak และเวลาจำกัด | EP | Basic / TimeBonus / Streak ตามเงื่อนไข | ผ่าน |

### 5.4 Unit — การตอบคำถาม (`SubmitAnswerCommandTest`, kongpob)

| ID | กรณี | เทคนิค | ผลที่คาด | สถานะ |
|---|---|---|---|---|
| TC-C01 | ตอบถูก | EP | ได้คะแนนจาก strategy บันทึกลง detail | ผ่าน |
| TC-C02 | ตอบผิด | EP | 0 คะแนน ไม่เรียก strategy | ผ่าน |
| TC-C03 | ตอบข้อเดิมซ้ำ | State / Negative | `ConflictException` | ผ่าน |

### 5.5 Unit — รอบการเล่น (`QuizSessionServiceTest`, rattaphum)

| ID | กรณี | เทคนิค | ผลที่คาด | สถานะ |
|---|---|---|---|---|
| TC-Q01 | จบเกม | State | รวมคะแนนจากทุกข้อ ส่ง `GameCompletedEvent` | ผ่าน |
| TC-Q02 | จบเกมที่จบแล้ว | State / Negative | `ConflictException` ไม่ส่ง event | ผ่าน |
| TC-Q03 | session ไม่มี | Negative | `ResourceNotFoundException` | ผ่าน |

### 5.6 Unit — หมวดหมู่ (`CategoryServiceTest`, noppavit)

| ID | กรณี | เทคนิค | ผลที่คาด | สถานะ |
|---|---|---|---|---|
| TC-G01 | เพิ่มหมวดหมู่ | EP | บันทึกและคืน DTO | ผ่าน |
| TC-G02 | ชื่อซ้ำ | Negative | `ConflictException` ไม่บันทึก | ผ่าน |
| TC-G03 | แก้หมวดหมู่ที่ไม่มี | Negative | `CategoryNotFoundException` | ผ่าน |

### 5.7 Unit — ห้องแข่งขัน (`RoomServiceTest`, pheerapat)

| ID | กรณี | เทคนิค | ผลที่คาด | สถานะ |
|---|---|---|---|---|
| TC-R01 | สร้างห้อง | EP | รหัส 6 ตัว เจ้าของห้องอยู่ในรายชื่อ สถานะ WAITING | ผ่าน |
| TC-R02 | เข้าห้องซ้ำ | State | ไม่เพิ่มคนซ้ำ ไม่บันทึกใหม่ | ผ่าน |
| TC-R03 | เข้าห้องที่เริ่มแล้ว | State / Negative | `ConflictException` | ผ่าน |
| TC-R04 | คนที่ไม่ใช่เจ้าของกดเริ่ม | Negative | `ConflictException` ไม่สร้าง session | ผ่าน |
| TC-R05 | เริ่มเมื่อมีคนเดียว | Boundary | `ConflictException` | ผ่าน |
| TC-R06 | เริ่มแข่ง | EP | สุ่มคำถาม 1 ครั้ง สร้าง session ให้ทุกคนด้วยคำถามชุดเดียวกัน | ผ่าน |
| TC-R07 | ทุกคนเล่นจบ | State | ห้องเป็น FINISHED เรียงคะแนนมากไปน้อย | ผ่าน |

### 5.8 Integration — API ผู้ใช้และโปรไฟล์ (`UserApiIntegrationTest`, pheerapat)

| ID | กรณี | เทคนิค | ผลที่คาด | สถานะ |
|---|---|---|---|---|
| TC-A01 | `POST /users/register` สำเร็จ | EP / Info hiding | 201, level 1, ไม่มี field `password` | ผ่าน |
| TC-A02 | รหัสผ่าน 5 ตัว | Boundary | 400 | ผ่าน |
| TC-A03 | รหัสผ่าน 6 ตัว | Boundary | 201 | ผ่าน |
| TC-A04 | อีเมลผิดรูปแบบ | Negative | 400 | ผ่าน |
| TC-A05 | สมัครซ้ำ | Negative | 409 พร้อม `status: 409` | ผ่าน |
| TC-A06 | login ถูก / ผิด | EP | 200 / 401 | ผ่าน |
| TC-A07 | `GET` และ `PUT /profiles/user/{id}` | EP | 200 ค่าที่แก้ถูกคืนกลับ | ผ่าน |
| TC-A08 | โปรไฟล์ของ user ที่ไม่มี | Negative | 404 ทั้ง GET และ PUT | ผ่าน |
| TC-A09 | ชื่อ 101 ตัวอักษร | Boundary | 400 | ผ่าน |

### 5.9 Integration — ความทนทานของ API ต่อข้อมูลผิดปกติ (`ApiValidationIntegrationTest`, pheerapat)

ทุกกรณีต้องได้ 4xx ใน error format มาตรฐาน ห้ามหลุดเป็น 500 ก่อนแก้ TC-V02, TC-V03, TC-V08 เคยได้ 500

| ID | กรณี | เทคนิค | ผลที่คาด | สถานะ |
|---|---|---|---|---|
| TC-V01 | ชื่อผู้ใช้ 500 ตัวอักษร | Boundary | 400 | ผ่าน |
| TC-V02 | ชื่อที่แสดง 500 ตัวอักษร | Boundary | 400 (เดิม 500 เพราะเกินคอลัมน์) | ผ่าน |
| TC-V03 | รหัสผ่าน 73 ตัวอักษร | Boundary (BCrypt 72) | 400 (เดิม 500) | ผ่าน |
| TC-V04 | รหัสผ่าน 72 ตัวอักษรพอดี | Boundary | 201 | ผ่าน |
| TC-V05 | ชื่อผู้ใช้มีช่องว่าง/อักขระพิเศษ | Negative | 400 | ผ่าน |
| TC-V06 | อีเมลยาวเกิน 100 แม้รูปแบบถูก | Boundary | 400 | ผ่าน |
| TC-V07 | ตัวเลือกคำตอบ 500 ตัวอักษร | Boundary | 400 | ผ่าน |
| TC-V08 | `?sort=` ด้วย field ที่ไม่มี | Negative | 400 (เดิม 500) | ผ่าน |
| TC-V09 | JSON พัง | Negative | 400 | ผ่าน |
| TC-V10 | id ไม่ใช่ตัวเลข / เกิน Long | Negative / Boundary | 400 | ผ่าน |
| TC-V11 | Content-Type เป็น text/plain | Negative | 415 ใน format เดียวกัน | ผ่าน |
| TC-V12 | `timeTakenSeconds` เกิน int | Boundary | 400 | ผ่าน |

## 6. System test (ทำด้วยมือผ่านหน้าเว็บ)

| ID | ขั้นตอน | ผลที่คาด | สถานะ |
|---|---|---|---|
| TC-M01 | `/register` สมัครสมาชิก | ถูกพาไปหน้าเลือกหมวดหมู่ เมนูแสดงชื่อผู้ใช้ | ยังไม่ได้ทดสอบ |
| TC-M02 | `/categories` เลือกหมวดแล้วกดเริ่มเล่น | เข้าหน้าเล่นเกม มี 5 ข้อ | ยังไม่ได้ทดสอบ |
| TC-M03 | ตอบครบ 5 ข้อ ปล่อยหมดเวลา 1 ข้อ | ข้อที่หมดเวลาได้ 0 คะแนน เปลี่ยนข้อได้ | ยังไม่ได้ทดสอบ |
| TC-M04 | กดดูผลคะแนน | หน้าสรุปผลแสดงคะแนนและเฉลยทุกข้อ | ยังไม่ได้ทดสอบ |
| TC-M05 | `/profile` | คะแนนรวม เลเวล streak เปลี่ยนตามผล | ยังไม่ได้ทดสอบ |
| TC-M06 | `/history` และ `/leaderboard` | เห็นรอบที่เพิ่งเล่นและอันดับ | ยังไม่ได้ทดสอบ |
| TC-M07 | `/admin/categories`, `/admin/questions` | เพิ่ม แก้ไข ลบได้ ชื่อซ้ำขึ้นข้อความผิดพลาด | ยังไม่ได้ทดสอบ |
| TC-M09 | `/rooms` สร้างห้องจากเครื่องหนึ่ง เข้าร่วมด้วยรหัสจากอีกเครื่อง เจ้าของกดเริ่ม | ทั้งสองเครื่องถูกพาไปหน้าเล่นเกมพร้อมกัน คำถามชุดเดียวกัน จบแล้วเห็นอันดับในห้อง | ยังไม่ได้ทดสอบ |
| TC-M08 | เปิดเว็บด้วยมือถือหรือจอแคบ | เมนูและปุ่มตัวเลือกไม่ล้นจอ | ยังไม่ได้ทดสอบ |

## 7. การผูกกับข้อกำหนด (Traceability)

| ข้อกำหนดในใบงาน | กรณีทดสอบ |
|---|---|
| ข้อ 6 One-to-One `users` – `user_profiles` | TC-U01, TC-A01 |
| ข้อ 7 status code 201 / 400 / 404 / 409 | TC-A01–TC-A09 |
| ข้อ 7 Bean Validation | TC-A02, TC-A03, TC-A04, TC-A09 |
| ข้อ 7 Global Exception Handler และ error format | TC-A02, TC-A05, TC-A06 (`status` ใน body) |
| ข้อ 5 Strategy pattern | TC-S01–TC-S04 |
| ข้อ 5 Command pattern | TC-C01–TC-C03 |
| ข้อ 5 Observer pattern | TC-Q01 (ส่ง event), TC-P01 (ผลของ event) |
| กติกาเกม: คะแนน เลเวล streak | TC-P01, TC-P02, TC-S01–TC-S03 |

## 8. วิธีรันและผลล่าสุด

รันทั้งหมดจากโฟลเดอร์ `code/battle-iq` (ต้องมีไฟล์ `.env` เพราะเทสระดับ integration ต่อฐานข้อมูลจริง)

```
.\mvnw.cmd test
```

สร้างรายงาน HTML

```
.\mvnw.cmd surefire-report:report-only
```

รายงานล่าสุดอยู่ที่ `test/report/` ผลการรันล่าสุด (7 ต.ค. 2026): 54 กรณี ผ่าน 54 ล้มเหลว 0

## 9. เกณฑ์การผ่าน

- เทสอัตโนมัติผ่าน 100% ก่อน merge เข้า `develop` ทุกครั้ง
- ทุก service ที่มี business logic ต้องมี unit test อย่างน้อย 3 กรณี ครอบคลุมทั้งกรณีปกติและกรณีผิดพลาด
- System test ข้อ 6 ต้องผ่านครบก่อนวันนำเสนอ
