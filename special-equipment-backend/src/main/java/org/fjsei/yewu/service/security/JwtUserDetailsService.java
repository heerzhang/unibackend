package org.fjsei.yewu.service.security;

import org.fjsei.yewu.entity.sei.User;
import org.fjsei.yewu.entity.sei.UserRepository;
import org.fjsei.yewu.security.JwtUserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    //参数username identifying the user；改成用户表的ID； spring security只需要以ID识别用户。
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //token / JWT当中实际存储user ID,代替username做标识。
        Long userID =Long.valueOf(username);
        User user = userRepository.findById(userID).orElse(null);   //原来findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            return JwtUserFactory.create(user);    //user.enabled复制给了UserDetails.enabled//=isEnabled();
        }
    }
}

