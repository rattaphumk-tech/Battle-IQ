# Class Diagram

แสดงคลาสหลักแยกตาม layer และตำแหน่งของ Design Pattern (กล่องที่มี «pattern» ในชื่อ) ละ DTO และ getter/setter เพื่อให้อ่านได้

```mermaid
classDiagram
    direction TB

    %% ---------- Presentation ----------
    class UserController {
        +registerUser(UserRequestDTO)
        +login(LoginRequestDTO)
        +getUserById(id)
    }
    class QuizSessionController {
        +startQuizSession(QuizSessionRequestDTO)
        +getSessionQuestions(id)
        +completeQuizSession(id)
    }
    class AnswerController {
        +submitAnswer(sessionId, AnswerRequestDTO)
    }
    class RoomController {
        +createRoom() +joinRoom() +startRoom() +getRoom()
    }
    class GlobalExceptionHandler {
        <<RestControllerAdvice>>
        +handle*(Exception) ErrorResponseDTO
    }

    %% ---------- Service ----------
    class UserService {
        -UserRepository userRepository
        -PasswordEncoder passwordEncoder
        +register() +login() +getUserById()
    }
    class UserProfileService {
        +applyGameResult(userId, score)
        +updateProfile(userId, dto)
    }
    class QuizSessionService {
        -ApplicationEventPublisher eventPublisher
        +startSession() +createSession() +completeSession()
    }
    class AnswerService {
        <<Command invoker>>
        +submitAnswer(sessionId, dto)
    }
    class RoomService {
        +createRoom() +joinRoom() +startRoom() +getRoom()
    }
    class QuestionFactory {
        <<Factory>>
        +createQuizQuestions(categoryId, count)
    }

    %% ---------- Strategy ----------
    class ScoringStrategy {
        <<interface Strategy>>
        +calculate(question, time, streak) int
    }
    class BasicScoringStrategy
    class TimeBonusScoringStrategy
    class StreakScoringStrategy
    class ScoringStrategySelector {
        <<Strategy context>>
        +select(question, streak) ScoringStrategy
    }

    %% ---------- Command ----------
    class Command~R~ {
        <<interface Command>>
        +execute() R
    }
    class SubmitAnswerCommand {
        <<Command>>
        -QuizDetail detail
        -String answer
        +execute() AnswerResultDTO
    }

    %% ---------- Observer ----------
    class GameCompletedEvent {
        <<Observer event>>
        sessionId userId totalScore
    }
    class GameCompletionEventListener {
        <<Observer>>
        +onGameCompleted(event)
    }

    %% ---------- Repository ----------
    class UserRepository { <<interface>> }
    class UserProfileRepository { <<interface>> }
    class QuizSessionRepository { <<interface>> }
    class QuizDetailRepository { <<interface>> }
    class QuestionRepository { <<interface>> }
    class RoomRepository { <<interface>> }

    %% ---------- Domain ----------
    class User
    class UserProfile
    class Category
    class Question
    class QuizSession
    class QuizDetail
    class Room
    class RoomPlayer

    %% relations: presentation -> service
    UserController --> UserService
    QuizSessionController --> QuizSessionService
    AnswerController --> AnswerService
    RoomController --> RoomService

    %% service -> repository
    UserService --> UserRepository
    UserProfileService --> UserProfileRepository
    QuizSessionService --> QuizSessionRepository
    QuizSessionService --> QuestionFactory
    QuestionFactory --> QuestionRepository
    AnswerService --> QuizDetailRepository
    AnswerService --> UserProfileRepository
    RoomService --> RoomRepository
    RoomService --> QuestionFactory
    RoomService --> QuizSessionService

    %% strategy
    ScoringStrategy <|.. BasicScoringStrategy
    ScoringStrategy <|.. TimeBonusScoringStrategy
    ScoringStrategy <|.. StreakScoringStrategy
    StreakScoringStrategy --> TimeBonusScoringStrategy
    ScoringStrategySelector --> ScoringStrategy
    AnswerService --> ScoringStrategySelector

    %% command
    Command <|.. SubmitAnswerCommand
    AnswerService ..> SubmitAnswerCommand : creates
    SubmitAnswerCommand --> ScoringStrategySelector
    SubmitAnswerCommand --> QuizDetail

    %% observer
    QuizSessionService ..> GameCompletedEvent : publishes
    GameCompletionEventListener ..> GameCompletedEvent : listens
    GameCompletionEventListener --> UserProfileService

    %% domain
    User "1" -- "1" UserProfile
    Category "1" -- "*" Question
    User "1" -- "*" QuizSession
    QuizSession "1" *-- "*" QuizDetail
    Question "1" -- "*" QuizDetail
    Room "1" *-- "*" RoomPlayer
    User "1" -- "*" RoomPlayer
    RoomPlayer "0..1" -- "0..1" QuizSession
```

## ตำแหน่ง Design Pattern

| Pattern | คลาส | บทบาท |
|---|---|---|
| Strategy | `ScoringStrategy` | interface กลยุทธ์ |
| | `BasicScoringStrategy`, `TimeBonusScoringStrategy`, `StreakScoringStrategy` | concrete strategy |
| | `ScoringStrategySelector` | context เลือกกลยุทธ์ตาม streak และเวลาจำกัด |
| Command | `Command<R>` | interface คำสั่ง |
| | `SubmitAnswerCommand` | concrete command ห่อการตอบคำถาม 1 ข้อ |
| | `AnswerService` | invoker สร้างและสั่ง execute |
| | `QuizDetail` | receiver ที่ถูกแก้ไข |
| Observer | `GameCompletedEvent` | เหตุการณ์ |
| | `QuizSessionService` | subject ที่ publish ผ่าน `ApplicationEventPublisher` |
| | `GameCompletionEventListener` | observer ที่รับและส่งต่อให้ `UserProfileService` |
| Factory | `QuestionFactory` | สร้างชุดคำถามให้ `QuizSessionService` และ `RoomService` |

คลาสที่ไม่ได้วาด: `CategoryService`, `QuestionService`, `QuizResultService`, `LeaderboardService` และ controller คู่ของมัน มีโครงสร้างเดียวกับ `UserService` → `UserRepository` (controller → service → repository) ไม่มี pattern เพิ่ม
