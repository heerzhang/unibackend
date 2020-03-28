package org.fjsei.yewu.service.security;
//Todo:暂时作废
//import org.fjsei.yewu.entity.sei.User;
//import org.fjsei.yewu.entity.sei.UserRepository;
import org.fjsei.yewu.security.JwtUserFactory;
import org.fjsei.yewu.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    //Todo:暂时屏蔽
   // @Autowired
   // private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
  //Todo:暂时屏蔽
  //      User user = userRepository.findByUsername(username);
        User user =new User();
        user.setUsername("sdfjadsf4244290x");
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            return JwtUserFactory.create(user);    //user.enabled复制给了UserDetails.enabled//=isEnabled();
        }
    }
}

