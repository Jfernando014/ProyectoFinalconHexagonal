package co.edu.unicauca.mensajeria.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes_internos")
@Data
@NoArgsConstructor
public class MensajeInterno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String remitenteEmail; // Email del estudiante

    @Column(nullable = false)
    private String destinatariosEmail; // Emails separados por coma

    @Column(nullable = false)
    private String asunto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String cuerpo;

    @Lob
    private byte[] documentoAdjunto; // Opcional

    private String nombreArchivo;
    private LocalDateTime fechaEnvio;
    private String estado; // "ENVIADO", "LEIDO", "RESPONDIDO"
}