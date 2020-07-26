package lec.spring.studygroupclone.Controllers;

import lec.spring.studygroupclone.Models.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AcountController {
    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        model.addAttribute( new Account() );
        return "account/sign-up";
    }
}
