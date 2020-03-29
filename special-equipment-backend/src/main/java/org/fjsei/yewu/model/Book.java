package org.fjsei.yewu.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Book {
    @Id
    @Column(name="book_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    private Long id;

    @Column(name="book_title", nullable = false)
    private String title;

    @Column(name="book_isbn", nullable = false)
    private String isbn;

    @Column(name="book_pageCount", nullable = false)
    private int pageCount;

    @ManyToOne
    @JoinColumn(name = "author_id",
            nullable = false, updatable = false)
    private Author author;

}
