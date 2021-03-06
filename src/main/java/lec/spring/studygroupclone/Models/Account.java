package lec.spring.studygroupclone.Models;

import lec.spring.studygroupclone.Services.StudyService;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter @Setter @EqualsAndHashCode(of="id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id @GeneratedValue
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

//    @Lob @Basic(fetch = FetchType.EAGER)
    @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean emailAlarm;

    private boolean studyCreatedAlarm;

    private boolean studyJoinAllowAlarm;

    private boolean studyUpdateAlarm;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    public List<String> getTagList(){
        if( this.getTags().size() == 0 ){
            return new ArrayList<>();
        }
        return this.getTags().stream().map(Tag::getTitle).collect(Collectors.toList());
    }

    @ManyToMany
    private Set<Location> locations = new HashSet<>();

    public List<String> getLocationList(){
        if( this.getLocations().size() == 0 ){
            return new ArrayList<>();
        }
        return this.getLocations().stream().map(Location::toString).collect(Collectors.toList());
    }

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.lastEmailCheckTokenCreatedAt = LocalDateTime.now();
    }

    public boolean canSendEmailCheckToken(String active){
        int waitMin = 10;
        if( active.equals("local") ){
            waitMin = 1;
        }
        return this.lastEmailCheckTokenCreatedAt.isBefore(LocalDateTime.now().minusMinutes(waitMin));
    }

    public Long getRemainAbleToResendEmailCheckToken(){
        return LocalDateTime.now().until(this.lastEmailCheckTokenCreatedAt.plusMinutes(10), ChronoUnit.MINUTES);
    }
}
