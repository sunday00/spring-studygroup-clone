package lec.spring.studygroupclone.Controllers;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Study;
import lec.spring.studygroupclone.Services.StudyService;
import lec.spring.studygroupclone.dataMappers.StudySetting;
import lec.spring.studygroupclone.helpers.account.CurrentUser;
import lec.spring.studygroupclone.helpers.study.StudySettingValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class StudyController {

    public static final String STUDY_READ_SHOW_VIEW = "/study/read/show";
    public static final String STUDY_READ_MEMBERS_VIEW = "/study/read/members";

    private final StudyService studyService;
    private final ModelMapper modelMapper;
    private final StudySettingValidator studySettingValidator;

    @InitBinder("studySetting")
    public void studySettingInitBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(studySettingValidator);
    }

    @GetMapping(STUDY_READ_SHOW_VIEW+"/{path}")
    public String read(@CurrentUser Account account, @PathVariable String path, Model model){
        model.addAttribute(account);
        model.addAttribute(studyService.getStudyByPath(path));
        return STUDY_READ_SHOW_VIEW;
    }

    @GetMapping(STUDY_READ_MEMBERS_VIEW+"/{path}")
    public String members(@CurrentUser Account account, @PathVariable String path, Model model){
        model.addAttribute(account);
        model.addAttribute(studyService.getStudyByPath(path));
        return STUDY_READ_MEMBERS_VIEW;
    }
}
