package com.dev.carrental_project.service;

import com.dev.carrental_project.domain.Role;
import com.dev.carrental_project.domain.User;
import com.dev.carrental_project.domain.enumeration.UserRole;

import com.dev.carrental_project.dto.UserDTO;
import com.dev.carrental_project.exception.AuthException;
import com.dev.carrental_project.exception.BadRequestException;
import com.dev.carrental_project.exception.ConflictException;
import com.dev.carrental_project.exception.ResourceNotFoundException;


import com.dev.carrental_project.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
@AllArgsConstructor
@Service
public class UserService {
    private final com.dev.carrental_project.repository.UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final static String USER_NOT_FOUND_MSG="user with id %d not found";
    private final PasswordEncoder passwordEncoder;



    public UserDTO findById(Long id) throws ResourceNotFoundException {

        User user = userRepository.findById(id)

                .orElseThrow(()-> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));

        UserDTO userDTO = new UserDTO();

        userDTO.setRoles(user.getRoles());

        return new UserDTO(user.getFirstName(), user.getLastName(), user.getPhoneNumber(), user.getEmail(),

                user.getAddress(), user.getZipCode(), user.getBuiltIn(), userDTO.getRoles());

    }
    public void register(User user) throws BadRequestException {
        if (userRepository.existsByEmail(user.getEmail())){
            throw new ConflictException("Error: Email is already in use!");
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        Set<Role> roles = new HashSet<>();
        Role customerRole = roleRepository.findByName(UserRole.ROLE_CUSTOMER)
                .orElseThrow(() -> new ResourceNotFoundException("Error: Role is not found."));
        roles.add(customerRole);
        user.setRoles(roles);
        userRepository.save(user);
    }
    public void login(String email, String password) throws AuthException{
        try{
            Optional<User>user=userRepository.findByEmail(email);
            if(!BCrypt.checkpw(password,user.get().getPassword()))
                throw new AuthException("invalid credentials");
        }catch (Exception e){
            throw new AuthException("invalid credentials");
        }
    }
    public void updateUser(Long id, UserDTO userDTO) throws BadRequestException{
        boolean emailExists=userRepository.existsByEmail(userDTO.getEmail());
        Optional<User>userDetails=userRepository.findById(id);
        if (userDetails.get().getBuiltIn()){
            throw new BadRequestException("You dont have permission to update user");
        }
        if (emailExists && ! userDTO.getEmail().equals(userDetails.get().getEmail())){
            throw new ConflictException("Error: Email is already in use");
        }
        userRepository.update(id, userDTO.getFirstName(), userDTO.getLastName(), userDTO.getPhoneNumber(),
                userDTO.getEmail(), userDTO.getAddress(), userDTO.getZipCode());

    }

    public void updatePassword(Long id, String newPassword, String oldPassword) throws BadRequestException{
        Optional<User>user=userRepository.findById(id);
        if(user.get().getBuiltIn()){
            throw new BadRequestException("You dont have permission to update password");
        }
        if(!BCrypt.hashpw(oldPassword, user.get().getPassword()).equals(user.get().getPassword()))
            throw new BadRequestException("Password does not match");
        String hashedPassword=passwordEncoder.encode(newPassword);
        user.get().setPassword(hashedPassword);
        userRepository.save(user.get());
    }
}