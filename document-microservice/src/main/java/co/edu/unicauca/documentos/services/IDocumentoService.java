package co.edu.unicauca.documentos.services;

import java.util.List;
import co.edu.unicauca.documentos.models.Documento;
import co.edu.unicauca.documentos.dto.DocumentoRequest;

public interface IDocumentoService {
    Documento subirDocumento(DocumentoRequest request) throws Exception;
    List<Documento> obtenerDocumentosPorProyecto(Long idProyecto);
    List<Documento> obtenerDocumentosPorProyectoYTipo(Long idProyecto, String tipoDocumento);
    byte[] descargarDocumento(Long id) throws Exception;
}
