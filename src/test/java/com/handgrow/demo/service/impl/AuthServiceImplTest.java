package com.handgrow.demo.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.handgrow.demo.dto.request.*;
import com.handgrow.demo.dto.response.AuthResponse;
import com.handgrow.demo.entity.*;
import com.handgrow.demo.entity.enums.EnterpriseType;
import com.handgrow.demo.repository.*;
import com.handgrow.demo.util.JwtUtil;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private FarmerRepository farmerRepository;

    @Mock
    private CooperativeRepository cooperativeRepository;

    @Mock
    private EnterpriseRepository enterpriseRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private Account testAccount;
    private Role testRole;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setName("FARMER");

        testAccount = new Account();
        testAccount.setUsername("testuser");
        testAccount.setPassword("encodedPassword");
        testAccount.setRole(testRole);
        testAccount.setActive(true);

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
    }

    @Test
    void login_Success() {
        // Given
        when(accountRepository.findByUsername("testuser")).thenReturn(Optional.of(testAccount));
        when(jwtUtil.generateToken("testuser", "FARMER")).thenReturn("mock-jwt-token");

        // When
        AuthResponse response = authService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(86400000L, response.getExpiresIn());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(accountRepository).findByUsername("testuser");
        verify(jwtUtil).generateToken("testuser", "FARMER");
    }

    @Test
    void login_UserNotFound() {
        // Given
        when(accountRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
        assertEquals("User not found", exception.getMessage());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(accountRepository).findByUsername("testuser");
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void registerFarmer_Success() {
        // Given
        FarmerRegisterRequest request = new FarmerRegisterRequest();
        request.setUsername("newfarmer");
        request.setPassword("password123");
        request.setFullName("John Farmer");
        request.setPhoneNumber("0987654321");
        request.setAddress("Farm Address");

        Farmer savedFarmer = new Farmer();
        savedFarmer.setAccount(testAccount);
        savedFarmer.setFullName("John Farmer");
        savedFarmer.setPhoneNumber("0987654321");
        savedFarmer.setAddress("Farm Address");

        when(accountRepository.findByUsername("newfarmer")).thenReturn(Optional.empty());
        when(roleRepository.findByName("FARMER")).thenReturn(Optional.of(testRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(farmerRepository.save(any(Farmer.class))).thenReturn(savedFarmer);
        when(jwtUtil.generateToken("newfarmer", "FARMER")).thenReturn("mock-jwt-token");

        // When
        AuthResponse response = authService.registerFarmer(request);

        // Then
        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());

        verify(accountRepository).findByUsername("newfarmer");
        verify(roleRepository).findByName("FARMER");
        verify(passwordEncoder).encode("password123");
        verify(farmerRepository).save(any(Farmer.class));
        verify(jwtUtil).generateToken("newfarmer", "FARMER");
    }

    @Test
    void registerFarmer_UsernameExists() {
        // Given
        FarmerRegisterRequest request = new FarmerRegisterRequest();
        request.setUsername("existinguser");

        when(accountRepository.findByUsername("existinguser")).thenReturn(Optional.of(testAccount));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.registerFarmer(request));
        assertEquals("Username already exists", exception.getMessage());

        verify(accountRepository).findByUsername("existinguser");
        verifyNoInteractions(roleRepository, passwordEncoder, farmerRepository, jwtUtil);
    }

    @Test
    void registerCoop_Success() {
        // Given
        CoopRegisterRequest request = new CoopRegisterRequest();
        request.setUsername("newcoop");
        request.setPassword("password123");
        request.setName("Green Coop");
        request.setAddress("Coop Address");
        request.setRepresentativeName("Jane Cooper");

        Role coopRole = new Role();
        coopRole.setName("COOP");
        Cooperative savedCoop = new Cooperative();
        savedCoop.setName("Green Coop");

        when(accountRepository.findByUsername("newcoop")).thenReturn(Optional.empty());
        when(roleRepository.findByName("COOP")).thenReturn(Optional.of(coopRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(cooperativeRepository.save(any(Cooperative.class))).thenReturn(savedCoop);
        when(jwtUtil.generateToken("newcoop", "COOP")).thenReturn("mock-jwt-token");

        // When
        AuthResponse response = authService.registerCoop(request);

        // Then
        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getAccessToken());

        verify(cooperativeRepository).save(any(Cooperative.class));
        verify(jwtUtil).generateToken("newcoop", "COOP");
    }

    @Test
    void registerEnterprise_Success() {
        // Given
        EnterpriseRegisterRequest request = new EnterpriseRegisterRequest();
        request.setUsername("newenterprise");
        request.setPassword("password123");
        request.setCompanyName("Tech Corp");
        request.setTaxCode("TAX123");
        request.setBusinessType(EnterpriseType.BUYER);
        request.setContactEmail("contact@tech.com");
        request.setWebsiteUrl("https://tech.com");
        request.setAddress("Tech Address");
        request.setEnterpriseType(EnterpriseType.SUPPLIER);
        request.setName("Tech");
        request.setPhoneNumber("0901234567");
        request.setRepresentativeName("Bob Tech");

        Role enterpriseRole = new Role();
        enterpriseRole.setName("ENTERPRISE");
        Enterprise savedEnterprise = new Enterprise();
        savedEnterprise.setCompanyName("Tech Corp");

        when(accountRepository.findByUsername("newenterprise")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ENTERPRISE")).thenReturn(Optional.of(enterpriseRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(enterpriseRepository.save(any(Enterprise.class))).thenReturn(savedEnterprise);
        when(jwtUtil.generateToken("newenterprise", "ENTERPRISE")).thenReturn("mock-jwt-token");

        // When
        AuthResponse response = authService.registerEnterprise(request);

        // Then
        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getAccessToken());

        verify(enterpriseRepository).save(any(Enterprise.class));
        verify(jwtUtil).generateToken("newenterprise", "ENTERPRISE");
    }

    @Test
    void registerFarmer_RoleNotFound() {
        // Given
        FarmerRegisterRequest request = new FarmerRegisterRequest();
        request.setUsername("newfarmer");

        when(accountRepository.findByUsername("newfarmer")).thenReturn(Optional.empty());
        when(roleRepository.findByName("FARMER")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.registerFarmer(request));
        assertEquals("Role not found", exception.getMessage());

        verify(roleRepository).findByName("FARMER");
        verifyNoInteractions(passwordEncoder, farmerRepository, jwtUtil);
    }
}
