package com.handgrow.demo.config;

import com.handgrow.demo.entity.Role;
import com.handgrow.demo.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
    }

    private void initializeRoles() {
        createRoleIfNotExists("FARMER", "Farmer role for agricultural users");
        createRoleIfNotExists("COOP", "Cooperative role for cooperative organizations");
        createRoleIfNotExists("ENTERPRISE", "Enterprise role for business organizations");
        createRoleIfNotExists("ADMIN", "Administrator role with full access");
    }

    private void createRoleIfNotExists(String name, String description) {
        if (roleRepository.findByName(name).isEmpty()) {
            Role role = Role.builder()
                .name(name)
                .description(description)
                .isActive(true)
                .build();
            roleRepository.save(role);
        }
    }
}