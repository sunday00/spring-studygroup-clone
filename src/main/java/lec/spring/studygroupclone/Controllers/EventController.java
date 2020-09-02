package lec.spring.studygroupclone.Controllers;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Event;
import lec.spring.studygroupclone.Models.Study;
import lec.spring.studygroupclone.Services.EnrollmentService;
import lec.spring.studygroupclone.Services.EventService;
import lec.spring.studygroupclone.Services.StudyService;
import lec.spring.studygroupclone.dataMappers.EventSetting;
import lec.spring.studygroupclone.helpers.ConsoleLog;
import lec.spring.studygroupclone.helpers.account.CurrentUser;
import lec.spring.studygroupclone.helpers.event.EventSettingValidator;
import lec.spring.studygroupclone.helpers.study.StudyCheckAccount;
import lec.spring.studygroupclone.helpers.study.StudyJoinData;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}")
public class EventController {

    private final ModelMapper modelMapper;

    private final EnrollmentService enrollmentService;
    private final EventService eventService;
    private final StudyService studyService;

    private final EventSettingValidator eventSettingValidator;

    public static final String EVENT_INDEX_VIEW = "/event/main";
    public static final String EVENT_CREATE_VIEW = "/event/create";
    public static final String EVENT_EDIT_VIEW = "/event/edit";
    public static final String EVENT_SHOW_VIEW = "/event/show";
    public static final String EVENT_DELETE_VIEW = "/event/delete";
    public static final String EVENT_APPLY_VIEW = "/event/apply";
    public static final String EVENT_LEAVE_VIEW = "/event/leave";

    @InitBinder("eventSetting")
    public void init(WebDataBinder webDataBinder){
        webDataBinder.addValidators(eventSettingValidator);
    }

    @GetMapping("/events")
    public String index (@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyByPath(path);
        model.addAttribute(study);
        model.addAttribute("events", eventService.getAllEventsByStudy(study));
        return EVENT_INDEX_VIEW;
    }

    @GetMapping("/events/coming")
    public String indexComing (@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyByPath(path);
        model.addAttribute(study);
        model.addAttribute("events", eventService.getComingEventsByStudy(study));
        return "/event/coming";
    }

    @GetMapping("/events/past")
    public String indexPast (@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyByPath(path);
        model.addAttribute(study);
        model.addAttribute("events", eventService.getPastEventsByStudy(study));
        return "/event/past";
    }

    @GetMapping(EVENT_CREATE_VIEW)
    public String create (@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyByPath(account, path, "manager");
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute(new EventSetting());
        model.addAttribute("mode", "create");
        return EVENT_CREATE_VIEW;
    }

    @PostMapping(EVENT_CREATE_VIEW)
    public String store (@CurrentUser Account account, @PathVariable String path, Model model,
                         @Valid EventSetting eventSetting, Errors errors, RedirectAttributes redirectAttributes) {
        Study study = studyService.getStudyByPath(account, path, "manager");
        if(errors.hasErrors()){
            model.addAttribute(account);
            model.addAttribute(study);
            return EVENT_CREATE_VIEW;
        }

        Event event = eventService.create(modelMapper.map(eventSetting, Event.class), study, account);

        return "redirect:/study/" + path + EVENT_SHOW_VIEW + "/" + event.getId();
    }

    @GetMapping(EVENT_EDIT_VIEW + "/{eventId}")
    public String edit (@CurrentUser Account account, @PathVariable String path, @PathVariable Long eventId, Model model) {
        Study study = studyService.getStudyByPath(account, path, "manager");
        Event event = eventService.getEventById(eventId);
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute(event);
        model.addAttribute(modelMapper.map(event, EventSetting.class));
        model.addAttribute("mode", "edit");
        return EVENT_CREATE_VIEW;
    }

    @PostMapping(EVENT_EDIT_VIEW + "/{eventId}")
    public String update (@CurrentUser Account account, @PathVariable String path, @PathVariable Long eventId, Model model,
                         @Valid EventSetting eventSetting, Errors errors, RedirectAttributes redirectAttributes) {
        Study study = studyService.getStudyByPath(account, path, "manager");
        Event event = eventService.getEventById(eventId);
        if(errors.hasErrors()){
            model.addAttribute(study);
            model.addAttribute(account);
            model.addAttribute(event);
            model.addAttribute("mode", "edit");
            return EVENT_CREATE_VIEW;
        }

        Event modifiedEvent = eventService.update(eventSetting, event);

        return "redirect:/study/" + path + EVENT_SHOW_VIEW + "/" + modifiedEvent.getId();
    }

    @GetMapping(EVENT_SHOW_VIEW + "/{eventId}")
    public String read(@CurrentUser Account account, @PathVariable String path, @PathVariable Long eventId, Model model){
        Study study = studyService.getStudyByPath(path);
        Event event = eventService.getEventById(eventId);
        HashMap<String, Boolean> canAccountEnroll = eventService.getCanAccountEnroll(account, event);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(event);
        model.addAttribute("canAccountEnroll", canAccountEnroll);
        return EVENT_SHOW_VIEW;
    }

//    @PostMapping(EVENT_DELETE_VIEW + "/{eventId}")
    @DeleteMapping(EVENT_DELETE_VIEW + "/{eventId}")
    public String delete (@CurrentUser Account account, @PathVariable String path, @PathVariable Long eventId,
                          RedirectAttributes redirectAttributes) {
        studyService.getStudyByPath(account, path, "manager");
        Event event = eventService.getEventById(eventId);

        eventService.delete(event);

        return "redirect:/study/" + path + "/events";
    }

    //TODO
    @PostMapping(EVENT_APPLY_VIEW + "/{eventId}")
    public String enroll (@CurrentUser Account account, @PathVariable String path, @PathVariable Long eventId,
                          Model model, RedirectAttributes redirectAttributes){
        Study study = studyService.getStudyByPath(account, path, StudyJoinData.ONLY, StudyCheckAccount.MEMBER);
        Event event = eventService.getEventById(eventId);
        enrollmentService.apply(event, account);

        return "redirect:/study/" + path + EVENT_SHOW_VIEW + "/" + event.getId();
    }

    //TODO
    @PostMapping(EVENT_LEAVE_VIEW + "/{eventId}")
    public String leave (@CurrentUser Account account, @PathVariable String path, @PathVariable Long eventId,
                         Model model, RedirectAttributes redirectAttributes){
        Study study = studyService.getStudyByPath(account, path, StudyJoinData.ONLY, StudyCheckAccount.MEMBER);
        Event event = eventService.getEventById(eventId);
        enrollmentService.leave(event, account);

        return "redirect:/study/" + path + EVENT_SHOW_VIEW + "/" + event.getId();
    }

}
