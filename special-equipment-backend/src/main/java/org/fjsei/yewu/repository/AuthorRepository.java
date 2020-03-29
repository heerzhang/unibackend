package org.fjsei.yewu.repository;


import org.fjsei.yewu.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/*
@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {
}
*/

public interface AuthorRepository extends JpaRepository<Author, Long>, JpaSpecificationExecutor<Author> {
}
