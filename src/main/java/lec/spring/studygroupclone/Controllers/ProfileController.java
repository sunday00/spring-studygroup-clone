package lec.spring.studygroupclone.Controllers;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Services.AccountService;
import lec.spring.studygroupclone.dataMappers.Profile;
import lec.spring.studygroupclone.helpers.account.CurrentUser;
import lec.spring.studygroupclone.helpers.account.ProfileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileValidator profileValidator;
    private final AccountService accountService;

    public static final String PROFILE_EDIT_VIEW_NAME = "/profile/edit";
    public static final String PROFILE_READ_VIEW_NAME = "/profile/show";

    @InitBinder("profile")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(profileValidator);
    }

    @GetMapping("/profile/read/{nickname}")
    public String profile(@PathVariable String nickname, Model model, @CurrentUser Account account ) {
        Account findMember = accountService.getAccount(nickname);
        model.addAttribute(findMember);
        model.addAttribute("isYou", findMember.equals(account));
        return PROFILE_READ_VIEW_NAME;
    }

    @GetMapping(PROFILE_EDIT_VIEW_NAME)
    public String edit (@CurrentUser Account account, Model model) {
        model.addAttribute(accountService.getAccount(account.getNickname()));
        model.addAttribute(new Profile(account));
        return PROFILE_EDIT_VIEW_NAME;
    }

    @PostMapping(PROFILE_EDIT_VIEW_NAME)
    public String update(@CurrentUser Account account, @Valid Profile profile, Errors errors, Model model,
                         RedirectAttributes attributes){

        if( errors.hasErrors() ){
            model.addAttribute(account);
            return PROFILE_EDIT_VIEW_NAME; // 이건 리다이렉트하면 에러 안뜨고 생까버림.
        }
        accountService.update(account, profile);
        attributes.addFlashAttribute("info", "Edited successfully");
        return "redirect:"+PROFILE_EDIT_VIEW_NAME;
    }
}
