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
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class StudyController {

    private static final String STUDY_CREATE_VIEW = "/study/create";
    private final StudyService studyService;
    private final ModelMapper modelMapper;
    private final StudySettingValidator studySettingValidator;

    @InitBinder("studySetting")
    public void studySettingInitBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(studySettingValidator);
    }

    @GetMapping(STUDY_CREATE_VIEW)
    public String create(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new StudySetting());
        return STUDY_CREATE_VIEW;
    }

    @PostMapping(STUDY_CREATE_VIEW)
    public String insert(@CurrentUser Account account, @Valid StudySetting studySetting, Errors errors){
        if(errors.hasErrors()){
            return STUDY_CREATE_VIEW;
        }

        Study newStudy = studyService.create(modelMapper.map(studySetting, Study.class), account);

        return "redirect:/study/read/"+newStudy.getPath();
    }
}
