INSERT INTO categories (name, description) VALUES ('วิทยาศาสตร์', 'ความรู้วิทยาศาสตร์ทั่วไป') ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name, description) VALUES ('ประวัติศาสตร์', 'ประวัติศาสตร์ไทยและโลก') ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name, description) VALUES ('เทคโนโลยี', 'คอมพิวเตอร์และการเขียนโปรแกรม') ON CONFLICT (name) DO NOTHING;

INSERT INTO questions (category_id, question_text, optiona, optionb, optionc, optiond, correct_answer, time_limit_seconds)
SELECT c.id, 'ดาวเคราะห์ดวงใดอยู่ใกล้ดวงอาทิตย์ที่สุด', 'ดาวศุกร์', 'ดาวพุธ', 'โลก', 'ดาวอังคาร', 'B', 15
FROM categories c WHERE c.name = 'วิทยาศาสตร์'
AND NOT EXISTS (SELECT 1 FROM questions q WHERE q.question_text = 'ดาวเคราะห์ดวงใดอยู่ใกล้ดวงอาทิตย์ที่สุด');

INSERT INTO questions (category_id, question_text, optiona, optionb, optionc, optiond, correct_answer, time_limit_seconds)
SELECT c.id, 'สูตรเคมีของน้ำคืออะไร', 'CO2', 'O2', 'H2O', 'NaCl', 'C', 15
FROM categories c WHERE c.name = 'วิทยาศาสตร์'
AND NOT EXISTS (SELECT 1 FROM questions q WHERE q.question_text = 'สูตรเคมีของน้ำคืออะไร');

INSERT INTO questions (category_id, question_text, optiona, optionb, optionc, optiond, correct_answer, time_limit_seconds)
SELECT c.id, 'อวัยวะใดทำหน้าที่สูบฉีดเลือดไปทั่วร่างกาย', 'ปอด', 'ตับ', 'ไต', 'หัวใจ', 'D', 15
FROM categories c WHERE c.name = 'วิทยาศาสตร์'
AND NOT EXISTS (SELECT 1 FROM questions q WHERE q.question_text = 'อวัยวะใดทำหน้าที่สูบฉีดเลือดไปทั่วร่างกาย');

INSERT INTO questions (category_id, question_text, optiona, optionb, optionc, optiond, correct_answer, time_limit_seconds)
SELECT c.id, 'พืชใช้แก๊สชนิดใดในการสังเคราะห์ด้วยแสง', 'คาร์บอนไดออกไซด์', 'ออกซิเจน', 'ไนโตรเจน', 'ไฮโดรเจน', 'A', 15
FROM categories c WHERE c.name = 'วิทยาศาสตร์'
AND NOT EXISTS (SELECT 1 FROM questions q WHERE q.question_text = 'พืชใช้แก๊สชนิดใดในการสังเคราะห์ด้วยแสง');

INSERT INTO questions (category_id, question_text, optiona, optionb, optionc, optiond, correct_answer, time_limit_seconds)
SELECT c.id, 'แสงเดินทางในสุญญากาศได้ประมาณกี่กิโลเมตรต่อวินาที', '3,000', '30,000', '300,000', '3,000,000', 'C', 20
FROM categories c WHERE c.name = 'วิทยาศาสตร์'
AND NOT EXISTS (SELECT 1 FROM questions q WHERE q.question_text = 'แสงเดินทางในสุญญากาศได้ประมาณกี่กิโลเมตรต่อวินาที');

INSERT INTO questions (category_id, question_text, optiona, optionb, optionc, optiond, correct_answer, time_limit_seconds)
SELECT c.id, 'กรุงศรีอยุธยาเสียกรุงครั้งที่ 2 ใน พ.ศ. ใด', '2112', '2310', '2325', '2475', 'B', 15
FROM categories c WHERE c.name = 'ประวัติศาสตร์'
AND NOT EXISTS (SELECT 1 FROM questions q WHERE q.question_text = 'กรุงศรีอยุธยาเสียกรุงครั้งที่ 2 ใน พ.ศ. ใด');

INSERT INTO questions (category_id, question_text, optiona, optionb, optionc, optiond, correct_answer, time_limit_seconds)
SELECT c.id, 'ใครเป็นผู้ประดิษฐ์ลายสือไทย', 'พ่อขุนรามคำแหงมหาราช', 'พ่อขุนศรีอินทราทิตย์', 'สมเด็จพระนารายณ์มหาราช', 'พระเจ้าอู่ทอง', 'A', 15
FROM categories c WHERE c.name = 'ประวัติศาสตร์'
AND NOT EXISTS (SELECT 1 FROM questions q WHERE q.question_text = 'ใครเป็นผู้ประดิษฐ์ลายสือไทย');

INSERT INTO questions (category_id, question_text, optiona, optionb, optionc, optiond, correct_answer, time_limit_seconds)
SELECT c.id, 'สงครามโลกครั้งที่ 2 สิ้นสุดใน ค.ศ. ใด', '1918', '1939', '1945', '1950', 'C', 15
FROM categories c WHERE c.name = 'ประวัติศาสตร์'
AND NOT EXISTS (SELECT 1 FROM questions q WHERE q.question_text = 'สงครามโลกครั้งที่ 2 สิ้นสุดใน ค.ศ. ใด');

INSERT INTO questions (category_id, question_text, optiona, optionb, optionc, optiond, correct_answer, time_limit_seconds)
SELECT c.id, 'กรุงเทพมหานครได้รับการสถาปนาเป็นราชธานีใน พ.ศ. ใด', '2310', '2325', '2411', '2475', 'B', 15
FROM categories c WHERE c.name = 'ประวัติศาสตร์'
AND NOT EXISTS (SELECT 1 FROM questions q WHERE q.question_text = 'กรุงเทพมหานครได้รับการสถาปนาเป็นราชธานีใน พ.ศ. ใด');

INSERT INTO questions (category_id, question_text, optiona, optionb, optionc, optiond, correct_answer, time_limit_seconds)
SELECT c.id, 'ประเทศไทยเปลี่ยนแปลงการปกครองใน พ.ศ. ใด', '2325', '2411', '2453', '2475', 'D', 15
FROM categories c WHERE c.name = 'ประวัติศาสตร์'
AND NOT EXISTS (SELECT 1 FROM questions q WHERE q.question_text = 'ประเทศไทยเปลี่ยนแปลงการปกครองใน พ.ศ. ใด');

INSERT INTO questions (category_id, question_text, optiona, optionb, optionc, optiond, correct_answer, time_limit_seconds)
SELECT c.id, 'HTML ย่อมาจากอะไร', 'HyperText Markup Language', 'HighText Machine Language', 'Hyperlink Text Main Language', 'Home Tool Markup Language', 'A', 15
FROM categories c WHERE c.name = 'เทคโนโลยี'
AND NOT EXISTS (SELECT 1 FROM questions q WHERE q.question_text = 'HTML ย่อมาจากอะไร');

INSERT INTO questions (category_id, question_text, optiona, optionb, optionc, optiond, correct_answer, time_limit_seconds)
SELECT c.id, 'Spring Boot เขียนด้วยภาษาใดเป็นหลัก', 'Python', 'Java', 'PHP', 'Ruby', 'B', 15
FROM categories c WHERE c.name = 'เทคโนโลยี'
AND NOT EXISTS (SELECT 1 FROM questions q WHERE q.question_text = 'Spring Boot เขียนด้วยภาษาใดเป็นหลัก');

INSERT INTO questions (category_id, question_text, optiona, optionb, optionc, optiond, correct_answer, time_limit_seconds)
SELECT c.id, '1 ไบต์มีกี่บิต', '2', '4', '8', '16', 'C', 10
FROM categories c WHERE c.name = 'เทคโนโลยี'
AND NOT EXISTS (SELECT 1 FROM questions q WHERE q.question_text = '1 ไบต์มีกี่บิต');

INSERT INTO questions (category_id, question_text, optiona, optionb, optionc, optiond, correct_answer, time_limit_seconds)
SELECT c.id, 'HTTP status code ใดหมายถึง Not Found', '200', '201', '400', '404', 'D', 10
FROM categories c WHERE c.name = 'เทคโนโลยี'
AND NOT EXISTS (SELECT 1 FROM questions q WHERE q.question_text = 'HTTP status code ใดหมายถึง Not Found');

INSERT INTO questions (category_id, question_text, optiona, optionb, optionc, optiond, correct_answer, time_limit_seconds)
SELECT c.id, 'คำสั่ง Git ใดใช้ส่ง commit ขึ้น remote', 'git push', 'git pull', 'git clone', 'git status', 'A', 10
FROM categories c WHERE c.name = 'เทคโนโลยี'
AND NOT EXISTS (SELECT 1 FROM questions q WHERE q.question_text = 'คำสั่ง Git ใดใช้ส่ง commit ขึ้น remote');
