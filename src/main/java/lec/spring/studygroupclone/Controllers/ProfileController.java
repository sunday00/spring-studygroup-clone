package lec.spring.studygroupclone.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Location;
import lec.spring.studygroupclone.Models.Tag;
import lec.spring.studygroupclone.Services.AccountService;
import lec.spring.studygroupclone.Services.LocationService;
import lec.spring.studygroupclone.Services.StudyService;
import lec.spring.studygroupclone.Services.TagService;
import lec.spring.studygroupclone.dataMappers.Profile;
import lec.spring.studygroupclone.helpers.account.CurrentUser;
import lec.spring.studygroupclone.helpers.account.ProfileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileValidator profileValidator;
    private final AccountService accountService;
    private final TagService tagService;
    private final LocationService locationService;
    private final ObjectMapper objectMapper;

    public static final String PROFILE_READ_VIEW_NAME = "/profile/show";
    public static final String PROFILE_EDIT_VIEW_NAME = "/profile/edit";
    public static final String PASSWD_EDIT_VIEW_NAME = "/profile/pswd";
    public static final String NOTIFICATION_EDIT_VIEW_NAME = "/profile/noti";
    public static final String TAG_EDIT_VIEW_NAME = "/profile/tags";
    public static final String LOCATION_EDIT_VIEW_NAME = "/profile/locations";
    public static final String ACCOUNT_EDIT_VIEW_NAME = "/profile/account";

    @InitBinder("profile")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(profileValidator);
    }

    @GetMapping("/profile/read/{nickname}")
    public String profile(@PathVariable String nickname, Model model, @CurrentUser Account account) {
        Account findMember;
        try{
            findMember = accountService.getAccount(nickname);
            model.addAttribute(findMember);
            model.addAttribute("isYou", findMember.equals(account));
            model.addAttribute("studies", accountService.getStudies(findMember));
        } catch (IllegalArgumentException exception){
          model.addAttribute("error", nickname + " is not a member");
        }
        return PROFILE_READ_VIEW_NAME;
    }

    @GetMapping(PROFILE_EDIT_VIEW_NAME)
    public String edit(@CurrentUser Account account, Model model) {
        model.addAttribute(accountService.getAccount(account.getNickname()));
        model.addAttribute(new Profile(account));
        return PROFILE_EDIT_VIEW_NAME;
    }

    @PostMapping(PROFILE_EDIT_VIEW_NAME)
    public String update(@CurrentUser Account account, @Valid Profile profile, Errors errors, Model model,
            RedirectAttributes attributes) throws IOException {

        if( errors.hasErrors() ){
            model.addAttribute(account);
            return PROFILE_EDIT_VIEW_NAME; // 이건 리다이렉트하면 에러 안뜨고 생까버림.
        }

        accountService.update(account, profile);
        attributes.addFlashAttribute("info", "Edited successfully");
        return "redirect:"+PROFILE_EDIT_VIEW_NAME;
    }

    @GetMapping(PASSWD_EDIT_VIEW_NAME)
    public String password (@CurrentUser Account account, Model model) {
        model.addAttribute(accountService.getAccount(account.getNickname()));
        model.addAttribute(new Profile(account));
        return PASSWD_EDIT_VIEW_NAME;
    }

    @PostMapping(PASSWD_EDIT_VIEW_NAME)
    public String updatePassword (@CurrentUser Account account, @Valid Profile profile, Errors errors, Model model,
                                  RedirectAttributes attributes) {
        if( errors.hasErrors() ){
            model.addAttribute(account);
            return PASSWD_EDIT_VIEW_NAME;
        }

        accountService.updatePasswd(account, profile);
        attributes.addFlashAttribute("info", "Edited successfully");
        return "redirect:"+PASSWD_EDIT_VIEW_NAME;
    }

    @GetMapping(NOTIFICATION_EDIT_VIEW_NAME)
    public String notification (@CurrentUser Account account, Model model) {
        model.addAttribute(accountService.getAccount(account.getNickname()));
        model.addAttribute(new Profile(account));
        return NOTIFICATION_EDIT_VIEW_NAME;
    }

    @PostMapping(NOTIFICATION_EDIT_VIEW_NAME)
    public String updateNotification (@CurrentUser Account account, @Valid Profile profile, Errors errors, Model model,
                                  RedirectAttributes attributes) {
        if( errors.hasErrors() ){
            model.addAttribute(account);
            return NOTIFICATION_EDIT_VIEW_NAME;
        }

        accountService.updateAlarm(account, profile);
        attributes.addFlashAttribute("info", "Edited successfully");
        return "redirect:"+NOTIFICATION_EDIT_VIEW_NAME;
    }

    @GetMapping(TAG_EDIT_VIEW_NAME)
    public String editTag(@CurrentUser Account account, Model model) throws JsonProcessingException {
        Account member = accountService.getAccount(account.getNickname());
        model.addAttribute("account", member);
        model.addAttribute(new Profile(account));
        model.addAttribute("allTags", objectMapper.writeValueAsString(tagService.getAllTags()));
        return TAG_EDIT_VIEW_NAME;
    }

    @PostMapping(TAG_EDIT_VIEW_NAME + "/add")
    @ResponseBody
    public ResponseEntity updateTag(@CurrentUser Account account, @RequestBody Tag tag){
        Tag resultTag = tagService.addTag(tag);
        accountService.updateTag(account, resultTag);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(TAG_EDIT_VIEW_NAME + "/remove")
    @ResponseBody
    public ResponseEntity deleteTag(@CurrentUser Account account, @RequestBody Tag tag){
        Tag resultTag = tagService.findByTitle(tag);
        accountService.removeTag(account, resultTag);
        return ResponseEntity.ok().build();
    }

    @GetMapping(LOCATION_EDIT_VIEW_NAME)
    public String editLocation(@CurrentUser Account account, Model model) throws JsonProcessingException {
        Account member = accountService.getAccount(account.getNickname());
        model.addAttribute("account", member);
        model.addAttribute(new Profile(account));
        model.addAttribute("allLocations", objectMapper.writeValueAsString(locationService.getAllLocations()));
        return LOCATION_EDIT_VIEW_NAME;
    }

    @PostMapping(LOCATION_EDIT_VIEW_NAME + "/add")
    @ResponseBody
    public ResponseEntity updateLocation(@CurrentUser Account account, @RequestBody Location location){
        Location resultLocation = locationService.findByCity(location);
        if(resultLocation != null){
            accountService.updateLocation(account, resultLocation);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping(LOCATION_EDIT_VIEW_NAME + "/remove")
    @ResponseBody
    public ResponseEntity deleteLocation(@CurrentUser Account account, @RequestBody Location location){
        Location resultlocation = locationService.findByCity(location);
        accountService.removeLocation(account, resultlocation);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ACCOUNT_EDIT_VIEW_NAME)
    public String editAccount (@CurrentUser Account account, Model model) {
        model.addAttribute(accountService.getAccount(account.getNickname()));
        model.addAttribute(new Profile(account));
        return ACCOUNT_EDIT_VIEW_NAME;
    }

    @PostMapping(ACCOUNT_EDIT_VIEW_NAME)
    public String updateAccount (@CurrentUser Account account, @Valid Profile profile, Errors errors, Model model,
                                  RedirectAttributes attributes) {
        if( errors.hasErrors() ){
            model.addAttribute(account);
            return ACCOUNT_EDIT_VIEW_NAME;
        }

        accountService.updateAccount(account, profile);
        attributes.addFlashAttribute("info", "Edited successfully");
        return "redirect:"+ACCOUNT_EDIT_VIEW_NAME;
    }

}
