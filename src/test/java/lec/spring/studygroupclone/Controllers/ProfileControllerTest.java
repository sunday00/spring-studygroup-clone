package lec.spring.studygroupclone.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Location;
import lec.spring.studygroupclone.Models.Tag;
import lec.spring.studygroupclone.Repositories.AccountRepository;
import lec.spring.studygroupclone.Repositories.LocationRepository;
import lec.spring.studygroupclone.Repositories.TagRepository;
import lec.spring.studygroupclone.config.AppConfig;
import lec.spring.studygroupclone.helpers.account.WithFakeAccountForTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
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

    @Autowired
    TagRepository tagRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    ObjectMapper objectMapper;

    @AfterEach
    @Transactional
    void afterEach(){
        if( accountRepository.findByNickname("sunday") != null ) accountRepository.deleteByNickname("sunday");
        tagRepository.deleteAll();
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

    @DisplayName("show tag form")
    @WithFakeAccountForTest(email = "abc@example.com")
    @Test
    void showTagForm () throws Exception {
        Account account = accountRepository.findByEmail("abc@example.com");

        mockMvc.perform(get(ProfileController.TAG_EDIT_VIEW_NAME))
                .andExpect(view().name(ProfileController.TAG_EDIT_VIEW_NAME))
                .andExpect(status().isOk())
                .andExpect(model().attribute("allTags", "[\"php\",\"java\"]"))
                .andExpect(model().attribute("account", account))
        ;
    }

    @DisplayName("add tag")
    @WithFakeAccountForTest(email = "abc@example.com")
    @Transactional
    @Test
    void addTagTest () throws Exception {
        Tag tag = new Tag();
        tag.setTitle("python");

        mockMvc.perform(post(ProfileController.TAG_EDIT_VIEW_NAME+"/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tag))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag resultTag = tagRepository.findByTitle(tag.getTitle());
        assertNotNull(resultTag);
        assertTrue(accountRepository.findByEmail("abc@example.com").getTags().contains(resultTag));
        //  mockMvc를 벗어나면 repository를 직접적으로 불러오는 부분까지만 트랜잭셔널(퍼시스타) 상태이고,
        // 나머지는 곧바로 디테치드가 되므로 트랜잭셔널 어노테이션이 필요.
    }

    @DisplayName("remove tag")
    @WithFakeAccountForTest(email = "abc@example.com")
    @Transactional
    @Test
    void removeTagTest () throws Exception {
        Tag tag = tagRepository.findByTitle("php");
        assertTrue(accountRepository.findByEmail("abc@example.com").getTags().contains(tag));

        mockMvc.perform(delete(ProfileController.TAG_EDIT_VIEW_NAME+"/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tag))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag resultTag = tagRepository.findByTitle(tag.getTitle());
        assertNotNull(resultTag);
        assertFalse(accountRepository.findByEmail("abc@example.com").getTags().contains(resultTag));
    }

    @DisplayName("show location form")
    @WithFakeAccountForTest(email = "abc@example.com")
    @Test
    void showLocationForm () throws Exception {
        Account account = accountRepository.findByEmail("abc@example.com");
        Location location = new Location();
        location.setCity("Example");
        location.setLocalName("예시");
        location.setProvince("SouthExample");
        locationRepository.save(location);

        mockMvc.perform(get(ProfileController.LOCATION_EDIT_VIEW_NAME))
                .andExpect(view().name(ProfileController.LOCATION_EDIT_VIEW_NAME))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("allLocations"))
                .andExpect(model().attribute("account", account))
        ;

        locationRepository.deleteAll();
    }

    @DisplayName("add location")
    @WithFakeAccountForTest(email = "abc@example.com")
    @Transactional
    @Test
    void addLocationTest () throws Exception {
        Location location = new Location();
        location.setCity("Example");
        location.setLocalName("예시");
        location.setProvince("SouthExample");
        locationRepository.save(location);

        mockMvc.perform(post(ProfileController.LOCATION_EDIT_VIEW_NAME+"/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(location))
                .with(csrf()))
                .andExpect(status().isOk());

        Location resultLocation = locationRepository.findByCity(location.getCity());
        assertNotNull(resultLocation);
        assertTrue(accountRepository.findByEmail("abc@example.com").getLocations().contains(location));
        locationRepository.deleteAll();
    }

    @DisplayName("remove location")
    @WithFakeAccountForTest(email = "abc@example.com")
    @Transactional
    @Test
    void removeLocationTest () throws Exception {
        Location location = new Location();
        location.setCity("Example");
        location.setLocalName("예시");
        location.setProvince("SouthExample");
        locationRepository.save(location);

        mockMvc.perform(delete(ProfileController.TAG_EDIT_VIEW_NAME+"/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(location))
                .with(csrf()))
                .andExpect(status().isOk());

        Location resultLocation = locationRepository.findByCity(location.getCity());
        assertNotNull(resultLocation);
        assertFalse(accountRepository.findByEmail("abc@example.com").getLocations().contains(resultLocation));
    }

}