package org.fjsei.yewu.third;

import org.fjsei.yewu.model.Author;
import org.fjsei.yewu.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


//连接到第三方平台的REST 客户端接口；
//将被org.springframework.web.reactive.client.WebClient所淘汰。

@Service
public class MyRestServiceClient {
    private final RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(MyRestServiceClient.class);


    public MyRestServiceClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<Book> getBooks() {
        ResponseEntity<Book[]> responseEntity = restTemplate.getForEntity(
                "http://localhost:3000/Books",
                Book[].class
        );
        return Arrays.asList(responseEntity.getBody());
    }

    public List<Book> getAuthorBooks(String authorId) {
        ResponseEntity<Book[]> responseEntity = restTemplate.getForEntity(
                "http://localhost:3000/Books",
                Book[].class
        );
        List<Book> Books = Arrays.asList(responseEntity.getBody());

        return Books.stream().filter(Book -> Book.getAuthor().getId().equals(authorId)).collect(Collectors.toList());
    }

    public List<Author> getAuthors() {
        ResponseEntity<Author[]> responseEntity = restTemplate.getForEntity(
                "http://localhost:3000/Authors",
                Author[].class
        );
        return Arrays.asList(responseEntity.getBody());
    }

    public Author getAuthor(String AuthorId) {
        ResponseEntity<Author> responseEntity = restTemplate
                .getForEntity("http://localhost:3000/Authors/{AuthorId}", Author.class, AuthorId);
        return responseEntity.getBody();
    }
}

