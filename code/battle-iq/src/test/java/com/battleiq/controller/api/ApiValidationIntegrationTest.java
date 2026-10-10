package com.battleiq.controller.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.battleiq.dto.response.CategoryDTO;

/**
 * ทดสอบความทนทานของ API ต่อข้อมูลที่ยาวเกิน ผิดรูปแบบ หรือผิดชนิด
 * ทุกกรณีต้องได้ 4xx พร้อม error format มาตรฐาน ห้ามหลุดเป็น 500
 */
@SpringBootTest
@Transactional
class ApiValidationIntegrationTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private CategoryController categoryController;

    private MockMvc mockMvc;
    private String suffix;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        suffix = String.valueOf(System.nanoTime());
    }

    private String register(String username, String email, String password, String fullName) {
        return "{\"username\":\"" + username + "\",\"email\":\"" + email + "\",\"password\":\"" + password
                + "\",\"fullName\":\"" + fullName + "\"}";
    }

    private String question(Long categoryId, String optionA) {
        return "{\"categoryId\":" + categoryId + ",\"questionText\":\"q\",\"optionA\":\"" + optionA
                + "\",\"optionB\":\"b\",\"optionC\":\"c\",\"optionD\":\"d\",\"correctAnswer\":\"A\"}";
    }

    // TC-V01 ชื่อผู้ใช้ 500 ตัวอักษร
    @Test
    void usernameOf500CharsIsRejected() throws Exception {
        mockMvc.perform(post("/api/v1/users/register").contentType(MediaType.APPLICATION_JSON)
                        .content(register("a".repeat(500), "v" + suffix + "@t.com", "secret123", "x")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // TC-V02 ชื่อที่แสดง 500 ตัวอักษร (เดิมหลุดเป็น 500 เพราะเกินความยาวคอลัมน์)
    @Test
    void fullNameOf500CharsIsRejected() throws Exception {
        mockMvc.perform(post("/api/v1/users/register").contentType(MediaType.APPLICATION_JSON)
                        .content(register("v" + suffix, "v" + suffix + "@t.com", "secret123", "a".repeat(500))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Full name must be at most 100 characters"));
    }

    // TC-V03 รหัสผ่าน 73 ตัวอักษร (เกินขีดจำกัดของ BCrypt เดิมหลุดเป็น 500)
    @Test
    void passwordOver72CharsIsRejected() throws Exception {
        mockMvc.perform(post("/api/v1/users/register").contentType(MediaType.APPLICATION_JSON)
                        .content(register("v" + suffix, "v" + suffix + "@t.com", "a".repeat(73), "x")))
                .andExpect(status().isBadRequest());
    }

    // TC-V04 รหัสผ่าน 72 ตัวอักษรพอดี ต้องผ่าน
    @Test
    void passwordOf72CharsIsAccepted() throws Exception {
        mockMvc.perform(post("/api/v1/users/register").contentType(MediaType.APPLICATION_JSON)
                        .content(register("v" + suffix, "v" + suffix + "@t.com", "a".repeat(72), "x")))
                .andExpect(status().isCreated());
    }

    // TC-V05 ชื่อผู้ใช้มีช่องว่างหรืออักขระพิเศษ
    @Test
    void usernameWithSpacesOrSymbolsIsRejected() throws Exception {
        mockMvc.perform(post("/api/v1/users/register").contentType(MediaType.APPLICATION_JSON)
                        .content(register("bad name!", "v" + suffix + "@t.com", "secret123", "x")))
                .andExpect(status().isBadRequest());
    }

    // TC-V06 อีเมลยาวเกิน 100 ตัวอักษรแม้รูปแบบถูก
    @Test
    void emailOver100CharsIsRejected() throws Exception {
        mockMvc.perform(post("/api/v1/users/register").contentType(MediaType.APPLICATION_JSON)
                        .content(register("v" + suffix, "a".repeat(95) + "@t.com", "secret123", "x")))
                .andExpect(status().isBadRequest());
    }

    // TC-V07 ตัวเลือกคำตอบ 500 ตัวอักษร
    @Test
    void questionOptionOf500CharsIsRejected() throws Exception {
        CategoryDTO category = categoryController.getAllCategories().getBody().get(0);
        mockMvc.perform(post("/api/v1/questions").contentType(MediaType.APPLICATION_JSON)
                        .content(question(category.getId(), "a".repeat(500))))
                .andExpect(status().isBadRequest());
    }

    // TC-V08 sort ด้วยชื่อ field ที่ไม่มี (เดิมหลุดเป็น 500)
    @Test
    void unknownSortFieldReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/questions").param("sort", "hackerfield,desc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid sort field: hackerfield"));
    }

    // TC-V09 JSON พัง
    @Test
    void malformedJsonReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/users/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request format"));
    }

    // TC-V10 id ไม่ใช่ตัวเลข และตัวเลขเกินขอบเขต Long
    @Test
    void nonNumericOrOverflowIdReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/users/abc")).andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/v1/users/99999999999999999999")).andExpect(status().isBadRequest());
    }

    // TC-V11 Content-Type ไม่ใช่ JSON ต้องได้ 415 ใน error format เดียวกัน
    @Test
    void unsupportedContentTypeReturns415InStandardFormat() throws Exception {
        mockMvc.perform(post("/api/v1/users/register").contentType(MediaType.TEXT_PLAIN).content("hello"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value(415));
    }

    // TC-V12 timeTakenSeconds เกิน int
    @Test
    void hugeTimeTakenReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/quiz-sessions/1/answers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"questionId\":1,\"answer\":\"A\",\"timeTakenSeconds\":99999999999}"))
                .andExpect(status().isBadRequest());
    }
}
