package org.fjsei.yewu.entity.incp;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
public class JcAuthor {

        @Id
        @GeneratedValue
        private Long id;

        @Column(unique = true, nullable = false)
        private String nickname;

        @OneToMany
        @JoinColumn(name = "author_id")
        private Set<JcBook> books;
}

