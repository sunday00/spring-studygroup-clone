package lec.spring.studygroupclone.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping(path = {"/", "/main", "/index"})
    public String index(){
        return "main";
    }
}
