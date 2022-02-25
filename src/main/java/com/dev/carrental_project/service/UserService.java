package com.dev.carrental_project.service;

import com.dev.carrental_project.domain.Role;
import com.dev.carrental_project.domain.User;
import com.dev.carrental_project.domain.enumeration.UserRole;

import com.dev.carrental_project.dto.UserDTO;
import com.dev.carrental_project.exception.AuthException;
import com.dev.carrental_project.exception.BadRequestException;
import com.dev.carrental_project.exception.ConflictException;
import com.dev.carrental_project.exception.ResourceNotFoundException;
import com.dev.carrental_project.dto.AdminDTO;


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
    } public void updateUserAuth(Long id, AdminDTO adminDTO) throws  BadRequestException{

        boolean emailExist=userRepository.existsByEmail(adminDTO.getEmail());
        Optional<User>userDetails=userRepository.findById(id);
        if (userDetails.get().getBuiltIn()){
            throw new BadRequestException("You dont have permission to update user");
        }
        adminDTO.setBuiltIn(false);

        if (emailExist && ! adminDTO.getEmail().equals(userDetails.get().getEmail())){
            throw new ConflictException("Error: Email is already in use");
        }
        if (adminDTO.getPassword()==null){
            adminDTO.setPassword(userDetails.get().getPassword());
        }else{
            String encodedPassword=passwordEncoder.encode(adminDTO.getPassword());
            adminDTO.setPassword(encodedPassword);
        }
        Set<String>userRoles=adminDTO.getRoles();
        Set<Role>roles=addRoles(userRoles);

        User user=new User(id, adminDTO.getFirstName(), adminDTO.getLastName(), adminDTO.getPassword(),
                adminDTO.getPhoneNumber(), adminDTO.getEmail(), adminDTO.getAddress(), adminDTO.getZipCode(),
                adminDTO.getBuiltIn(),roles);
        userRepository.save(user);
    }

    public Set<Role> addRoles(Set<String> userRoles) {
        Set<Role>roles=new HashSet<>();
        if (userRoles==null){
            Role userRole=roleRepository.findByName(UserRole.ROLE_CUSTOMER)
                    .orElseThrow(()->new RuntimeException("Error: Role is not found"));
            roles.add(userRole);
        }else{
            userRoles.forEach(role->{
                switch (role){
                    case "Administrator":
                        Role adminRole=roleRepository.findByName(UserRole.ROLE_ADMIN)
                                .orElseThrow(()->new RuntimeException("Error: Role is not found"));
                        roles.add(adminRole);
                        break;
                    case "CustomerService":
                        Role customerService=roleRepository.findByName(UserRole.ROLE_CUSTOMER_SERVICE)
                                .orElseThrow(()->new RuntimeException("Error: Role is not found"));
                        roles.add(customerService);
                        break;
                    case "Manager":
                        Role manager=roleRepository.findByName(UserRole.ROLE_MANAGER)
                                .orElseThrow(()->new RuntimeException("Error: Role is not found"));
                        roles.add(manager);
                        break;
                    default:
                        Role customer=roleRepository.findByName(UserRole.ROLE_CUSTOMER)
                                .orElseThrow(()->new RuntimeException("Error: Role is not found"));
                        roles.add(customer);
                        break;
                }
            });
        }return roles;
    }
    public void removeById(Long id) throws ResourceNotFoundException{
        User user=userRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG,id)));
        if (user.getBuiltIn()){
            throw new BadRequestException("You dont have permission to delete this user!");
        }
        userRepository.deleteById(id);
    }
}