package co.edu.unicauca.documentos.services;

import co.edu.unicauca.documentos.models.Documento;
import co.edu.unicauca.documentos.factorys.DocumentoFactory;
import co.edu.unicauca.documentos.dto.DocumentoRequest;
import co.edu.unicauca.documentos.repository.DocumentoRepository;
import co.edu.unicauca.documentos.utilities.validator.DocumentoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentoService implements IDocumentoService {

    @Autowired
    private DocumentoRepository documentoRepository;

    // Factory (Singleton según tu proyecto)
    private final DocumentoFactory documentoFactory = DocumentoFactory.getInstance();

    @Value("${app.document.storage-dir}")
    private String storageDir;

    @Override
    public Documento subirDocumento(DocumentoRequest request) throws Exception {
        MultipartFile archivo = request.getArchivo();
        String tipoDocumento = request.getTipoDocumento();
        Long idProyecto = request.getIdProyecto();

        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío.");
        }

        // --- FACTORY METHOD (validador) ---
        DocumentoValidator validator = documentoFactory.crearValidator(tipoDocumento);
        validator.validar(archivo);
        // ----------------------------------

        // Carpeta de storage
        Path dirPath = Paths.get(storageDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // Nombre original y extensión (con punto, como lo venías usando)
        String originalFilename = archivo.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.lastIndexOf(".") > 0) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")); // e.g. ".pdf"
        }

        // Nombre físico único para disco
        String nombreUnico = UUID.randomUUID() + extension;
        Path filePath = dirPath.resolve(nombreUnico);

        // Guardar en disco
        try {
            archivo.transferTo(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo en disco.", e);
        }

        // --- FACTORY METHOD (entidad Documento) ---
        // Si no hay nombre original, usamos un fallback manteniendo la extensión calculada
        String nombreParaEntidad = (originalFilename != null && !originalFilename.isBlank())
                ? originalFilename
                : ("archivo" + extension);

        Documento documento = documentoFactory.nuevo(
                idProyecto,
                tipoDocumento,
                nombreParaEntidad,
                archivo.getSize(),
                filePath
        );
        // ------------------------------------------

        return documentoRepository.save(documento);
    }

    @Override
    public List<Documento> obtenerDocumentosPorProyecto(Long idProyecto) {
        return documentoRepository.findByIdProyecto(idProyecto);
    }

    @Override
    public List<Documento> obtenerDocumentosPorProyectoYTipo(Long idProyecto, String tipoDocumento) {
        return documentoRepository.findByIdProyectoAndTipoDocumento(idProyecto, tipoDocumento);
    }

    @Override
    public byte[] descargarDocumento(Long id) throws Exception {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con ID: " + id));
        return Files.readAllBytes(Paths.get(documento.getRutaArchivo()));
    }
}
