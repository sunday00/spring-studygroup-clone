package lec.spring.studygroupclone.Controllers;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Notification;
import lec.spring.studygroupclone.Services.NotificationService;
import lec.spring.studygroupclone.helpers.ConsoleLog;
import lec.spring.studygroupclone.helpers.account.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notification")
    public String index (@CurrentUser Account account, Model model) {
        HashMap<String, List<Notification>> unreadList = notificationService.getUnreadSets(account);

        Long readCount = notificationService.getAlarmCount(account, true);

        model.addAttribute("isNew", true);
        model.addAttribute("unreadList", unreadList);
        model.addAttribute("readCount", readCount);

        return "/notification/main";
    }

    @PostMapping("/notification/read")
    @ResponseBody
    public ResponseEntity read(@CurrentUser Account account, @RequestBody HashMap<String, Long> body){
        notificationService.notificationRead(account, body.get("id"));
        return new ResponseEntity<>("{\"message\":\"DONE\"}", HttpStatus.OK);
    }

}
