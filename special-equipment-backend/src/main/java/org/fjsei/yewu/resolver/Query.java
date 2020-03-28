package org.fjsei.yewu.resolver;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.fjsei.yewu.model.Author;
import org.fjsei.yewu.model.Book;
import org.fjsei.yewu.repository.AuthorRepository;
import org.fjsei.yewu.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;

//GraphqlConfiguration 关联，Query=类名字可修改
public class Query implements GraphQLQueryResolver {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;

    public Query(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    public Iterable<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    public Iterable<Author> findAllAuthors() {
        return authorRepository.findAll();
    }

    public long countBooks() {
        return bookRepository.count();
    }
    public long countAuthors() {
        return authorRepository.count();
    }
}
