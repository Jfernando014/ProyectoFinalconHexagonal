package co.edu.unicauca.documentos.utilities.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PdfValidator implements DocumentoValidator {
    @Override
    public void validar(MultipartFile archivo) throws IllegalArgumentException {
        String nombre = archivo.getOriginalFilename();
        if (nombre == null || !nombre.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("El archivo debe ser un PDF.");
        }
    }
}