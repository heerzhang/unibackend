package org.fjsei.yewu.service;

import org.fjsei.yewu.entity.incp.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @program: spring-boot-example
 * @description:
 * @author:
 * @create: 2018-05-02 10:07
 **/

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    @Qualifier("incpJdbcTemplate")
    protected JdbcTemplate fooJdbcTemplate;

    @Autowired
    @Qualifier("sdnJdbcTemplate")
    protected JdbcTemplate barJdbcTemplate;

    @Override
    public User getUserById(int id) {
        User user = fooJdbcTemplate.queryForObject("select * from user where id=?", new Object[]{id},new UserRowMapper());
        User user2 = barJdbcTemplate.queryForObject("select * from user where id=?", new Object[]{id},new UserRowMapper());
        System.out.println(user);
        System.out.println(user2);
        return user;
    }

}

