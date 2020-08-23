package lec.spring.studygroupclone.Controllers;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Study;
import lec.spring.studygroupclone.Services.StudyService;
import lec.spring.studygroupclone.dataMappers.StudySetting;
import lec.spring.studygroupclone.helpers.account.CurrentUser;
import lec.spring.studygroupclone.helpers.study.StudySettingValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class StudySettingController {

    public static final String STUDY_SETTING_CREATE_VIEW = "/study/setting/create";
    public static final String STUDY_SETTING_UPDATE_VIEW = "/study/setting/edit";
    public static final String STUDY_SETTING_BANNER_VIEW = "/study/setting/banner";

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
    public String insert(@CurrentUser Account account, @Valid StudySetting studySetting, Errors errors, Model model){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return STUDY_SETTING_CREATE_VIEW;
        }

        Study newStudy = studyService.create(modelMapper.map(studySetting, Study.class), account);

        return "redirect:"+StudyController.STUDY_READ_SHOW_VIEW+"/"+newStudy.getPath();
    }

    @GetMapping(STUDY_SETTING_UPDATE_VIEW + "/{path}")
    public String edit(@CurrentUser Account account, @PathVariable String path, Model model){
        Study study = studyService.getStudyByPath(path);
        if( !study.isManager(account) ) {
            throw new AccessDeniedException("Not enough permission");
        };
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(modelMapper.map(study, StudySetting.class));
        return STUDY_SETTING_UPDATE_VIEW;
    }

    @PostMapping(STUDY_SETTING_UPDATE_VIEW + "/{path}")
    public String update (@CurrentUser Account account, @PathVariable String path,
                          @Valid StudySetting studySetting, Errors errors,
                          Model model, RedirectAttributes redirectAttributes) {

        Study study = studyService.getStudyByPath(path);

        if( errors.hasErrors() ){
            model.addAttribute(account);
            model.addAttribute(study);
            return STUDY_SETTING_UPDATE_VIEW;
        }

        study.setIntroduce(studySetting.getIntroduce());
        study.setFullDescription(studySetting.getFullDescription());
        studyService.save(study, studySetting);

        redirectAttributes.addFlashAttribute("info", "modify success");

        return "redirect:" + STUDY_SETTING_UPDATE_VIEW + "/" + path;
    }

    @GetMapping(STUDY_SETTING_BANNER_VIEW + "/{path}")
    public String banner (@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyByPath(path);
        if( !study.isManager(account) ) {
            throw new AccessDeniedException("Not enough permission");
        };
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(modelMapper.map(study, StudySetting.class));
        return STUDY_SETTING_BANNER_VIEW;
    }

    @PostMapping(STUDY_SETTING_BANNER_VIEW+"-toggle/{path}")
    public String bannerToggle(@CurrentUser Account account, @PathVariable String path, String enable,
                               Model model, RedirectAttributes redirectAttributes){

        Study study = studyService.getStudyByPath(path);

        studyService.setStudyToggleBannerUsing(study, enable);

        return "redirect:" + STUDY_SETTING_BANNER_VIEW + "/" + path;
    }

    @PostMapping(STUDY_SETTING_BANNER_VIEW+"-image/{path}")
    public String bannerImage(@CurrentUser Account account, @PathVariable String path, String image,
                               Model model, RedirectAttributes redirectAttributes) throws IOException {

        Study study = studyService.getStudyByPath(path);

        studyService.setStudyBannerImage(study, image);

        return "redirect:" + STUDY_SETTING_BANNER_VIEW + "/" + path;
    }

}
