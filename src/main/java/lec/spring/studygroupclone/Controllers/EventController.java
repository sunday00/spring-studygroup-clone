package lec.spring.studygroupclone.Controllers;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Event;
import lec.spring.studygroupclone.Models.Study;
import lec.spring.studygroupclone.Services.EventService;
import lec.spring.studygroupclone.Services.StudyService;
import lec.spring.studygroupclone.dataMappers.EventSetting;
import lec.spring.studygroupclone.helpers.account.CurrentUser;
import lec.spring.studygroupclone.helpers.event.EventSettingValidator;
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

    private final EventService eventService;
    private final StudyService studyService;

    private final EventSettingValidator eventSettingValidator;

    public static final String EVENT_INDEX_VIEW = "/event/main";
    public static final String EVENT_CREATE_VIEW = "/event/create";
    public static final String EVENT_SHOW_VIEW = "/event/show";

    @InitBinder("eventSetting")
    public void init(WebDataBinder webDataBinder){
        webDataBinder.addValidators(eventSettingValidator);
    }

    @GetMapping("/events")
    public String index (@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyByPath(path);
        model.addAttribute(study);
        model.addAttribute(new EventSetting());
        model.addAttribute("events", eventService.getAllEventsByStudy(study));

        return EVENT_INDEX_VIEW;
    }

    @GetMapping(EVENT_CREATE_VIEW)
    public String create (@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyByPath(account, path, "manager");
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute(new EventSetting());
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


}
