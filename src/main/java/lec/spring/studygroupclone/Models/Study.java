package lec.spring.studygroupclone.Models;

import lec.spring.studygroupclone.dataMappers.CurrentAccount;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@NamedEntityGraph(name="Study.withAll", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("locations"),
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("members")
})
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Study {

    @Id @GeneratedValue
    private Long id;

    @ManyToMany
    private Set<Account> managers = new HashSet<>();

    public List<String> getManagerList(){
        if( this.getManagers().size() == 0 ){
            return new ArrayList<>();
        }
        return this.getManagers().stream().map(Account::getNickname).collect(Collectors.toList());
    }

    @ManyToMany
    private Set<Account> members = new HashSet<>();

    public List<String> getMemberList(){
        if( this.getMembers().size() == 0 ){
            return new ArrayList<>();
        }
        return this.getMembers().stream().map(Account::getNickname).collect(Collectors.toList());
    }

    @Column(unique = true)
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-_]{3,25}$")
    private String path;

    @NotBlank
    private String title;

    private String introduce;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;
    //TODO:: change for not base64. using helper.convertor;

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

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdatedDateTime;

    private boolean recruiting;

    private boolean published = false;

    private boolean closed;

    private boolean useBanner;

    public void addManager(Account account) {
        this.managers.add(account);
    }

    public boolean isJoinable(CurrentAccount currentAccount) {
        Account account = currentAccount.getAccount();
        return this.isPublished() && this.isRecruiting()
                && !this.members.contains(account) && !this.managers.contains(account);

    }

    public boolean isMember(CurrentAccount currentAccount) {
        return this.members.contains(currentAccount.getAccount());
    }

    public boolean isManager(CurrentAccount currentAccount) {
        return this.managers.contains(currentAccount.getAccount());
    }

    public boolean isManager(Account account) {
        return this.managers.contains(account);
    }
}
