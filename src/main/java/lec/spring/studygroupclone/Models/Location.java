package lec.spring.studygroupclone.Models;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Location {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String localName;

    @Column(nullable = true)
    private String province;

    @Override
    public String toString() {
        if( this.province != null ) return String.format("%s(%s)/%s", this.city, this.localName, this.province);
        else return String.format("%s(%s)", this.city, this.localName);
    }
}
