package org.fjsei.yewu.resolver.incp.module;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.fjsei.yewu.entity.incp.JcAuthor;
import org.fjsei.yewu.entity.incp.JcAuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

//实际相当于controller;

@Component
public class JcQuery implements GraphQLQueryResolver {



        @Autowired
        private JcAuthorRepository jcauthorRepository;



        public Iterable<JcAuthor> findAllJcAuthors() {
            return jcauthorRepository.findAll();
        }


}
