package com.handgrow.demo.config;

import com.handgrow.demo.entity.*;
import com.handgrow.demo.entity.enums.Produce;
import com.handgrow.demo.repository.*;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final CooperativeRepository cooperativeRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final FarmerRepository farmerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initializeRoles();
        if (accountRepository.count() == 0) {
            log.info("Seeding initial users and sample data...");
            seedUsers();
            log.info("Data seeding completed!");
        }
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
            log.info("Created role: {}", name);
        }
    }

    private void seedUsers() {
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
        Role coopRole = roleRepository.findByName("COOP").orElseThrow();
        Role enterpriseRole = roleRepository.findByName("ENTERPRISE").orElseThrow();
        Role farmerRole = roleRepository.findByName("FARMER").orElseThrow();

        // 1. Admin Account
        Account adminAccount = Account.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .role(adminRole)
                .isActive(true)
                .build();
        accountRepository.save(adminAccount);

        // 2. Cooperative Account & Entity
        Account coopAccount = Account.builder()
                .username("coop")
                .password(passwordEncoder.encode("coop123"))
                .role(coopRole)
                .isActive(true)
                .build();
        accountRepository.save(coopAccount);

        Cooperative coop = Cooperative.builder()
                .account(coopAccount)
                .name("HTX Nông Nghiệp Xanh")
                .phoneNumber("0912345678")
                .address("123 Đường Lúa, Huyện Mỹ Xuyên")
                .commune("Mỹ Xuyên")
                .province("Sóc Trăng")
                .produce(Produce.RICE)
                .representativeName("Nguyễn Văn A")
                .fundBalance(new BigDecimal("100000000.00"))
                .build();
        cooperativeRepository.save(coop);

        // 3. Enterprise Account & Entity
        Account enterpriseAccount = Account.builder()
                .username("enterprise")
                .password(passwordEncoder.encode("ent123"))
                .role(enterpriseRole)
                .isActive(true)
                .build();
        accountRepository.save(enterpriseAccount);

        Enterprise enterprise = Enterprise.builder()
                .account(enterpriseAccount)
                .companyName("Công Ty Xuất Khẩu Gạo Việt")
                .name("Việt Rice Corp")
                .phoneNumber("0987654321")
                .address("456 Đại Lộ Đông Tây, Quận 1")
                .province("TP. Hồ Chí Minh")
                .contactEmail("contact@vietrice.com")
                .representativeName("Trần Thị B")
                .build();
        enterpriseRepository.save(enterprise);

        // 4. Farmer Account & Entity
        Account farmerAccount = Account.builder()
                .username("farmer")
                .password(passwordEncoder.encode("farmer123"))
                .role(farmerRole)
                .isActive(true)
                .build();
        accountRepository.save(farmerAccount);

        Farmer farmer = Farmer.builder()
                .account(farmerAccount)
                .cooperative(coop)
                .fullName("Lê Văn C")
                .phoneNumber("0900112233")
                .address("789 Cánh Đồng, Xã Mỹ Xuyên")
                .commune("Mỹ Xuyên")
                .province("Sóc Trăng")
                .produce(Produce.RICE)
                .build();
        farmerRepository.save(farmer);
    }
}
