package dev.siraj.restauron.service.authentication;

import dev.siraj.restauron.config.userDetails.CustomUserDetails;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.respository.userRepo.UserRepository;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserAll user = repository.findByEmail(email);

        if(user == null){
            throw  new UsernameNotFoundException("The user with the emailId "+ email +" is not found");
        }

        return new CustomUserDetails(user);
    }
}
