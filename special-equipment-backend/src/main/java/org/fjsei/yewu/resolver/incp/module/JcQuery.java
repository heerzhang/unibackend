package org.fjsei.yewu.resolver.incp.module;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.fjsei.yewu.entity.incp.JcAuthor;
import org.fjsei.yewu.entity.incp.JcAuthorRepository;
import org.fjsei.yewu.entity.incp.JcBook;
import org.fjsei.yewu.entity.incp.JcBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

//实际相当于controller;

@Component
public class JcQuery implements GraphQLQueryResolver {


        @Autowired
        private JcBookRepository jcbookRepository;

        @Autowired
        private JcAuthorRepository jcauthorRepository;

//       @Autowired
//       private BookService bookService;

        public Iterable<JcBook> findAllJcBooks() {
            return jcbookRepository.findAll();
        }

        public Iterable<JcAuthor> findAllJcAuthors() {
            return jcauthorRepository.findAll();
        }

        public Long countJcBook(Long authorId) {
            if (authorId == null) return jcbookRepository.count();
            JcAuthor author = jcauthorRepository.findById(authorId).orElse(null);
           // if (author == null) throw new NotFoundException("未找到",new Exception("null"));
            Assert.isTrue(author != null,"未找到author:"+author);

            int myInt=jcbookRepository.findByAuthor(author).size();
            return Long.parseLong(new String().valueOf(myInt));
        }

}
