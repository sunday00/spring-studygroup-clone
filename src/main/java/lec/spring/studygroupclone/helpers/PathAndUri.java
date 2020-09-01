package lec.spring.studygroupclone.helpers;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
@Getter @Setter
public class PathAndUri {

    private String uri;
    private String[] uriSplit;

    public void setUri() {
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequestUri();
        this.uri = builder.build().toUri().getPath();
        this.uriSplit = this.uri.split("/");
    }

    public String getLastPath(){
        return this.uriSplit[this.uriSplit.length - 1];
    }

    public String getMode(){
        if (this.getLastPath().equals("create")) return this.getLastPath();
        else return this.uriSplit[this.uriSplit.length - 2];
    }
}
