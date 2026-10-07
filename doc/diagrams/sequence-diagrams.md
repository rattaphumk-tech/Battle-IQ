# Sequence Diagrams

3 scenario หลัก: สมัครสมาชิก, ตอบคำถาม 1 ข้อ (Command + Strategy), จบเกม (Observer) และเพิ่มการเริ่มแข่งในห้อง

## 1. สมัครสมาชิก

```mermaid
sequenceDiagram
    actor P as ผู้เล่น
    participant V as register.html
    participant C as UserController
    participant S as UserService
    participant E as PasswordEncoder
    participant R as UserRepository
    participant DB as PostgreSQL

    P->>V: กรอกฟอร์ม กดสมัคร
    V->>V: ตรวจรหัสผ่านสองช่องตรงกัน
    V->>C: POST /api/v1/users/register (JSON)
    C->>C: @Valid UserRequestDTO
    alt ข้อมูลไม่ผ่าน
        C-->>V: 400 ErrorResponseDTO
    end
    C->>S: register(dto)
    S->>R: existsByUsername / existsByEmail
    R->>DB: SELECT
    DB-->>R: ผล
    alt ซ้ำ
        S-->>C: ConflictException
        C-->>V: 409
    end
    S->>E: encode(password)
    E-->>S: BCrypt hash
    S->>S: สร้าง User + UserProfile (level 1, score 0)
    S->>R: save(user)  (cascade profile)
    R->>DB: INSERT users, INSERT user_profiles
    S-->>C: UserResponseDTO (ไม่มี password)
    C-->>V: 201 Created
    V->>V: เก็บ user ใน localStorage
    V-->>P: ไปหน้า /categories
```

## 2. ตอบคำถาม 1 ข้อ (Command + Strategy)

```mermaid
sequenceDiagram
    actor P as ผู้เล่น
    participant V as play.html
    participant C as AnswerController
    participant S as AnswerService
    participant DR as QuizDetailRepository
    participant PR as UserProfileRepository
    participant CMD as SubmitAnswerCommand
    participant SEL as ScoringStrategySelector
    participant ST as ScoringStrategy

    P->>V: กดตัวเลือก B
    V->>V: หยุดเวลา คำนวณ timeTaken
    V->>C: POST /quiz-sessions/{id}/answers {questionId, "B", timeTaken}
    C->>S: submitAnswer(sessionId, dto)
    S->>DR: findByQuizSessionIdAndQuestionId
    DR-->>S: QuizDetail (พร้อม Question, QuizSession)
    alt session ไม่ใช่ IN_PROGRESS
        S-->>C: ConflictException → 409
    end
    S->>PR: findByUserId(userId)
    PR-->>S: currentStreak
    S->>CMD: new SubmitAnswerCommand(detail, "B", timeTaken, streak, selector)
    S->>CMD: execute()
    alt ตอบข้อนี้ไปแล้ว
        CMD-->>S: ConflictException → 409
    end
    CMD->>CMD: correct = correctAnswer == "B"
    opt ตอบถูก
        CMD->>SEL: select(question, streak)
        SEL-->>CMD: TimeBonus หรือ Streak หรือ Basic
        CMD->>ST: calculate(question, timeTaken, streak)
        ST-->>CMD: score
    end
    CMD->>CMD: detail.userAns / isCorrect / timeTaken / scoreEarned
    CMD-->>S: AnswerResultDTO
    S->>DR: save(detail)
    S-->>C: AnswerResultDTO
    C-->>V: 200 {correct, correctAnswer, scoreEarned}
    V-->>P: ไฮไลต์เฉลย แสดงคะแนน
```

## 3. จบเกมและอัปเดตโปรไฟล์ (Observer)

```mermaid
sequenceDiagram
    actor P as ผู้เล่น
    participant V as play.html
    participant C as QuizSessionController
    participant S as QuizSessionService
    participant SR as QuizSessionRepository
    participant PUB as ApplicationEventPublisher
    participant L as GameCompletionEventListener
    participant PS as UserProfileService
    participant PR as UserProfileRepository

    P->>V: กด "ดูผลคะแนน" หลังข้อสุดท้าย
    V->>C: POST /quiz-sessions/{id}/complete
    C->>S: completeSession(id)
    S->>SR: findById(id)
    SR-->>S: QuizSession + details
    alt status != IN_PROGRESS
        S-->>C: ConflictException → 409
    end
    S->>S: totalScore = Σ detail.scoreEarned
    S->>S: status = COMPLETED, completedAt = now
    S->>SR: save(session)
    S->>PUB: publishEvent(GameCompletedEvent(sessionId, userId, totalScore))
    PUB->>L: onGameCompleted(event)   (ใน transaction เดียวกัน)
    L->>PS: applyGameResult(userId, totalScore)
    PS->>PR: findByUserId(userId)
    PR-->>PS: UserProfile
    PS->>PS: totalScore += score, level = total/100+1, streak +1 หรือ 0
    PS->>PR: save(profile)
    PS-->>L: done
    L-->>PUB: done
    S-->>C: QuizSessionResponseDTO
    C-->>V: 200
    V-->>P: ไปหน้า /result (หรือ /room ถ้าเล่นในห้อง)
```

## 4. เริ่มแข่งในห้อง (เพิ่มเติม)

```mermaid
sequenceDiagram
    actor H as เจ้าของห้อง
    actor G as ผู้เล่นคนอื่น
    participant VH as room.html (host)
    participant VG as room.html (guest)
    participant C as RoomController
    participant S as RoomService
    participant F as QuestionFactory
    participant QS as QuizSessionService

    loop ทุก 2 วินาที
        VG->>C: GET /rooms/{code}
        C-->>VG: {status: WAITING, players}
    end
    H->>VH: กดเริ่มแข่ง
    VH->>C: POST /rooms/{code}/start {userId}
    C->>S: startRoom(code, userId)
    alt ไม่ใช่เจ้าของ / ผู้เล่น < 2 / เริ่มแล้ว
        S-->>C: ConflictException → 409
    end
    S->>F: createQuizQuestions(categoryId, 5)
    F-->>S: คำถาม 1 ชุด
    loop ผู้เล่นทุกคนในห้อง
        S->>QS: createSession(user, category, questions)
        QS-->>S: QuizSession
        S->>S: player.quizSession = session
    end
    S->>S: status = IN_PROGRESS
    S-->>C: RoomResponseDTO (players มี sessionId)
    C-->>VH: 200
    VH-->>H: ไป /play?session=A&room=CODE
    VG->>C: GET /rooms/{code}
    C-->>VG: {status: IN_PROGRESS, players[me].sessionId = B}
    VG-->>G: ไป /play?session=B&room=CODE
```
