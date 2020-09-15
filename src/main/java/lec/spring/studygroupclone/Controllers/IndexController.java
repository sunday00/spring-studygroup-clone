package lec.spring.studygroupclone.Controllers;

import lec.spring.studygroupclone.Models.Study;
import lec.spring.studygroupclone.Services.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final StudyService studyService;

    @GetMapping(path = {"/", "/main", "/index"})
    public String index(){
        return "main";
    }

    @GetMapping ("/search/study")
    public String search(@RequestParam String keyword, Model model){
        List<Study> studies = studyService.searchStudyList(keyword);
        model.addAttribute("studies", studies);
        model.addAttribute("keyword", keyword);
        return "/search";
    }
}
