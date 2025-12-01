package co.edu.unicauca.client;

public class Session {
    private static String email;
    private static String rol;
    private static String token;

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        Session.email = email;
    }

    public static String getRol() {
        return rol;
    }

    public static void setRol(String rol) {
        Session.rol = rol;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Session.token = token;
    }

    public static void clear() {
        email = null;
        rol = null;
        token = null;
    }
}
