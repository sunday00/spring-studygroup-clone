package lec.spring.studygroupclone.Controllers;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Enrollment;
import lec.spring.studygroupclone.Models.Event;
import lec.spring.studygroupclone.Models.Study;
import lec.spring.studygroupclone.Repositories.AccountRepository;
import lec.spring.studygroupclone.Repositories.EnrollmentRepository;
import lec.spring.studygroupclone.Repositories.EventRepository;
import lec.spring.studygroupclone.Repositories.StudyRepository;
import lec.spring.studygroupclone.config.AppConfig;
import lec.spring.studygroupclone.helpers.account.WithFakeAccountForTest;
import lec.spring.studygroupclone.helpers.event.EventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @AfterEach
    @Transactional
    void afterEach(){
        enrollmentRepository.deleteAll();
        eventRepository.deleteAll();
        studyRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @DisplayName("enrollment and accepted success")
    @Test
    @WithFakeAccountForTest(email = "sunday@example.com")
    void enrollment() throws Exception {
        Account member1 = this.createAccount("monday", "monday@example.com");
        Account member2 = this.createAccount("today", "today@example.com");
        Account member3 = this.createAccount("samday", "samday@example.com");
        Account member4 = this.createAccount("snake", "snake@example.com");
        Account member5 = this.createAccount("batman", "batman@example.com");

        Study study = this.createStudy(member1, member2, member3, member4, member5);

        Event event = this.createEvent(study, member1, member2);

        assertEquals(event.getEnrollments().size(), 2);

        mockMvc.perform(post("/study/"+study.getPath()+"/event/apply/"+event.getId())
                .with(csrf()))
                .andExpect(status().is3xxRedirection());

        List<Enrollment> enrollments = enrollmentRepository.findAllByEvent(event);
        assertEquals(enrollments.size(), 3);

        Enrollment enrollment = enrollments.stream().max(Comparator.comparing(Enrollment::getId)).orElseThrow(NullPointerException::new);
        assertTrue(enrollment.isAccepted());
    }

    private Account createAccount(String nick, String email){
        Account account = new Account();
        account.setNickname(nick);
        account.setEmail(email);
        account.setPassword(AppConfig.passwordEncoder().encode("secret"));

        return accountRepository.save(account);
    }

    private Study createStudy(Account member1, Account member2, Account member3, Account member4, Account member5){
        Study study = new Study();
        study.setPath("php");
        study.setTitle("php");
        study.setIntroduce("hello");
        study.setFullDescription("hello guys");

        Account currentUser = accountRepository.findByEmail("sunday@example.com");

        study.addManager(currentUser);
        study.addMember(member1);
        study.addMember(member2);
        study.addMember(member3);
        study.addMember(member4);
        study.addMember(member5);

        return studyRepository.save(study);
    }

    private Event createEvent(Study study, Account account1, Account account2){
        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();
        LocalDateTime now = LocalDateTime.now();
        List<Enrollment> enrollments = new ArrayList<>();

        Event event = new Event();
        event.setTitle("laravel");
        event.setEventType(EventType.FCFS);
        event.setEndEnrollmentAt(LocalDateTime.of(nowDate.plusDays(3), nowTime));
        event.setStartAt(LocalDateTime.of(nowDate.plusDays(4), nowTime));
        event.setEndAt(LocalDateTime.of(nowDate.plusDays(4), nowTime.plusHours(2)));
        event.setDescription("hohoho");
        event.setStudy(study);
        event.setLimitEnrollment(3);
        event.setCreatedAt(now);

        Enrollment enrollment1 = new Enrollment();
        enrollment1.setEvent(event);
        enrollment1.setAccepted(true);
        enrollment1.setEvent(event);
        enrollment1.setAccount(account1);
        enrollments.add(enrollment1);

        Enrollment enrollment2 = new Enrollment();
        enrollment2.setEvent(event);
        enrollment2.setAccepted(true);
        enrollment2.setEvent(event);
        enrollment2.setAccount(account2);
        enrollments.add(enrollment2);

        event.setEnrollments(enrollments);

        Event result = eventRepository.save(event);

        enrollmentRepository.save(enrollment1);
        enrollmentRepository.save(enrollment2);

        return result;
    }
}