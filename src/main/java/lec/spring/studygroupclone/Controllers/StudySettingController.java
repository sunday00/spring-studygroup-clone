package lec.spring.studygroupclone.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Location;
import lec.spring.studygroupclone.Models.Study;
import lec.spring.studygroupclone.Models.Tag;
import lec.spring.studygroupclone.Services.LocationService;
import lec.spring.studygroupclone.Services.StudyService;
import lec.spring.studygroupclone.Services.TagService;
import lec.spring.studygroupclone.dataMappers.StudySetting;
import lec.spring.studygroupclone.helpers.account.CurrentUser;
import lec.spring.studygroupclone.helpers.study.StudySettingValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;

@Controller
@RequiredArgsConstructor
public class StudySettingController {

    public static final String STUDY_SETTING_CREATE_VIEW = "/study/setting/create";
    public static final String STUDY_SETTING_UPDATE_VIEW = "/study/setting/edit";
    public static final String STUDY_SETTING_BANNER_VIEW = "/study/setting/banner";
    public static final String STUDY_SETTING_TAG_VIEW = "/study/setting/tags";
    public static final String STUDY_SETTING_LOCATION_VIEW = "/study/setting/locations";
    public static final String STUDY_SETTING_STATUS_VIEW = "/study/setting/status";
    public static final String STUDY_SETTING_RECRUIT_VIEW = "/study/setting/recruit";

    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    private final StudyService studyService;
    private final StudySettingValidator studySettingValidator;
    private final TagService tagService;
    private final LocationService locationService;

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
        Study study = studyService.getStudyByPath(account, path);

        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(modelMapper.map(study, StudySetting.class));
        return STUDY_SETTING_UPDATE_VIEW;
    }

    @PostMapping(STUDY_SETTING_UPDATE_VIEW + "/{path}")
    public String update (@CurrentUser Account account, @PathVariable String path,
                          @Valid StudySetting studySetting, Errors errors,
                          Model model, RedirectAttributes redirectAttributes) {

        Study study = studyService.getStudyByPath(account, path);

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
        Study study = studyService.getStudyByPath(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        return STUDY_SETTING_BANNER_VIEW;
    }

    @PostMapping(STUDY_SETTING_BANNER_VIEW+"-toggle/{path}")
    public String bannerToggle(@CurrentUser Account account, @PathVariable String path,
                               String enable,
                               RedirectAttributes redirectAttributes){

        Study study = studyService.getStudyByPath(account, path);

        studyService.setStudyToggleBannerUsing(study, enable);

        redirectAttributes.addFlashAttribute("info", "toggle success");

        return "redirect:" + STUDY_SETTING_BANNER_VIEW + "/" + path;
    }

    @PostMapping(STUDY_SETTING_BANNER_VIEW+"-image/{path}")
    public String bannerImage(@CurrentUser Account account, @PathVariable String path,
                              String image,
                              RedirectAttributes redirectAttributes) throws IOException {

        Study study = studyService.getStudyByPath(account, path);

        studyService.setStudyBannerImage(study, image);

        redirectAttributes.addFlashAttribute("info", "update success");

        return "redirect:" + STUDY_SETTING_BANNER_VIEW + "/" + path;
    }

    @GetMapping(STUDY_SETTING_TAG_VIEW + "/{path}")
    public String tagForm (@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Study study = studyService.getStudyByPath(account, path, "tagAndManager");
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute("allTags", objectMapper.writeValueAsString(tagService.getAllTags()));
        return STUDY_SETTING_TAG_VIEW;
    }

    @PostMapping(STUDY_SETTING_TAG_VIEW + "/add")
    @ResponseBody
    public ResponseEntity updateTag(@CurrentUser Account account, @RequestBody HashMap<String, String> body){
        Tag resultTag = tagService.addTag(body.get("title"));
        Study study = studyService.getStudyByPath(account, body.get("path"));
        studyService.updateTag(study, resultTag);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(STUDY_SETTING_TAG_VIEW + "/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentUser Account account, @RequestBody HashMap<String, String> body){
        Tag resultTag = tagService.findByTitle(body.get("title"));
        Study study = studyService.getStudyByPath(account, body.get("path"));
        studyService.removeTag(study, resultTag);
        return ResponseEntity.ok().build();
    }

    @GetMapping(STUDY_SETTING_LOCATION_VIEW + "/{path}")
    public String editLocation(@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Study study = studyService.getStudyByPath(account, path, "locationsAndManager");
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute("allLocations", objectMapper.writeValueAsString(locationService.getAllLocations()));
        return STUDY_SETTING_LOCATION_VIEW;
    }

    @PostMapping(STUDY_SETTING_LOCATION_VIEW + "/add")
    @ResponseBody
    public ResponseEntity updateLocation(@CurrentUser Account account, @RequestBody HashMap<String, String> body){
        Location resultLocation = locationService.findByCity(body.get("city"));
        Study study = studyService.getStudyByPath(account, body.get("path"));
        if(resultLocation != null){
            studyService.updateLocation(study, resultLocation);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping(STUDY_SETTING_LOCATION_VIEW + "/remove")
    @ResponseBody
    public ResponseEntity deleteLocation(@CurrentUser Account account, @RequestBody HashMap<String, String> body){
        Location resultLocation = locationService.findByCity(body.get("city"));
        Study study = studyService.getStudyByPath(account, body.get("path"));
        studyService.removeLocation(study, resultLocation);
        return ResponseEntity.ok().build();
    }

    @GetMapping(STUDY_SETTING_STATUS_VIEW + "/{path}")
    public String status (@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyByPath(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        return STUDY_SETTING_STATUS_VIEW;
    }

    @PostMapping(STUDY_SETTING_STATUS_VIEW + "/{path}")
    public String statusModify(@CurrentUser Account account, @PathVariable String path, String status, RedirectAttributes redirectAttributes){
        Study study = studyService.getStudyByPath(account, path, "manager");
        String nowStatus = studyService.updateStatus(study, status);

        redirectAttributes.addFlashAttribute("info", "NOW the study is " + nowStatus);
        return "redirect:" + STUDY_SETTING_STATUS_VIEW + "/" + path;
    }

    @PostMapping(STUDY_SETTING_RECRUIT_VIEW + "/{path}")
    public String recruitModify(@CurrentUser Account account, @PathVariable String path, String status, RedirectAttributes redirectAttributes){
        Study study = studyService.getStudyByPath(account, path, "manager");
        String nowStatus = studyService.updateStatus(study, status);

        redirectAttributes.addFlashAttribute("info", "NOW the study is " + nowStatus);
        return "redirect:" + STUDY_SETTING_STATUS_VIEW + "/" + path;
    }

}
