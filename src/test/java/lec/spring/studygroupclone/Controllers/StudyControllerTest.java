package lec.spring.studygroupclone.Controllers;

import lec.spring.studygroupclone.Models.Study;
import lec.spring.studygroupclone.Repositories.AccountRepository;
import lec.spring.studygroupclone.Repositories.StudyRepository;
import lec.spring.studygroupclone.Repositories.TagRepository;
import lec.spring.studygroupclone.dataMappers.StudySetting;
import lec.spring.studygroupclone.helpers.account.WithFakeAccountForTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudyControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TagRepository tagRepository;

    @AfterEach
    @Transactional
    void afterEach(){
        if( accountRepository.findByNickname("sunday") != null ){
            accountRepository.deleteByNickname("sunday");
        }
        tagRepository.deleteAll();
    }

    Study createNewStudy() throws Exception {
        mockMvc.perform(post(StudySettingController.STUDY_SETTING_CREATE_VIEW)
                .param("path", "example")
                .param("title", "Example study")
                .param("introduce", "hello")
                .param("fullDescription", "this is test study")
                .with(csrf())
        );

        return studyRepository.findByPath("example");
    }

    @DisplayName("create study")
    @WithFakeAccountForTest(email = "abc@example.com")
    @Test
    void createStudy () throws Exception {
        mockMvc.perform(post(StudySettingController.STUDY_SETTING_CREATE_VIEW)
                .param("path", "example")
                .param("title", "Example study")
                .param("introduce", "hello")
                .param("fullDescription", "this is test study")
                .with(csrf())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(StudyController.STUDY_READ_SHOW_VIEW+"/example"));

        Study study = studyRepository.findByPath("example");
        assertNotNull(study);

        studyRepository.deleteAll();
    }

    @DisplayName("edit study basic info")
    @WithFakeAccountForTest(email = "abc@example.com")
    @Test
    void editStudy() throws Exception {
        this.createNewStudy();

        mockMvc.perform(post(StudySettingController.STUDY_SETTING_UPDATE_VIEW + "/example")
                .param("title", "Example study2")
                .param("introduce", "hello2")
                .param("fullDescription", "this is test study2")
                .with(csrf())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(StudySettingController.STUDY_SETTING_UPDATE_VIEW+"/example"))
                .andExpect(flash().attribute("info", "modify success"));

        Study study = studyRepository.findByPath("example");
        assertEquals(study.getTitle(), "Example study2");
        assertEquals(study.getIntroduce(), "hello2");
        assertEquals(study.getFullDescription(), "this is test study2");

        studyRepository.deleteAll();
    }

}