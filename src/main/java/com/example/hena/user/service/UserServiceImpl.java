//package com.example.hena.user.service;
//
//import com.example.hena.user.entity.User;
//import com.example.hena.user.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class UserServiceImpl extends UserService  {
////    implements UserDetailsService
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Override
//    public User findByEmail(String email) {
//        Optional<User> userOpt = userRepository.findByEmail(email);
//        return userOpt.orElse(null);
//    }
//
//    @Override
//    public boolean checkPassword(String rawPassword, String encodedPassword) {
//        return passwordEncoder.matches(rawPassword, encodedPassword);
//    }
//
////    @Override
////    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
////        User user = userRepository.findByEmail(email)
////                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
////
////        return org.springframework.security.core.userdetails.User
////                .withUsername(user.getEmail())
////                .password(user.getPassword())
////                .authorities("ROLE_" + user.getRole())
////                .build();
////    }
//
//
//    // Implement other methods here as needed
//}
