package com.example.hena.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {
//    The role field ("USER", "HOST", or "ADMIN") is used in:
//Spring Security config to control which URLs each role can access
//    The role field is used to enforce who can access what (via SecurityConfig).

//@Autowired
//private CustomUserDetailsService customUserDetailsService;



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for testing and public POSTs
//                .userDetailsService(customUserDetailsService)
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/", "/user/register", "/user/register/**", "/event/form", "/event/create", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/user/register").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/host/**").hasRole("HOST")
                        .requestMatchers("/user/**").hasAnyRole("USER", "HOST", "ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())  // enables default form login
                .httpBasic(Customizer.withDefaults()); // enables default basic auth

        return http.build();
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User
                .withUsername("admin")
                .password(passwordEncoder().encode("adminpass"))  // BCrypt encoding for the password
                .roles("ADMIN")
                .build();

        UserDetails host = User
                .withUsername("host")
                .password(passwordEncoder().encode("hostpass"))
                .roles("HOST")
                .build();

        UserDetails user = User
                .withUsername("user")
                .password(passwordEncoder().encode("userpass"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, host, user);
    }
//    @Bean
//    public JwtAuthenticationFilter jwtAuthenticationFilter() {
//        return new JwtAuthenticationFilter();}
}
