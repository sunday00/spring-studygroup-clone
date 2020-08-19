package lec.spring.studygroupclone.Controllers;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.dataMappers.StudySetting;
import lec.spring.studygroupclone.helpers.account.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class StudyController {

    private static final String STUDY_CREATE_VIEW = "/study/create";

    @GetMapping(STUDY_CREATE_VIEW)
    public String create(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new StudySetting());
        return STUDY_CREATE_VIEW;
    }
}
