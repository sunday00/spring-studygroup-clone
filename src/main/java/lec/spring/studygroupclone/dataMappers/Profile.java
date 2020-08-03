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

    public Profile(Account account) {
        this.description = account.getDescription();
        this.website = account.getWebsite();
        this.job = account.getJob();
        this.location = account.getLocation();
    }
}
