package com.nisum.userws.services.impl;

import com.nisum.userws.models.User;
import com.nisum.userws.repositories.PhoneRepository;
import com.nisum.userws.repositories.UserRepository;
import com.nisum.userws.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl  implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhoneRepository phoneRepository;
    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User save(User userRequestDto) {

        User foundUser=userRepository.findByEmail(userRequestDto.getEmail());
        if(foundUser==null){//create
            var result = userRepository.save(userRequestDto);
            userRequestDto.getPhones().stream().forEach(p->{
                p.setUser(result);
                phoneRepository.save(p);
                    }


            );
            return result;
        }else{//update
            return userRepository.save(userRequestDto);
        }
    }

    @Override
    public void updateTokenUsername(String token, String username) {

    }
}

