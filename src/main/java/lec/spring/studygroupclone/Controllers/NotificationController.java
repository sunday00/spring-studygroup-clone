package lec.spring.studygroupclone.Controllers;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Notification;
import lec.spring.studygroupclone.Services.NotificationService;
import lec.spring.studygroupclone.helpers.ConsoleLog;
import lec.spring.studygroupclone.helpers.account.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

}
