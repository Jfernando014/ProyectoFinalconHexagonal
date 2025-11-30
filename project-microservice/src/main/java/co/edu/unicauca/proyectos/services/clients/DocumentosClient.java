package co.edu.unicauca.proyectos.services.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "document-ms", url = "${document.service.url}")
public interface DocumentosClient {
    
    @PostMapping(value = "/api/documentos/subir", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String subir(
        @RequestParam("idProyecto") Long idProyecto,
        @RequestParam("tipoDocumento") String tipoDocumento,
        @RequestPart("archivo") MultipartFile archivo
    );
}
