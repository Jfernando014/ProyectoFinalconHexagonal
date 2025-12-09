package co.edu.unicauca.usuarios.dto;

public class UsuarioDetalleDTO {

    private String nombres;
    private String apellidos;
    private String email;
    private String celular;
    private String rol;      // ESTUDIANTE, DOCENTE, COORDINADOR, JEFE_DEPARTAMENTO
    private String programa; // puede ser null si no aplica

    public UsuarioDetalleDTO() {
    }

    public UsuarioDetalleDTO(String nombres,
                             String apellidos,
                             String email,
                             String celular,
                             String rol,
                             String programa) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.celular = celular;
        this.rol = rol;
        this.programa = programa;
    }

    public String getNombres() {
        return nombres;
    }
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getCelular() {
        return celular;
    }
    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getRol() {
        return rol;
    }
    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getPrograma() {
        return programa;
    }
    public void setPrograma(String programa) {
        this.programa = programa;
    }
}
