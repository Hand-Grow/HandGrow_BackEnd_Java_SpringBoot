package com.handgrow.demo.config;

import com.handgrow.demo.entity.*;
import com.handgrow.demo.entity.enums.Produce;
import com.handgrow.demo.entity.enums.ProductCategory;
import com.handgrow.demo.repository.*;
import java.math.BigDecimal;
import java.time.LocalDate;
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
    private final PlotRepository plotRepository;
    private final ProductRepository productRepository;
    private final CoopAnnouncementRepository announcementRepository;
    private final CollectionCampaignRepository campaignRepository;
    private final SourcingRequestRepository sourcingRequestRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initializeRoles();
        if (accountRepository.count() == 0) {
            log.info("Seeding realistic users and sample data...");
            seedData();
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

    private void seedData() {
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
        Role coopRole = roleRepository.findByName("COOP").orElseThrow();
        Role enterpriseRole = roleRepository.findByName("ENTERPRISE").orElseThrow();
        Role farmerRole = roleRepository.findByName("FARMER").orElseThrow();

        // 1. Admin Account
        Account adminAccount = accountRepository.save(Account.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .role(adminRole)
                .isActive(true)
                .build());

        // 2. Cooperatives
        Account coopAccount1 = accountRepository.save(Account.builder()
                .username("coop_xanh")
                .password(passwordEncoder.encode("coop123"))
                .role(coopRole)
                .isActive(true)
                .build());

        Cooperative coop1 = cooperativeRepository.save(Cooperative.builder()
                .account(coopAccount1)
                .name("HTX Nông Nghiệp Xanh")
                .phoneNumber("0912345678")
                .address("123 Đường Lúa, Huyện Mỹ Xuyên")
                .commune("Mỹ Xuyên")
                .province("Sóc Trăng")
                .produce(Produce.RICE)
                .representativeName("Nguyễn Văn A")
                .fundBalance(new BigDecimal("150000000.00"))
                .build());

        Account coopAccount2 = accountRepository.save(Account.builder()
                .username("coop_thinhvuong")
                .password(passwordEncoder.encode("coop123"))
                .role(coopRole)
                .isActive(true)
                .build());

        Cooperative coop2 = cooperativeRepository.save(Cooperative.builder()
                .account(coopAccount2)
                .name("HTX Nông Nghiệp Thịnh Vượng")
                .phoneNumber("0988776655")
                .address("Thôn 4, Xã An Cư")
                .commune("An Cư")
                .province("Phú Yên")
                .produce(Produce.RICE)
                .representativeName("Trần Bình Trọng")
                .fundBalance(new BigDecimal("200000000.00"))
                .build());

        // 3. Enterprises
        Account entAccount1 = accountRepository.save(Account.builder()
                .username("ent_vietrice")
                .password(passwordEncoder.encode("ent123"))
                .role(enterpriseRole)
                .isActive(true)
                .build());

        Enterprise ent1 = enterpriseRepository.save(Enterprise.builder()
                .account(entAccount1)
                .companyName("Công Ty Xuất Khẩu Gạo Việt")
                .name("Việt Rice Corp")
                .phoneNumber("0987654321")
                .address("456 Đại Lộ Đông Tây, Quận 1")
                .province("TP. Hồ Chí Minh")
                .contactEmail("contact@vietrice.com")
                .representativeName("Trần Thị B")
                .build());

        Account entAccount2 = accountRepository.save(Account.builder()
                .username("ent_agrifood")
                .password(passwordEncoder.encode("ent123"))
                .role(enterpriseRole)
                .isActive(true)
                .build());

        Enterprise ent2 = enterpriseRepository.save(Enterprise.builder()
                .account(entAccount2)
                .companyName("Công ty CP Thực Phẩm Agrifood")
                .name("Agrifood VN")
                .phoneNumber("0909888777")
                .address("KCN Nam Tân Uyên, Bình Dương")
                .province("Bình Dương")
                .contactEmail("info@agrifood.vn")
                .representativeName("Lê Hoàng C")
                .build());

        // 4. Farmers for Coop 1
        Farmer farmer1 = createFarmer("farmer1", "Lê Văn Lúa", coop1, farmerRole);
        Farmer farmer2 = createFarmer("farmer2", "Nguyễn Thị Thơm", coop1, farmerRole);
        Farmer farmer3 = createFarmer("farmer3", "Trần Hữu Cơ", coop1, farmerRole);

        // Farmers for Coop 2
        Farmer farmer4 = createFarmer("farmer4", "Phạm Trọng Mùa", coop2, farmerRole);
        Farmer farmer5 = createFarmer("farmer5", "Võ Thị Màng", coop2, farmerRole);

        // 5. Plots for Farmers
        createPlot(farmer1, "Thửa ruộng A1", 5000.0);
        createPlot(farmer1, "Thửa ruộng A2", 3500.0);
        createPlot(farmer2, "Thửa ruộng B1", 4200.0);
        createPlot(farmer3, "Thửa ruộng C1", 6000.0);
        createPlot(farmer4, "Thửa ruộng D1", 4500.0);

        // 6. Products for Enterprises
        createProduct(
                ent1,
                "Phân bón NPK 20-20-15",
                "Phân bón chất lượng cao cho lúa",
                new BigDecimal("15000"),
                "Bao 50kg",
                ProductCategory.FERTILIZER);
        createProduct(
                ent2,
                "Giống lúa ST25",
                "Giống lúa thơm năng suất cao",
                new BigDecimal("25000"),
                "Bao 10kg",
                ProductCategory.SEED);

        // 7. Announcements from Coops
        announcementRepository.save(CoopAnnouncement.builder()
                .cooperative(coop1)
                .title("Thông báo họp thường kỳ tháng 10")
                .content(
                        "Mời toàn thể bà con xã viên có mặt tại nhà văn hóa lúc 8h sáng Chủ Nhật để bàn về kế hoạch gieo sạ vụ Đông Xuân.")
                .build());

        announcementRepository.save(CoopAnnouncement.builder()
                .cooperative(coop1)
                .title("Cập nhật giá phân bón mới")
                .content("HTX vừa nhập về lô phân bón hữu cơ giá rẻ, bà con đăng ký nhận phân bón trước ngày 15 nhé.")
                .build());

        // 8. Collection Campaigns
        campaignRepository.save(CollectionCampaign.builder()
                .cooperative(coop1)
                .title("Thu mua lúa ST25 vụ Hè Thu")
                .content("HTX cần thu mua 50 tấn lúa ST25 từ bà con. Đảm bảo độ ẩm < 15%.")
                .productName("Lúa ST25")
                .expectedDate(LocalDate.now().plusDays(10))
                .build());

        campaignRepository.save(CollectionCampaign.builder()
                .cooperative(coop2)
                .title("Gom gạo OM5451 xuất khẩu")
                .content("Cần gom gấp 100 tấn OM5451 để giao cho đối tác Agrifood.")
                .productName("Gạo OM5451")
                .expectedDate(LocalDate.now().plusDays(5))
                .build());

        // 9. Sourcing Requests from Enterprises
        sourcingRequestRepository.save(SourcingRequest.builder()
                .enterprise(entAccount1)
                .productName("ST25 chuẩn GlobalGAP")
                .requirements("Cần thu mua ổn định 20 tấn mỗi tháng để xuất khẩu đi EU.")
                .quantity(20.0)
                .unit("Tấn")
                .expectedPrice(new BigDecimal("22000"))
                .deadline(LocalDate.now().plusDays(30))
                .status(SourcingRequest.SourcingRequestStatus.OPEN)
                .build());

        sourcingRequestRepository.save(SourcingRequest.builder()
                .enterprise(entAccount2)
                .productName("Đài Thơm 8 số lượng lớn")
                .requirements("Thu mua cho nhà máy chế biến thực phẩm.")
                .quantity(100.0)
                .unit("Tấn")
                .expectedPrice(new BigDecimal("16000"))
                .deadline(LocalDate.now().plusDays(15))
                .status(SourcingRequest.SourcingRequestStatus.OPEN)
                .build());
    }

    private Farmer createFarmer(String username, String fullName, Cooperative coop, Role farmerRole) {
        Account account = accountRepository.save(Account.builder()
                .username(username)
                .password(passwordEncoder.encode(username + "123"))
                .role(farmerRole)
                .isActive(true)
                .build());

        return farmerRepository.save(Farmer.builder()
                .account(account)
                .cooperative(coop)
                .fullName(fullName)
                .phoneNumber("09" + (int) (Math.random() * 100000000))
                .address("Thôn X, Xã Y")
                .commune("Mỹ Xuyên")
                .province("Sóc Trăng")
                .produce(Produce.RICE)
                .build());
    }

    private void createPlot(Farmer farmer, String name, Double area) {
        plotRepository.save(Plot.builder()
                .farmer(farmer)
                .name(name)
                .area(area)
                .areaUnit("m2")
                .location("Tọa độ X,Y")
                .build());
    }

    private void createProduct(
            Enterprise enterprise, String name, String desc, BigDecimal price, String unit, ProductCategory category) {
        productRepository.save(Product.builder()
                .enterprise(enterprise)
                .name(name)
                .description(desc)
                .basePrice(price)
                .unit(unit)
                .category(category)
                .build());
    }
}
