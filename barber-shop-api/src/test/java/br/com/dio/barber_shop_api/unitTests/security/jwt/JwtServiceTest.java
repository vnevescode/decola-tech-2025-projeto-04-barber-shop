package br.com.dio.barber_shop_api.unitTests.security.jwt;

import br.com.dio.barber_shop_api.entity.UserEntity;
import br.com.dio.barber_shop_api.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        user = UserEntity.builder()
                .id(java.util.UUID.randomUUID())
                .email("test@example.com")
                .password("superSecret")
                .role(UserEntity.Role.ROLE_USER)
                .build();
    }

    /**
     * Deve gerar um token JWT válido (string não vazia)
     */
    @Test
    void generateToken_ShouldReturnValidJwtToken() {
        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(token.startsWith("ey")); // Tokens JWT geralmente começam com 'ey...'
    }

    /**
     * Deve extrair o e-mail corretamente a partir de um token válido
     */
    @Test
    void extractUsername_ShouldReturnCorrectEmail() {
        String token = jwtService.generateToken(user);
        String extracted = jwtService.extractUsername(token);

        assertEquals(user.getEmail(), extracted);
    }

    /**
     * Deve manter o ciclo de gerar + extrair com consistência
     */
    @Test
    void tokenCycle_GenerateAndExtract_ShouldBeConsistent() {
        String token = jwtService.generateToken(user);
        String extractedEmail = jwtService.extractUsername(token);

        assertNotNull(token);
        assertEquals("test@example.com", extractedEmail);
    }
}
