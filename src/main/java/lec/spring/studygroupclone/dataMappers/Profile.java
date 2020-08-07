package lec.spring.studygroupclone.dataMappers;

import lec.spring.studygroupclone.Models.Account;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Getter
@NoArgsConstructor
public class Profile {

    @Length(max = 35, message = "Too long")
    private String description;

    private String website;

    private String job;

    private String location;

    private String profileImage;

    private boolean emailAlarm;
    private boolean studyCreatedAlarm;
    private boolean studyJoinAllowAlarm;
    private boolean studyUpdateAlarm;

    @Length(min = 4, message = "Too short")
    private String newPassword;
    private String newPasswordConfirm;

    public Profile(Account account) {
        this.description = account.getDescription();
        this.website = account.getWebsite();
        this.job = account.getJob();
        this.location = account.getLocation();
        this.profileImage = account.getProfileImage();

        this.newPassword = null;
        this.newPasswordConfirm = null;

        this.emailAlarm = account.isEmailAlarm();
        this.studyCreatedAlarm = account.isStudyCreatedAlarm();
        this.studyJoinAllowAlarm = account.isStudyJoinAllowAlarm();
        this.studyUpdateAlarm = account.isStudyUpdateAlarm();
    }
}
