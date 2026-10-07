# State Diagram

Entity ที่มีสถานะคือ `QuizSession` และ `Room`

## QuizSession

```mermaid
stateDiagram-v2
    [*] --> IN_PROGRESS : POST /quiz-sessions/start<br/>หรือเจ้าของห้องกดเริ่ม
    IN_PROGRESS --> IN_PROGRESS : POST /answers<br/>บันทึกคำตอบทีละข้อ
    IN_PROGRESS --> COMPLETED : POST /complete<br/>รวมคะแนน publish GameCompletedEvent
    COMPLETED --> [*]

    note right of IN_PROGRESS
        ตอบได้เฉพาะข้อที่ยังไม่เคยตอบ (ไม่งั้น 409)
        ดูผล (/result) ยังไม่ได้ (409)
    end note
    note right of COMPLETED
        complete ซ้ำ → 409
        answers → 409
        /result ใช้ได้
    end note
```

สถานะ `ABANDONED` มีใน enum `SessionStatus` ไว้สำหรับรอบที่ผู้เล่นออกกลางคัน แต่เวอร์ชันนี้ยังไม่มี endpoint ที่เปลี่ยนเป็นสถานะนี้ รอบที่ค้างจะอยู่ที่ IN_PROGRESS และกด "เล่นต่อ" ได้จากหน้าประวัติ

## Room

```mermaid
stateDiagram-v2
    [*] --> WAITING : POST /rooms<br/>เจ้าของเป็นผู้เล่นคนแรก
    WAITING --> WAITING : POST /join<br/>เพิ่มผู้เล่น (สูงสุด 8)
    WAITING --> IN_PROGRESS : POST /start โดยเจ้าของ<br/>ผู้เล่น ≥ 2 สุ่มคำถาม 1 ชุด สร้าง session ทุกคน
    IN_PROGRESS --> IN_PROGRESS : ผู้เล่นทยอยเล่นจบ<br/>GET /rooms/code แสดงคะแนนสด
    IN_PROGRESS --> FINISHED : GET /rooms/code พบว่า<br/>ทุก session เป็น COMPLETED
    FINISHED --> [*]

    note right of WAITING
        join ซ้ำ → คืนห้องเดิม
        join เมื่อเต็ม → 409
        start โดยคนอื่น → 409
    end note
    note right of IN_PROGRESS
        join → 409
        start ซ้ำ → 409
    end note
```

การเปลี่ยนเป็น FINISHED เกิดตอนมีคนเรียก `GET /rooms/{code}` (หน้าห้อง polling ทุก 2 วินาที) ไม่มี scheduler แยก ถ้าไม่มีใครเปิดหน้าห้อง สถานะจะค้างที่ IN_PROGRESS จนกว่าจะมีคนเปิด
