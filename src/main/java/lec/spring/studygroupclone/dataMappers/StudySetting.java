package lec.spring.studygroupclone.dataMappers;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Getter
@NoArgsConstructor
public class StudySetting {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-_]{3,25}$")
    private String path;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-_]{1,255}$")
    private String title;

    @NotBlank
    @Length(min = 0, max = 255)
    private String introduce;

    @NotBlank
    private String fullDescription;
}
