package lec.spring.studygroupclone.Models;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter @EqualsAndHashCode(of="id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    @NotBlank
    @Email
    private String email;

    @Column(unique = true)
    @NotBlank
    @Length(min = 3, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,20}$")
    private String nickname;

    @NotBlank
    private String password;

    private boolean emailVerified = false;

    private String emailCheckToken;

    private LocalDateTime lastEmailCheckTokenCreatedAt;

    private LocalDateTime joinedAt;

    @Length(max = 35, message = "Description is too long.")
    private String description;

    private String website;

    private String job;

    private String location;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean studyCreatedAlarm;

    private boolean studyJoinAllowAlarm;

    private boolean studyUpdateAlarm;

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.lastEmailCheckTokenCreatedAt = LocalDateTime.now();
    }

    public boolean canSendEmailCheckToken(){
        return this.lastEmailCheckTokenCreatedAt.isBefore(LocalDateTime.now().minusMinutes(10));
    }

    public int getRemainAbleToResendEmailCheckToken(){
        return this.lastEmailCheckTokenCreatedAt.plusMinutes(10).getMinute() - LocalDateTime.now().getMinute();
    }
}
