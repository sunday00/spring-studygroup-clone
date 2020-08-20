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

    private static final String STUDY_SETTING_CREATE_VIEW = "/study/setting/create";
    private static final String STUDY_READ_SHOW_VIEW = "/study/read/show";

    private final StudyService studyService;
    private final ModelMapper modelMapper;
    private final StudySettingValidator studySettingValidator;

    @InitBinder("studySetting")
    public void studySettingInitBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(studySettingValidator);
    }

    @GetMapping(STUDY_SETTING_CREATE_VIEW)
    public String create(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new StudySetting());
        return STUDY_SETTING_CREATE_VIEW;
    }

    @PostMapping(STUDY_SETTING_CREATE_VIEW)
    public String insert(@CurrentUser Account account, @Valid StudySetting studySetting, Errors errors){
        if(errors.hasErrors()){
            return STUDY_SETTING_CREATE_VIEW;
        }

        Study newStudy = studyService.create(modelMapper.map(studySetting, Study.class), account);

        return "redirect:"+STUDY_READ_SHOW_VIEW+"/"+newStudy.getPath();
    }

    @GetMapping(STUDY_READ_SHOW_VIEW+"/{path}")
    public String read(@CurrentUser Account account, @PathVariable String path, Model model){
        model.addAttribute(account);
        model.addAttribute(studyService.getStudyByPath(path));
        return STUDY_READ_SHOW_VIEW;
    }
}
