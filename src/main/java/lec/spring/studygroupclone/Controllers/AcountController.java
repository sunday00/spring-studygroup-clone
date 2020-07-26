package lec.spring.studygroupclone.Controllers;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Repositories.AccountRepository;
import lec.spring.studygroupclone.Services.AccountService;
import lec.spring.studygroupclone.helpers.AccountValidator;
import lec.spring.studygroupclone.helpers.ConsoleMailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
}
