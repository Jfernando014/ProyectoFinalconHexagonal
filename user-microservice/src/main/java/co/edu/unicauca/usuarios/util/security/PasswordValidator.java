package co.edu.unicauca.usuarios.util.security;

import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {
    public static boolean isValid(String password) {
        if (password == null || password.length() < 6) return false;
        boolean hasDigit = false, hasSpecial = false, hasUpper = false;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) hasDigit = true;
            if (!Character.isLetterOrDigit(c)) hasSpecial = true;
            if (Character.isUpperCase(c)) hasUpper = true;
        }
        return hasDigit && hasSpecial && hasUpper;
    }
}
