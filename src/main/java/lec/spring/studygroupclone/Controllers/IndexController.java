package lec.spring.studygroupclone.Controllers;

import com.github.javafaker.Faker;
import com.querydsl.core.Tuple;
import lec.spring.studygroupclone.Models.Account;
import lec.spring.studygroupclone.Models.Location;
import lec.spring.studygroupclone.Models.Study;
import lec.spring.studygroupclone.Models.Tag;
import lec.spring.studygroupclone.Services.AccountService;
import lec.spring.studygroupclone.Services.LocationService;
import lec.spring.studygroupclone.Services.StudyService;
import lec.spring.studygroupclone.Services.TagService;
import lec.spring.studygroupclone.config.AppConfig;
import lec.spring.studygroupclone.helpers.ConsoleLog;
import lec.spring.studygroupclone.helpers.account.CurrentUser;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final StudyService studyService;
    private final AccountService accountService;
    private final TagService tagService;
    private final LocationService locationService;

    @GetMapping(path = {"/", "/main", "/index"})
    public String index(){
        return "main";
    }

    @GetMapping ("/search/study")
    public String search(@RequestParam String keyword, Model model,
             @PageableDefault(size = 6, sort = "publishedDateTime", direction = Sort.Direction.DESC) Pageable pageable){
        Page<Study> studyPagination = studyService.searchStudyList(keyword, pageable);
        model.addAttribute("studies", studyPagination);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", studyService.getSimpleSort(studyPagination.getSort()));

        studyPagination.getContent().forEach(ConsoleLog::print);

        return "/search";
    }

    @GetMapping("/generate-faker-study")
    public String generate(@CurrentUser Account account){
        Faker faker = new Faker();
        for(int i = 0; i<50; i++){
            String path = RandomString.make(7);

            Set<Tag> tags = new HashSet<>();
            for(int k = 0 ; k < faker.number().numberBetween(1,3); k ++){
                Tag tag = new Tag();
                tag.setTitle(faker.job().field());
                if( tagService.findByTitle(tag.getTitle()) == null ){
                    Tag savedTag = tagService.save(tag);
                    tags.add(savedTag);
                }
            }
            Tag essetialTag = new Tag();
            essetialTag.setTitle("oh-" + faker.educator().course().replace(" ", ""));

            if( tagService.findByTitle(essetialTag.getTitle()) == null ){
                essetialTag = tagService.save(essetialTag);
                tags.add(essetialTag);
            }

            Set<Location> locations = new HashSet<>();
            for(int l=0; l<faker.number().numberBetween(1,3); l++){
                Location location = locationService.findById(faker.number().numberBetween(1, 85));
                locations.add(location);
            }

            Set<Account> members = new HashSet<>();
            Set<Account> managers = new HashSet<>();

            for( int j = 0; j < faker.number().numberBetween(1, 5); j++ ){
//                String nick = faker.name().username().replace(" ", "");
                String nick = faker.superhero().name().replace(" ", "");

                if( nick.length() > 20 ) nick = nick.substring(0, 19);

                if( accountService.countByNickname(nick) != null) nick = nick.substring(4) + faker.lorem().characters(3);

                Account member = Account.builder()
                        .nickname(nick)
                        .email(faker.internet().emailAddress())
                        .password(AppConfig.passwordEncoder().encode(faker.lorem().characters(8,10)))
                        .build();
                Account savedMember = accountService.save(member);
                members.add(savedMember);
            }

            for( int j = 0; j < faker.number().numberBetween(1, 2); j++ ){
                String nick = faker.artist().name().replace(" ", "");

                if( nick.length() > 20 ) nick = nick.substring(0, 19);

                if( accountService.countByNickname(nick) != null) nick = nick.substring(4) + faker.lorem().characters(3);

                Account member = Account.builder()
                        .nickname(nick)
                        .email(faker.internet().emailAddress())
                        .password(AppConfig.passwordEncoder().encode(faker.lorem().characters(8,10)))
                        .build();
                Account savedManager = accountService.save(member);
                managers.add(savedManager);
            }

            Study study = Study.builder()
                    .title("oh-" + faker.educator().course())
                    .path("test-" + path)
                    .introduce(faker.lorem().sentence())
                    .fullDescription(faker.lorem().paragraph(3))
                    .published(true)
                    .publishedDateTime(LocalDateTime.now().minusDays(faker.number().numberBetween(0L, 60L)))
                    .build();

            Study savedStudy = studyService.save(study);

            savedStudy.setTags(tags);
            savedStudy.setLocations(locations);
            savedStudy.setMembers(members);
            savedStudy.setManagers(managers);

//            studyService.save(savedStudy);
        }

        return "redirect:/";
    }
}
