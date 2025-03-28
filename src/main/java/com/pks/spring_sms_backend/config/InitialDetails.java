package com.pks.spring_sms_backend.config;


import com.pks.spring_sms_backend.entity.Role;
import com.pks.spring_sms_backend.entity.User;
import com.pks.spring_sms_backend.enums.UserStatus;
import com.pks.spring_sms_backend.repository.CategoryRepository;
import com.pks.spring_sms_backend.repository.RoleRepository;
import com.pks.spring_sms_backend.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Log4j2
public class InitialDetails implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository; // Added CategoryRepository

    public InitialDetails(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.categoryRepository = categoryRepository; // Injected CategoryRepository
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Creating Roles");
        // Ensure roles exist
        if (roleRepository.count() == 0) {
            List<Role> roles = List.of(

                    Role.builder().roleName("admin").dateCreated(LocalDateTime.now()).build(),
                    Role.builder().roleName("teacher").dateCreated(LocalDateTime.now()).build(),
                    Role.builder().roleName("class-teacher").dateCreated(LocalDateTime.now()).build(),
                    Role.builder().roleName("account").dateCreated(LocalDateTime.now()).build(),
                    Role.builder().roleName("user").dateCreated(LocalDateTime.now()).build(),
                    Role.builder().roleName("guardian").dateCreated(LocalDateTime.now()).build(),
                    Role.builder().roleName("student").dateCreated(LocalDateTime.now()).build()
            );
            roleRepository.saveAll(roles);
            log.info("Saved Roles "+ roles);
        }

        // Ensure super admin exists
        final User userByEmail = userRepository.findByEmail("admin@gmail.com");
        log.info("finding if admin exist "+ userByEmail);
        if ( userByEmail== null) {
            Optional<Role> superAdminRole = roleRepository.findByRoleName("super-admin");
            log.info("finding if superAdminRole "+ superAdminRole);
            User superAdmin = User.builder()
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("password"))
                    .status(UserStatus.ACTIVE)
                    .dateCreated(LocalDateTime.now())
                    .dateUpdated(LocalDateTime.now())
                    .build();

            // Add role only if it exists
            superAdmin.addRole( Role.builder().roleName("super-admin").dateCreated(LocalDateTime.now()).build());
            userRepository.save(superAdmin);
        }
    }
}
