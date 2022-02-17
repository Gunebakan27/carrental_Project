package com.dev.carrental_project.security.service;


import com.dev.carrental_project.domain.User;


import com.dev.carrental_project.repository.UserRepository;
import com.dev.carrental_project.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@AllArgsConstructor
@Service
/* */
public class UserDetailsServiceImpl implements UserDetailsService {//bununla kullaniciyi username ile yukleme metodunu override

    private final UserRepository userRepository;

    @Override//user import domain
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {//burada kullaniciyi kontrol ediyor databasen
        User user=userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("user not found with email"+email));
        //email kontrol edip aldiktan sonra (pass bilgiler de icinde var)//optional cok onemli ya yoksa diye kontrol ediyor
        return UserDetailsImpl.build(user);//bilgiler duzgun bir sekilde(role pass email id ) aldik

        /*username e email ile giris yapiyoruz bundan dolayi email ile useri aratiyoruz
         springboot bize findbyid metodunu sagliyor ama findbyemail i saglaniyor
         bundan dolayi bizde interface ile kendimiz olusturuyoruz.*/
    }
}

