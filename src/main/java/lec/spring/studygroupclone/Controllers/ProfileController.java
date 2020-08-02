package lec.spring.studygroupclone.Controllers;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Services.AccountService;
import lec.spring.studygroupclone.helpers.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final AccountService accountService;

    @GetMapping("/profile/read/{nickname}")
    public String profile(@PathVariable String nickname, Model model, @CurrentUser Account account ) {
        Account findMember = accountService.getAccount(nickname);
        model.addAttribute(findMember);
        model.addAttribute("isYou", findMember.equals(account));
        return "profile/show";
    }

    @GetMapping ("/profile/edit")
    public String edit (@CurrentUser Account account, Model model) {

        System.out.println(accountService.getAccount(account.getNickname()).toString());

        model.addAttribute(accountService.getAccount(account.getNickname()));
        return "profile/edit";
    }
}
