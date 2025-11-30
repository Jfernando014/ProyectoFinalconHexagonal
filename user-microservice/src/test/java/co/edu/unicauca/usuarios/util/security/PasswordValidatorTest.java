package co.edu.unicauca.usuarios.util.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    @Test
    void validaPasswordCorrecta() {
        assertTrue(PasswordValidator.isValid("Abcd1!"));
        assertTrue(PasswordValidator.isValid("XyZ123$"));
    }

    @Test
    void rechazaMuyCorta() {
        assertFalse(PasswordValidator.isValid("A1!a"));
    }

    @Test
    void rechazaSinDigito() {
        assertFalse(PasswordValidator.isValid("Abcdef!"));
    }

    @Test
    void rechazaSinEspecial() {
        assertFalse(PasswordValidator.isValid("Abcdef1"));
    }

    @Test
    void rechazaSinMayuscula() {
        assertFalse(PasswordValidator.isValid("abcd1!"));
    }

    @Test
    void rechazaNull() {
        assertFalse(PasswordValidator.isValid(null));
    }
}
