package org.fjsei.yewu.security;
//Todo:暂时屏蔽
//import org.fjsei.yewu.entity.sei.Authority;
//import org.fjsei.yewu.entity.sei.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class JwtUserFactory {
    //Todo:暂时改变



    private JwtUserFactory() {
    }

    public static JwtUser create(User user) {
        return new JwtUser(
                null,
                user.getUsername(),
                null,
                null,
                null,
                user.getPassword(),
                null,
                user.getEnabled(),
                null
        );
    }
//todo: 本文作废

}
