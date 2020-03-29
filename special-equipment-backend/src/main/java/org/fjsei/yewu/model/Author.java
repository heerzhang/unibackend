package org.fjsei.yewu.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Author {
    // @GeneratedValue(strategy= GenerationType.AUTO)
    //Oracle不能用。

    @Id
    @Column(name="book_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    private Long id;

    @Column(name="author_first_name", nullable = false)
    private String firstName;

    @Column(name="author_last_name", nullable = false)
    private String lastName;

/*
    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
*/
}
