package org.fjsei.yewu.resolver.incp.module;

import graphql.kickstart.tools.GraphQLMutationResolver;
import org.fjsei.yewu.entity.incp.JcAuthor;
import org.fjsei.yewu.entity.incp.JcAuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


//实际相当于controller;

@Component
public class JcMutation implements GraphQLMutationResolver {
    @Autowired
    private JcAuthorRepository authorRepository;
    @PersistenceContext(unitName = "entityManagerFactoryIncp")
    private EntityManager emIncp;

    @Transactional
    public JcAuthor newJcAuthor(String nickname) {
        if(!emIncp.isJoinedToTransaction())      System.out.println("没达到 emIncp.isJoinedToTransaction()");
        else System.out.println("到 emIncp.isJoinedToTransaction()");
        if(!emIncp.isJoinedToTransaction())      emIncp.joinTransaction();
        Assert.isTrue(emIncp.isJoinedToTransaction(),"没emIncpisJoinedToTransaction");

        JcAuthor author = new JcAuthor();
        author.setNickname(nickname);
        authorRepository.save(author);
        return author;
    }

}
