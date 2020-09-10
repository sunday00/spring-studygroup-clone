package lec.spring.studygroupclone.helpers.notification;

import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Services.NotificationService;
import lec.spring.studygroupclone.dataMappers.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class NotificationInterceptor implements HandlerInterceptor {

    private final NotificationService notificationService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if( isInterceptable(modelAndView, authentication) ){
            Account account = ((CurrentAccount) authentication.getPrincipal()).getAccount();
            Long unreadNotificationCount = notificationService.getAlarmCount(account, false);
            modelAndView.addObject("unreadNotification", unreadNotificationCount);
        }

    }

    private boolean isInterceptable(ModelAndView modelAndView, Authentication authentication){

        return authentication != null
                && modelAndView != null
                && authentication.getPrincipal() instanceof CurrentAccount
                && !isRedirectRequest(modelAndView);
    }

    private boolean isRedirectRequest(ModelAndView modelAndView){
        return (modelAndView.getViewName() != null && modelAndView.getViewName().startsWith("redirect:"))
                || modelAndView.getView() instanceof RedirectView;
    }
}
