package org.fjsei.yewu.third;

import md.specialEqp.Eqp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


//仅当演示;
//连接到第三方平台的REST 客户端接口；在后端发起的三方调用等待收集结果。
//将被org.springframework.web.reactive.client.WebClient所淘汰。

@Service
public class MyRestServiceClient {
    private final RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(MyRestServiceClient.class);


    public MyRestServiceClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }
    //仅当演示
    public List<Eqp> getBooks() {
        ResponseEntity<Eqp[]> responseEntity = restTemplate.getForEntity(
                "http://localhost:3000/Books",
                Eqp[].class
        );
        return Arrays.asList(responseEntity.getBody());
    }
    //仅当演示
    public List<Eqp> getAuthorBooks(String authorId) {
        ResponseEntity<Eqp[]> responseEntity = restTemplate.getForEntity(
                "http://localhost:3000/Books",
                Eqp[].class
        );
        List<Eqp> Books = Arrays.asList(responseEntity.getBody());

        return Books.stream().filter(Book -> Book.getPos().getId().equals(authorId)).collect(Collectors.toList());
    }

    public List<Eqp> getAuthors() {
        ResponseEntity<Eqp[]> responseEntity = restTemplate.getForEntity(
                "http://localhost:3000/Authors",
                Eqp[].class
        );
        return Arrays.asList(responseEntity.getBody());
    }
    //仅当演示
    public Eqp getAuthor(String AuthorId) {
        ResponseEntity<Eqp> responseEntity = restTemplate
                .getForEntity("http://localhost:3000/Authors/{AuthorId}", Eqp.class, AuthorId);
        return responseEntity.getBody();
    }
}

