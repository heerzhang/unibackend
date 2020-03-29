package org.fjsei.yewu.repository;


import org.fjsei.yewu.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


/*
public interface BookRepository extends CrudRepository<Book, Long> {
}
*/
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
}
