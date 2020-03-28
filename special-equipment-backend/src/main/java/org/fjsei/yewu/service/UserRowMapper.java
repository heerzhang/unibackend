package org.fjsei.yewu.service;

import org.fjsei.yewu.entity.incp.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

class UserRowMapper implements RowMapper<User> {

    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        User user = new User();
        user.setName(resultSet.getString("name"));
        user.setId(resultSet.getInt("id"));
        user.setAge(resultSet.getInt("age"));
        user.setAddress(resultSet.getString("address"));
        return user;
    }

}