package br.com.dio.barber_shop_api.unitTests.common.sanitizer;

import br.com.dio.barber_shop_api.common.sanitizer.SanitizerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class SanitizerMapperTest {

    private SanitizerMapper sanitizerMapper;

    @BeforeEach
    void setUp() {
        sanitizerMapper = new SanitizerMapper();
    }

    /**
     * Deve remover scripts maliciosos da entrada HTML
     */
    @Test
    void sanitize_WhenInputHasScript_ShouldSanitize() {
        String input = "<script>alert('xss')</script><b>Texto</b>";
        String sanitized = sanitizerMapper.sanitize(input);

        assertFalse(sanitized.contains("script"));
        assertTrue(sanitized.contains("Texto"));
    }

    /**
     * Deve manter uma string limpa sem alterações
     */
    @Test
    void sanitize_WhenInputIsSafe_ShouldReturnSameContent() {
        String input = "João da Silva";
        String result = sanitizerMapper.sanitize(input);

        assertEquals("João da Silva", result);
    }

    /**
     * Deve retornar null quando a entrada for null
     */
    @Test
    void sanitize_WhenInputIsNull_ShouldReturnNull() {
        assertNull(sanitizerMapper.sanitize(null));
    }


}
