package lec.spring.studygroupclone.Controllers;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Enrollment;
import lec.spring.studygroupclone.Models.Event;
import lec.spring.studygroupclone.Services.EnrollmentService;
import lec.spring.studygroupclone.Services.EventService;
import lec.spring.studygroupclone.Services.StudyService;
import lec.spring.studygroupclone.helpers.account.CurrentUser;
import lec.spring.studygroupclone.helpers.study.StudyCheckAccount;
import lec.spring.studygroupclone.helpers.study.StudyJoinData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static lec.spring.studygroupclone.Controllers.EventController.EVENT_SHOW_VIEW;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}/event/{eventId}")
public class EnrollmentController {

    private final StudyService studyService;
    private final EventService eventService;
    private final EnrollmentService enrollmentService;

    @PostMapping("/enrollment/{id}/accept")
    public String acceptEnroll (@CurrentUser Account account, RedirectAttributes redirectAttributes,
                                @PathVariable String path, @PathVariable Long eventId, @PathVariable Long id) {
        studyService.getStudyByPath(account, path, StudyJoinData.MANAGER, StudyCheckAccount.MANAGER);
        Event event = eventService.getEventById(eventId);
        enrollmentService.acceptEnroll(id, event);

        return "redirect:/study/" + path + EVENT_SHOW_VIEW + "/" + eventId;
    }

    @PostMapping("/enrollment/{id}/reject")
    public String rejectEnroll (@CurrentUser Account account, RedirectAttributes redirectAttributes,
                                @PathVariable String path, @PathVariable Long eventId, @PathVariable Long id) {
        studyService.getStudyByPath(account, path, StudyJoinData.MANAGER, StudyCheckAccount.MANAGER);
        Event event = eventService.getEventById(eventId);
        enrollmentService.rejectEnroll(id, event);

        return "redirect:/study/" + path + EVENT_SHOW_VIEW + "/" + eventId;
    }

    @PostMapping("/enrollment/{id}/checkin")
    public String attend(@CurrentUser Account account, RedirectAttributes redirectAttributes,
                         @PathVariable String path, @PathVariable Long eventId, @PathVariable Long id){
        studyService.getStudyByPath(account, path, StudyJoinData.MANAGER, StudyCheckAccount.MANAGER);
        enrollmentService.attend(id);
        return "redirect:/study/" + path + EVENT_SHOW_VIEW + "/" + eventId;
    }

    @PostMapping("/enrollment/{id}/cancel-checkin")
    public String cancelAttend(@CurrentUser Account account, RedirectAttributes redirectAttributes,
                         @PathVariable String path, @PathVariable Long eventId, @PathVariable Long id){
        studyService.getStudyByPath(account, path, StudyJoinData.MANAGER, StudyCheckAccount.MANAGER);
        enrollmentService.cancelAttend(id);
        return "redirect:/study/" + path + EVENT_SHOW_VIEW + "/" + eventId;
    }
}
