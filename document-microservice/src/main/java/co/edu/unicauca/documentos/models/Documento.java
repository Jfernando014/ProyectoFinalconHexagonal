package co.edu.unicauca.documentos.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "documentos")
@Data
@NoArgsConstructor
public class Documento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idProyecto;
    private String tipoDocumento; // FORMATO_A, CARTA_EMPRESA, ANTEPROYECTO
    private Integer version = 1;
    private String nombreArchivo;
    private String extension;
    private Long tamaño;
    private String rutaArchivo;
    private String estado; // PENDIENTE, APROBADO, RECHAZADO, OBSOLETO
}