package lec.spring.studygroupclone.Services;

import lec.spring.studygroupclone.Models.Tag;
import lec.spring.studygroupclone.Repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public Tag addTag(Tag tag){
        String title = tag.getTitle();

        Tag resultTag;

        Tag exitedTag = tagRepository.findByTitle(title);
        if( exitedTag == null ) resultTag = tagRepository.save(tag);
        else resultTag = exitedTag;

        return resultTag;
    }

    public Tag findByTitle(Tag tag) {
        return tagRepository.findByTitle(tag.getTitle());
    }

    public List<String> getAllTags() {
        List<Tag> list = tagRepository.findAll();
        if( list.size() == 0 ) return new ArrayList<String>(Collections.singletonList(""));
        return list.stream().map(Tag::getTitle).collect(Collectors.toList());
    }
}
