package lec.spring.studygroupclone.Controllers;

import lec.spring.studygroupclone.Repositories.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AcountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("signup-form test")
    @Test
    void signForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))

                .andExpect(model().attributeExists("account"));
    }

    @DisplayName("register successfully")
    @Test
    void signProcessed() throws Exception {
        mockMvc.perform(
                    post("/sign-up")
                    .param("nickname", "darkKnight")
                    .param("email", "abc@example.com")
                    .param("password", "mysecurity123!")
                    .with(csrf())
                ).andExpect(status().is3xxRedirection())
//                .andExpect(view().name("account/sign-up"));
                .andExpect(view().name("redirect:/"));

        assertTrue(accountRepository.existsByEmail("abc@example.com"));
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }
}