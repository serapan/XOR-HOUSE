package gr.ntua.ece.softeng18b.services;



import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//import gr.ntua.ece.softeng18b.model.Role;
import gr.ntua.ece.softeng18b.model.User;
import gr.ntua.ece.softeng18b.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optUser = userRepository.findByUsername(username);

        User user = optUser.get();

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole().getRole()));
        //BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        //String encryptedPassword = bCryptPasswordEncoder.encode(user.getPassword());

        org.springframework.security.core.userdetails.User res = new org.springframework.security.core.userdetails.User(
            user.getUsername(), user.getPassword(), grantedAuthorities);
        return res;
    }
}