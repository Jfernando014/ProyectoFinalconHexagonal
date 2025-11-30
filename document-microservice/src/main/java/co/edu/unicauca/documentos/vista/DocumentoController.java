
package co.edu.unicauca.documentos.vista;

import co.edu.unicauca.documentos.models.Documento;
import co.edu.unicauca.documentos.dto.DocumentoRequest;
import co.edu.unicauca.documentos.services.IDocumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/documentos")
@Tag(name = "Gestión de Documentos", description = "API para subir y descargar documentos de proyectos de grado")
public class DocumentoController {

    @Autowired
    private IDocumentoService documentoService;

    @Operation(summary = "Subir un documento (retorna String token para Feign)")
    @PostMapping(value = "/subir", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> subirDocumento(
            @RequestParam("idProyecto") Long idProyecto,
            @RequestParam("tipoDocumento") String tipoDocumento,
            @RequestPart("archivo") MultipartFile archivo) {
        try {
            DocumentoRequest request = new DocumentoRequest();
            request.setIdProyecto(idProyecto);
            request.setTipoDocumento(tipoDocumento);
            request.setArchivo(archivo);

            Documento documento = documentoService.subirDocumento(request);

            // Retornar solo el ID como String (token)
            return ResponseEntity.ok(documento.getId().toString());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @Operation(summary = "Descargar un documento")
    @GetMapping("/descargar/{id}")
    public ResponseEntity<Resource> descargarDocumento(@PathVariable Long id) {
        try {
            byte[] contenido = documentoService.descargarDocumento(id);
            ByteArrayResource resource = new ByteArrayResource(contenido);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=documento.pdf")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Obtener documentos de un proyecto")
    @GetMapping("/proyecto/{idProyecto}")
    public ResponseEntity<List<Documento>> obtenerPorProyecto(@PathVariable Long idProyecto) {
        List<Documento> documentos = documentoService.obtenerDocumentosPorProyecto(idProyecto);
        return ResponseEntity.ok(documentos);
    }

    @Operation(summary = "Descargar plantilla del Formato A")
    @GetMapping("/plantilla/formato-a")
    public ResponseEntity<Resource> descargarPlantillaFormatoA() {
        try {
            Path path = Paths.get("src/main/resources/static/formatoA.doc");
            UrlResource resource = new UrlResource(path.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=formato_a_plantilla.doc")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}