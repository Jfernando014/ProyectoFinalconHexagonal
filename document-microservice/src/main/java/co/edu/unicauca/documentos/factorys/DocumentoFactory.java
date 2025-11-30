package co.edu.unicauca.documentos.factorys;

import co.edu.unicauca.documentos.utilities.validator.DocumentoValidator;
import co.edu.unicauca.documentos.utilities.validator.PdfValidator;

import co.edu.unicauca.documentos.models.Documento;
import co.edu.unicauca.documentos.utilities.validator.DocumentoValidator;
import co.edu.unicauca.documentos.utilities.validator.PdfValidator;

import java.nio.file.Path;

public class DocumentoFactory {

    private static volatile DocumentoFactory instance;

    private DocumentoFactory() {}

    public static DocumentoFactory getInstance() {
        if (instance == null) {
            synchronized (DocumentoFactory.class) {
                if (instance == null) {
                    instance = new DocumentoFactory();
                }
            }
        }
        return instance;
    }

    /** Factory Method para seleccionar validador según tipo */
    public DocumentoValidator crearValidator(String tipoDocumento) {
        switch (tipoDocumento) {
            case "FORMATO_A":
            case "ANTEPROYECTO":
            case "CARTA_EMPRESA":
            case "MONOGRAFIA":
            case "ANEXOS":
            case "PRESENTACION":
                return new PdfValidator();
            default:
                throw new IllegalArgumentException("Tipo de documento no soportado: " + tipoDocumento);
        }
    }

    /** Factory Method para construir la entidad Documento de forma consistente */
    public Documento nuevo(Long idProyecto,
                           String tipoDocumento,
                           String nombreOriginal,
                           long sizeBytes,
                           Path filePath) {

        if (nombreOriginal == null || nombreOriginal.isBlank()) {
            throw new IllegalArgumentException("Nombre de archivo inválido.");
        }
        String lower = nombreOriginal.toLowerCase();
        String extension = lower.contains(".") ? lower.substring(lower.lastIndexOf('.') + 1) : "";

        Documento d = new Documento();
        d.setIdProyecto(idProyecto);
        d.setTipoDocumento(tipoDocumento);
        d.setVersion(1);                  // versión inicial
        d.setNombreArchivo(nombreOriginal);
        d.setExtension(extension);
        d.setTamaño(sizeBytes);           // (tu entidad usa 'tamaño' con ñ)
        d.setRutaArchivo(filePath.toString());
        d.setEstado("PENDIENTE");
        return d;
    }
}