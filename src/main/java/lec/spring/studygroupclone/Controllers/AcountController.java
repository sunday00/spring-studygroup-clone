package lec.spring.studygroupclone.Controllers;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Services.AccountService;
import lec.spring.studygroupclone.helpers.account.AccountValidator;
import lec.spring.studygroupclone.helpers.account.CurrentUser;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;

@Controller
@RequiredArgsConstructor
public class AcountController {

    private final AccountValidator accountValidator;
    private final AccountService accountService;

    @InitBinder("account")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(accountValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        model.addAttribute( new Account() );
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpProcess(@Valid Account account, Errors errors){
        if(errors.hasErrors()) return "account/sign-up";

        accountService.processSignUp(account);

        return "redirect:/";
    }

    @GetMapping("/check-email")
    public String checkEmail(){
        return "account/check-email";
    }

    @PostMapping("/resend-verification-token")
    public String resendVerificationToken(@CurrentUser Account account, Model model){

        if( accountService.resendVerificationToken(account) ) {
            return "redirect:/";
        }

        model.addAttribute("error", "wait");

        return "account/check-email";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model){
        Account account = accountService.checkEmailToken(token, email);
        String view = "account/checked-email";

        if( account == null) {
            model.addAttribute("error", "Email Verification Failed");
            return view;
        }

        model.addAttribute("countMember", accountService.count());
        model.addAttribute("nickname", account.getNickname());

        return view;
    }

    @GetMapping("/login")
    public String login(Account account) {
        return "account/login";
    }

    @GetMapping("/login-nopassword")
    public String nopassword(Account account){
        return "account/nopassword";
    }

    @PostMapping("/login-nopassword")
    public String sendTokenForNopassword(Account account, Model model, RedirectAttributes attributes){

        HashMap<String, String> result = accountService.sendLoginToken(account);
        if( result.get("error") != null ){
            model.addAttribute("error", result.get("error"));
            return "account/nopassword";
        }

        attributes.addFlashAttribute("info", result.get("info"));

        return "redirect:/login-nopassword";
    }

}
