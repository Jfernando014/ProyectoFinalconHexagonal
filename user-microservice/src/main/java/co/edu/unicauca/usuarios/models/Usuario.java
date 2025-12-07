package co.edu.unicauca.usuarios.models;

import co.edu.unicauca.usuarios.models.enums.Rol;
import co.edu.unicauca.usuarios.models.enums.TipoDocente;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
public class Usuario {
    @Id
    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    private String nombres;
    private String apellidos;
    private String celular;
    private String programa;

    // Si el usuario tiene rol DOCENTE, este campo indica su tipo
    @Enumerated(EnumType.STRING)
    private TipoDocente tipoDocente;

    // Un usuario puede tener múltiples roles
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_email"))
    @Enumerated(EnumType.STRING)
    @Column(name = "rol")
    private Set<Rol> roles = new HashSet<>();
}