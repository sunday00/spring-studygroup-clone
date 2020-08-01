package lec.spring.studygroupclone.Controllers;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Services.AccountService;
import lec.spring.studygroupclone.helpers.AccountValidator;
import lec.spring.studygroupclone.helpers.CurrentUser;
import lombok.RequiredArgsConstructor;
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
        String view = "account/check-email";
        return view;
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

//    @PostMapping("/login")
//    public String signIn(Account account, Model model){
//        if( accountService.signIn(account) ){
//            return "redirect:/";
//        }
//
//        model.addAttribute("error", "wrong");
//        return "account/login";
//    }
// security 로 대체 완료

    @GetMapping("/profile/{nickname}")
    public String profile(@PathVariable String nickname, Model model, @CurrentUser Account account ) {

        return "account/profile";
    }
}
