package co.edu.unicauca.documentos.utilities.validator;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentoValidator {
    void validar(MultipartFile archivo) throws IllegalArgumentException;
}
