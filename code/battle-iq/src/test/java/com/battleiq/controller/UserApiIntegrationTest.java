package com.battleiq.controller;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * ทดสอบระดับ API ของส่วนผู้ใช้และโปรไฟล์ ใช้ฐานข้อมูลจริง แต่ rollback ทุกเทส
 */
@SpringBootTest
@Transactional
class UserApiIntegrationTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private String suffix;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        suffix = String.valueOf(System.nanoTime());
    }

    private String registerBody(String username, String email, String password) {
        return "{\"username\":\"" + username + "\",\"email\":\"" + email
                + "\",\"password\":\"" + password + "\",\"fullName\":\"Tester\"}";
    }

    private long registerAndGetId() throws Exception {
        String body = mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody("it" + suffix, "it" + suffix + "@test.com", "secret123")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(body);
        return json.get("id").asLong();
    }

    // TC-A01 สมัครสำเร็จ ได้ 201 มีโปรไฟล์เริ่มต้น และไม่ส่งรหัสผ่านกลับ
    @Test
    void registerReturnsCreatedWithDefaultProfile() throws Exception {
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody("it" + suffix, "it" + suffix + "@test.com", "secret123")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("it" + suffix))
                .andExpect(jsonPath("$.fullName").value("Tester"))
                .andExpect(jsonPath("$.level").value(1))
                .andExpect(jsonPath("$.totalScore").value(0))
                .andExpect(jsonPath("$.currentStreak").value(0))
                .andExpect(jsonPath("$", not(hasKey("password"))));
    }

    // TC-A02 รหัสผ่าน 5 ตัว (ต่ำกว่าขอบเขต 6) ต้องได้ 400
    @Test
    void registerRejectsPasswordBelowMinimumLength() throws Exception {
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody("it" + suffix, "it" + suffix + "@test.com", "12345")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // TC-A03 รหัสผ่าน 6 ตัวพอดี (ขอบเขตล่าง) ต้องผ่าน
    @Test
    void registerAcceptsPasswordAtMinimumLength() throws Exception {
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody("it" + suffix, "it" + suffix + "@test.com", "123456")))
                .andExpect(status().isCreated());
    }

    // TC-A04 อีเมลผิดรูปแบบ ต้องได้ 400
    @Test
    void registerRejectsInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody("it" + suffix, "not-an-email", "secret123")))
                .andExpect(status().isBadRequest());
    }

    // TC-A05 สมัครซ้ำ ต้องได้ 409
    @Test
    void registerTwiceReturnsConflict() throws Exception {
        registerAndGetId();

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody("it" + suffix, "other" + suffix + "@test.com", "secret123")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    // TC-A06 login ถูก ได้ 200 / รหัสผิด ได้ 401
    @Test
    void loginReturnsOkOrUnauthorized() throws Exception {
        long id = registerAndGetId();

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"it" + suffix + "\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"it" + suffix + "\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    // TC-A07 ดูและแก้โปรไฟล์
    @Test
    void getAndUpdateProfile() throws Exception {
        long id = registerAndGetId();

        mockMvc.perform(get("/api/v1/profiles/user/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Tester"));

        mockMvc.perform(put("/api/v1/profiles/user/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"New Name\",\"avatarUrl\":\"https://example.com/a.png\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("New Name"))
                .andExpect(jsonPath("$.avatarUrl").value("https://example.com/a.png"));
    }

    // TC-A08 โปรไฟล์ของ user ที่ไม่มี ต้องได้ 404 ทั้ง GET และ PUT
    @Test
    void profileOfUnknownUserReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/profiles/user/999999999"))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/v1/profiles/user/999999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"x\"}"))
                .andExpect(status().isNotFound());
    }

    // TC-A09 ชื่อยาวเกิน 100 ตัวอักษร (เกินขอบเขต) ต้องได้ 400
    @Test
    void updateProfileRejectsTooLongName() throws Exception {
        long id = registerAndGetId();
        String longName = "a".repeat(101);

        mockMvc.perform(put("/api/v1/profiles/user/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"" + longName + "\"}"))
                .andExpect(status().isBadRequest());
    }
}
