package org.fjsei.yewu.repository;

import org.fjsei.yewu.model.Book;
import org.springframework.data.repository.CrudRepository;


public interface BookRepository extends CrudRepository<Book, Long> {
}

