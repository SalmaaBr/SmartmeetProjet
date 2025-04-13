package tn.esprit.examen.Smartmeet.ResReq.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import tn.esprit.examen.Smartmeet.entities.Users.TypeUserRole;


import java.util.Set;

public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;


    private Set<TypeUserRole> roles;  // Remplacer Roles par TypeUserRole

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<TypeUserRole> getRoles() {  // Remplacer Roles par TypeUserRole
        return this.roles;
    }

    public void setRoles(Set<TypeUserRole> roles) {  // Ajouter un setter pour roles
        this.roles = roles;
    }
}


