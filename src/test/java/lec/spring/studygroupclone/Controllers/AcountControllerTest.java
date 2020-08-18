package lec.spring.studygroupclone.Controllers;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.EmailInfo;
import lec.spring.studygroupclone.Repositories.AccountRepository;
import lec.spring.studygroupclone.config.AppConfig;
import lec.spring.studygroupclone.helpers.MailSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
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

    @MockBean
    MailSender mailSender;

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

        Account account = accountRepository.findByEmail("abc@example.com");
        assertNotNull(account);
        assertNotNull(account.getEmailCheckToken());
//        assertTrue(accountRepository.existsByEmail("abc@example.com"));
        assertTrue( AppConfig.passwordEncoder().matches( "mysecurity123!", account.getPassword() ) );
        System.out.println(account.getPassword());
        System.out.println(account.getEmailCheckToken());
        then(mailSender).should().send(any(EmailInfo.class));
    }

    @DisplayName("Token forced error")
    @Test
    void check_email_token_error() throws Exception{
        mockMvc.perform(get("/check-email-token")
                    .param("token", "somethingWrongToken")
                    .param("email", "abc@abc.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(unauthenticated());
    }

    @DisplayName("Token check successfully")
    @Transactional
    @Test
    void check_email_token_success() throws Exception{
        Account account = Account.builder()
                .email("abc@abc.com")
                .nickname("joker")
                .password("secure1234*")
                .build();
        account = accountRepository.save(account);
        account.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                .param("token", account.getEmailCheckToken())
                .param("email", account.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("countMember"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(authenticated().withUsername(account.getEmail()));
    }

    @Transactional
    @DisplayName("login Test")
    @Test
    void login_and_logout_test() throws Exception{
        String email = "abc@test.com";
        String password = "secret123!";

        Account account = Account.builder()
                .email(email)
                .nickname("joker")
                .password(AppConfig.passwordEncoder().encode(password))
                .build();
        accountRepository.save(account);

        mockMvc.perform(post("/login")
                .param("email", email)
                .param("password", password)
                .with(csrf())
        ).andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"))
        .andExpect(authenticated().withUsername(account.getEmail()));

        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }

}