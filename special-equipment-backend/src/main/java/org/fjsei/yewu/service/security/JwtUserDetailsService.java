package org.fjsei.yewu.service.security;

import md.system.User;
import md.system.UserRepository;
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
        //这里的任务执行特别频繁！无状态的=没session，每次请求验证都要执行，添加cache注解，避免数据库压力。
        User user = userRepository.findById(userID).orElse(null);   //原来JWT是findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            return JwtUserFactory.create(user);    //user.enabled复制给了UserDetails.enabled//=isEnabled();
        }
    }
}

