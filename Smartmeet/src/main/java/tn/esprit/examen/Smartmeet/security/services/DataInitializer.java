package tn.esprit.examen.Smartmeet.security.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tn.esprit.examen.Smartmeet.entities.Users.TypeUserRole;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;


import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Ensure roles exist
        Set<TypeUserRole> userRoles = new HashSet<>();

        // Adding roles to the set (using enum directly)    USER,ADMIN,PARTICIPANT,SPEAKER,TRAINER,SPONSOR,COMPANY
        userRoles.add(TypeUserRole.ADMIN);
        userRoles.add(TypeUserRole.PARTICIPANT);
        userRoles.add(TypeUserRole.SPEAKER);
        userRoles.add(TypeUserRole.TRAINER);
        userRoles.add(TypeUserRole.SPONSOR);
        userRoles.add(TypeUserRole.COMPANY);
        userRoles.add(TypeUserRole.USER);



        // Ensure admin user exists
        Optional<Users> existingAdmin = userRepository.findByUsername("salma123456");

        if (existingAdmin.isEmpty()) {
            Users adminUser = new Users();
            adminUser.setUsername("salma123456");
            adminUser.setEmail("salma123456@esprit.tn");
            adminUser.setPassword(passwordEncoder.encode("#Salma123456")); // Encrypt password
            // Add the admin role
            Set<TypeUserRole> roles = new HashSet<>();
            roles.add(TypeUserRole.ADMIN); // Set admin role here
            adminUser.setUserRole(roles); // Assuming setRoles accepts Set<TypeUserRole>
            adminUser.setEnabled(true);
            userRepository.save(adminUser);
            System.out.println("✅ Admin user created successfully!");
        } else {
            System.out.println("⚠️ Admin user already exists. Skipping creation.");
        }
    }

}

