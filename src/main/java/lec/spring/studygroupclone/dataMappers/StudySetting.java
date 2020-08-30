package lec.spring.studygroupclone.dataMappers;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class StudySetting {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-_]{3,25}$", message = "Make sure 3-25 alphabets, digits, - and _.")
    private String path;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-_\\s]{1,255}$", message = "Too short, too long or not allow weird character.")
    private String title;

    @NotBlank
    @Length(min = 0, max = 255)
    private String introduce;

    @NotBlank
    private String fullDescription;
}
