package com.dev.carrental_project.dto;

import com.dev.carrental_project.domain.Role;
import com.dev.carrental_project.domain.enumeration.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    //kullanicinin id ve pass gibi bilgilerini gormesini istemiyoruz.
    //id ve pass olmadan tum bilgilerini iceren class olusturup bunun uzerine calisiyoruz
    //DTO=data tranfert object


    public UserDTO(String firstName, String lastName, String phoneNumber,//pass ve id siz const
                   String email, String address, String zipCode, Boolean builtIn, Set<String> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.zipCode = zipCode;
        this.builtIn = builtIn;
        this.roles = roles;
    }
    //class entity table olusturmadigi icin column kullanilmaz
    @Size(max = 15)
    @NotNull(message = "Please enter your first name")
    private String firstName;

    @Size(max = 15)
    @NotNull(message = "Please enter your last name")
    private String lastName;

    @JsonIgnore
    private String password;

    @Pattern(regexp = "^((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$",
            message = "Please enter valid phone number")
    @Size(min = 14, max = 14)
    @NotNull(message = "Please enter your phone number")
    private String phoneNumber;
    @Email(message = "Please enter valid email")
    @Size(min = 5, max = 150)
    @NotNull(message = "Please enter your email")
    private String email;
    @Size(max = 250)
    @NotNull(message = "Please enter your address")
    private String address;
    @Size(max = 15)
    @NotNull(message = "Please enter your zip code")
    private String zipCode;
    private Boolean builtIn;//admin olarak bilgi eklendiginde, silinmesini istemiyorsa ileriki zamanlarda true diyoruz
    private Set<String> roles;

    public void setRoles(Set<Role> roles) {//???????????????
        Set<String> roles1 = new HashSet<>();
        Role[] role = roles.toArray(new Role[roles.size()]);
        for (int i = 0; i < roles.size(); i++){
            if (role[i].getName().equals(UserRole.ROLE_ADMIN))
                roles1.add("Administrator");
            else
                roles1.add("Customer");
        }
        this.roles = roles1;
    }
}