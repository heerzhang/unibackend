package org.fjsei.yewu.resolver;

import graphql.kickstart.tools.GraphQLMutationResolver;
import org.fjsei.yewu.exception.BookNotFoundException;
import org.fjsei.yewu.model.Author;
import org.fjsei.yewu.model.Book;
import org.fjsei.yewu.repository.AuthorRepository;
import org.fjsei.yewu.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

//GraphqlConfiguration 关联，Mutation=类名字可修改
public class Mutation implements GraphQLMutationResolver {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @PersistenceContext(unitName = "entityManagerFactorySei")
    private EntityManager emSei;

    public Mutation(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public Author newAuthor(String firstName, String lastName) {
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Author author = new Author();
        author.setFirstName(firstName);
        author.setLastName(lastName);

        authorRepository.save(author);

        return author;
    }

   @Transactional
    public Book newBook(String title, String isbn, Integer pageCount, Long authorId) {
        if(!emSei.isJoinedToTransaction())      System.out.println("没达到 emSei.isJoinedToTransaction()");
        else System.out.println("到 emSei.isJoinedToTransaction()");
        if(!emSei.isJoinedToTransaction())      emSei.joinTransaction();
        Assert.isTrue(emSei.isJoinedToTransaction(),"没emSeiisJoinedToTransaction");
        Book book = new Book();
        book.setAuthor(new Author(authorId));
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setPageCount(pageCount != null ? pageCount : 0);

        bookRepository.save(book);

        return book;
    }

    public boolean deleteBook(Long id) {
        //hez
        bookRepository.deleteById(id);
        return true;
    }

    public Book updateBookPageCount(Integer pageCount, Long id) {
        //hez
        Book book = bookRepository.findById(id).get();
        if(book == null) {
            throw new BookNotFoundException("The book to be updated was found", id);
        }
        book.setPageCount(pageCount);

        bookRepository.save(book);

        return book;
    }
}
