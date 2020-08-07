package lec.spring.studygroupclone.Controllers;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Repositories.AccountRepository;
import lec.spring.studygroupclone.config.AppConfig;
import lec.spring.studygroupclone.helpers.account.WithFakeAccountForTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @AfterEach
    @Transactional
    void afterEach(){
        if( accountRepository.findByNickname("sunday") != null ) accountRepository.deleteByNickname("sunday");
    }

    @DisplayName("Do not allow not logged in person")
    @Test
    void showProfileUpdateFormWithoutLogIn() throws Exception {
        mockMvc.perform(get(ProfileController.PROFILE_EDIT_VIEW_NAME))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @DisplayName("profile edit successfully")
//    @WithUserDetails(value = "sunday", setupBefore = TestExecutionEvent.TEST_EXECUTION) // this is not work with junit5
    @WithFakeAccountForTest(email = "abc@example.com")
    @Test
    void updateProfileTest() throws Exception {
        mockMvc.perform(post(ProfileController.PROFILE_EDIT_VIEW_NAME)
            .param("description", "some description")
            .with(csrf())
        ).andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ProfileController.PROFILE_EDIT_VIEW_NAME))
        .andExpect(flash().attributeExists("info"));
    }

    @DisplayName("modify password test")
    @WithFakeAccountForTest(email = "abc@example.com")
    @Test
    void updatePassword() throws Exception {
        Account beforeAccount = accountRepository.findByEmail("abc@example.com");
        assertTrue(AppConfig.passwordEncoder().matches("security", beforeAccount.getPassword()));

        mockMvc.perform(post(ProfileController.PASSWD_EDIT_VIEW_NAME)
                .param("newPassword", "security2")
                .param("newPasswordConfirm", "security2")
                .with(csrf())
        ).andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ProfileController.PASSWD_EDIT_VIEW_NAME))
        .andExpect(flash().attributeExists("info"));

        Account afterAccount = accountRepository.findByEmail("abc@example.com");
        assertFalse(AppConfig.passwordEncoder().matches("security", afterAccount.getPassword()));
        assertTrue(AppConfig.passwordEncoder().matches("security2", afterAccount.getPassword()));
    }

}