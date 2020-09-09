package lec.spring.studygroupclone.Repositories;

import com.querydsl.core.types.Predicate;
import lec.spring.studygroupclone.Models.Location;
import lec.spring.studygroupclone.Models.QAccount;
import lec.spring.studygroupclone.Models.Tag;

import java.util.Set;

public class AccountPredicate {
    public static Predicate findByTagsAnsLocations(Set<Tag> tags, Set<Location> locations){
        QAccount account = QAccount.account;
        return account.tags.any().in(tags).and(account.locations.any().in(locations));
    }
}
