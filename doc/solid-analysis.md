# SOLID Analysis

ตำแหน่งอ้างอิงเป็นไฟล์ใน `code/battle-iq/src/main/java/com/battleiq/` เลขบรรทัดเป็นค่าโดยประมาณ ณ วันที่ 7 ต.ค. 2026 ถ้าโค้ดเลื่อนให้ดูที่ชื่อเมธอด

## S — Single Responsibility

| ไฟล์ | ตำแหน่ง | เหตุผล |
|---|---|---|
| `controller/UserController.java` | ทั้งไฟล์ | รับ request ตรวจ `@Valid` และคืน `ResponseEntity` เท่านั้น ไม่มี business logic ส่งต่อ `UserService` ทุกอย่าง |
| `service/UserService.java` | `register()` | ทำเฉพาะเรื่องบัญชี (ตรวจซ้ำ เข้ารหัส บันทึก) การเข้ารหัสแยกไปที่ `PasswordEncoder` การคิดเลเวลแยกไปที่ `UserProfileService` |
| `service/UserProfileService.java` | `applyGameResult()` | รับผิดชอบเฉพาะกติกาโปรไฟล์ (คะแนนรวม เลเวล streak) ไม่รู้เรื่องรอบการเล่น |
| `service/command/SubmitAnswerCommand.java` | `execute()` | ตรวจคำตอบและคิดคะแนน 1 ข้อ ไม่โหลดข้อมูลและไม่บันทึกเอง (หน้าที่ของ `AnswerService`) |
| `exception/GlobalExceptionHandler.java` | ทั้งไฟล์ | แปลง exception เป็น `ErrorResponseDTO` ที่เดียว controller และ service ไม่ต้องจัดการ HTTP status |
| `dto/*RequestDTO.java` | annotation บน field | กฎการตรวจข้อมูลเข้าอยู่ที่ DTO ไม่ปนใน service |

## O — Open/Closed

| ไฟล์ | ตำแหน่ง | เหตุผล |
|---|---|---|
| `service/scoring/ScoringStrategy.java` | interface | เพิ่มสูตรคะแนนใหม่ด้วยการเพิ่ม class ที่ implement interface ไม่ต้องแก้ `SubmitAnswerCommand` |
| `service/scoring/ScoringStrategySelector.java` | `select()` | จุดเดียวที่ต้องเพิ่มเงื่อนไขเมื่อมีสูตรใหม่ โค้ดส่วนอื่นปิดต่อการแก้ไข |
| `event/GameCompletedEvent.java` + `GameCompletionEventListener.java` | `@EventListener` | เพิ่มสิ่งที่ต้องทำตอนจบเกม (badge, แจ้งเตือน) ด้วย listener ใหม่ โดย `QuizSessionService.completeSession()` ไม่เปลี่ยน |
| `exception/GlobalExceptionHandler.java` | เมธอด `@ExceptionHandler` แต่ละตัว | เพิ่ม exception ใหม่ด้วยการเพิ่มเมธอด ไม่แก้ของเดิม |

## L — Liskov Substitution

| ไฟล์ | ตำแหน่ง | เหตุผล |
|---|---|---|
| `service/scoring/BasicScoringStrategy.java`, `TimeBonusScoringStrategy.java`, `StreakScoringStrategy.java` | `calculate()` | ทั้งสามรับพารามิเตอร์เดียวกัน คืนจำนวนเต็ม ≥ 0 เสมอ ไม่โยน exception ใช้แทนกันได้ทุกที่ที่รับ `ScoringStrategy` (`SubmitAnswerCommandTest` ใช้ mock แทนได้) |
| `service/command/SubmitAnswerCommand.java` | implements `Command<AnswerResultDTO>` | ทำตามสัญญา `execute()` คืนผลหรือโยน `ConflictException` ที่ handler รองรับ ไม่มี `UnsupportedOperationException` |
| `repository/*Repository.java` | extends `JpaRepository` | ใช้เมธอดมาตรฐาน (`findById`, `save`, `findAll(Pageable)`) ได้ครบ ไม่ override ให้พฤติกรรมเพี้ยน |

## I — Interface Segregation

| ไฟล์ | ตำแหน่ง | เหตุผล |
|---|---|---|
| `service/scoring/ScoringStrategy.java` | มีเมธอดเดียว `calculate()` | ผู้ implement ไม่ถูกบังคับให้มีเมธอดที่ไม่ใช้ |
| `service/command/Command.java` | มีเมธอดเดียว `execute()` | interface เล็ก รองรับ command ชนิดอื่นในอนาคต |
| `repository/UserRepository.java`, `UserProfileRepository.java`, `QuizDetailRepository.java` | เมธอดเฉพาะเช่น `existsByUsername`, `findByUserId`, `findByQuizSessionIdAndQuestionId` | แต่ละ repository มีเฉพาะ query ที่ service ของมันใช้ ไม่มี repository กลางก้อนใหญ่ |
| `dto/` | `UserRequestDTO`, `LoginRequestDTO`, `ProfileUpdateDTO` แยกกัน | แต่ละ endpoint รับเฉพาะ field ที่ต้องใช้ ไม่ใช้ `User` entity เป็น request |

## D — Dependency Inversion

| ไฟล์ | ตำแหน่ง | เหตุผล |
|---|---|---|
| ทุก service และ controller | `@RequiredArgsConstructor` + field `private final` | Constructor injection ทั้งหมด ไม่มี `@Autowired` บน field และไม่มี `new` service ใน service |
| `service/UserService.java` | field `PasswordEncoder passwordEncoder` | ขึ้นกับ interface `PasswordEncoder` ของ Spring Security ไม่ใช่ `BCryptPasswordEncoder` ตรง ๆ สลับอัลกอริทึมได้ที่ `config/PasswordConfig` |
| `service/command/SubmitAnswerCommand.java` | field `ScoringStrategySelector` และตัวแปร `ScoringStrategy strategy` | เรียกผ่าน interface `ScoringStrategy` ไม่รู้จัก class สูตรจริง |
| `service/QuizSessionService.java` | field `ApplicationEventPublisher eventPublisher` | ขึ้นกับ abstraction ของ Spring ไม่รู้ว่าใครรับ event |
| `service/*Service.java` | field `*Repository` | ขึ้นกับ interface ของ Spring Data (JPA implementation ถูกสร้างตอน runtime) |

| `controller/*Controller.java` | field `private final XxxService` | Controller ขึ้นกับ interface ใน `service/` ส่วน implementation อยู่ใน `service/impl/*ServiceImpl` Spring inject ให้ตอน runtime เปลี่ยน implementation ได้โดย controller ไม่เปลี่ยน |
| `service/impl/RoomServiceImpl.java` | field `QuizSessionService quizSessionService` | service เรียก service อื่นผ่าน interface เช่นกัน |
| `mapper/*Mapper.java` | static method `toDTO`, `toResponse` | การแปลง entity → DTO แยกออกจาก service เป็นแพ็กเกจ `mapper/` service ไม่ต้องรู้โครงสร้าง DTO |

## จุดที่ยังไม่สมบูรณ์

- `ScoringStrategySelector` ขึ้นกับ concrete strategy ทั้งสามเพราะต้องรู้ว่าจะเลือกตัวไหน เป็นข้อยกเว้นที่ยอมรับได้สำหรับ class ที่ทำหน้าที่เลือก
- `QuestionFactory` เป็น concrete class ที่ `QuizSessionServiceImpl` และ `RoomServiceImpl` เรียกตรง เพราะมีวิธีสร้างแบบเดียว ถ้าในอนาคตมีการสุ่มแบบอื่น (ตามความยาก) ควรแยกเป็น interface
- DTO ยังอยู่ในแพ็กเกจ `dto/` เดียว ไม่ได้แยก `dto/request` กับ `dto/response` ตามตัวอย่างในใบงาน ใช้ชื่อไฟล์ (`*RequestDTO`, `*ResponseDTO`, `*DTO`) แยกแทน
