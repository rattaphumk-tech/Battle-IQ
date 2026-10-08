# Diagrams

ทุก diagram มี 2 รูปแบบ: ไฟล์ `.md` (Mermaid แสดงบน GitHub ได้ทันที พร้อมคำอธิบาย) และ PlantUML (`plantuml/*.puml` → รูป `img/*.png`)

| ชนิด | Mermaid + คำอธิบาย | PlantUML PNG |
|---|---|---|
| Use Case + Use Case Description | [use-case.md](use-case.md) | [img/use-case.png](img/use-case.png) |
| Domain Model | [domain-model.md](domain-model.md) | [img/domain-model.png](img/domain-model.png) |
| Class Diagram (ระบุ pattern) | [class-diagram.md](class-diagram.md) | [img/class-diagram.png](img/class-diagram.png) |
| Sequence: สมัครสมาชิก | [sequence-diagrams.md](sequence-diagrams.md) | [img/sequence-register.png](img/sequence-register.png) |
| Sequence: ตอบคำถาม (Command + Strategy) | [sequence-diagrams.md](sequence-diagrams.md) | [img/sequence-answer.png](img/sequence-answer.png) |
| Sequence: จบเกม (Observer) | [sequence-diagrams.md](sequence-diagrams.md) | [img/sequence-complete.png](img/sequence-complete.png) |
| Sequence: เริ่มแข่งในห้อง | [sequence-diagrams.md](sequence-diagrams.md) | [img/sequence-room-start.png](img/sequence-room-start.png) |
| Activity: เล่น 1 รอบ | [activity-diagram.md](activity-diagram.md) | [img/activity-play.png](img/activity-play.png) |
| Activity: แข่งในห้อง | [activity-diagram.md](activity-diagram.md) | [img/activity-room.png](img/activity-room.png) |
| State: QuizSession | [state-diagram.md](state-diagram.md) | [img/state-session.png](img/state-session.png) |
| State: Room | [state-diagram.md](state-diagram.md) | [img/state-room.png](img/state-room.png) |
| ER Diagram | [er-diagram.md](er-diagram.md) | [img/er-diagram-drawio.png](img/er-diagram-drawio.png) (draw.io) |
| Component Diagram | [component-deployment.md](component-deployment.md) | [img/component.png](img/component.png) |
| Deployment Diagram | [component-deployment.md](component-deployment.md) | [img/deployment.png](img/deployment.png) |

Data Dictionary อยู่ที่ [../data-dictionary.md](../data-dictionary.md)

## สร้างรูปใหม่จาก PlantUML

ต้องมี Java 17 และ `plantuml.jar` (ดาวน์โหลดจาก https://github.com/plantuml/plantuml/releases) ไม่ต้องติดตั้ง Graphviz เพราะไฟล์ใช้ layout `smetana`

```
cd doc/diagrams
java -Dfile.encoding=UTF-8 -jar plantuml.jar -charset UTF-8 -tpng -o ../img plantuml/*.puml
```

ไฟล์ `.puml` ตั้ง font เป็น Tahoma เพื่อให้ภาษาไทยแสดงถูกบน Windows ถ้า render บน Linux/Mac ให้เปลี่ยน `skinparam defaultFontName` เป็น font ที่มีภาษาไทย
