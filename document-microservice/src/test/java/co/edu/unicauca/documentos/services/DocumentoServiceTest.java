package co.edu.unicauca.documentos.services;

import co.edu.unicauca.documentos.dto.DocumentoRequest;
import co.edu.unicauca.documentos.models.Documento;
import co.edu.unicauca.documentos.repository.DocumentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentoServiceTest {

    @TempDir
    Path tmp;

    @Test
    void subirDocumento_pdfValido_guardaEnDiscoYPersistencia() throws Exception {
        DocumentoRepository repo = mock(DocumentoRepository.class);
        DocumentoService service = new DocumentoService();
        ReflectionTestUtils.setField(service, "documentoRepository", repo);
        ReflectionTestUtils.setField(service, "storageDir", tmp.toString());

        byte[] data = "contenido".getBytes();
        MockMultipartFile file = new MockMultipartFile("archivo", "formatoA.pdf",
                "application/pdf", data);

        DocumentoRequest req = new DocumentoRequest();
        req.setIdProyecto(10L);
        req.setTipoDocumento("FORMATO_A");
        req.setArchivo(file);

        when(repo.save(any(Documento.class))).thenAnswer(i -> {
            Documento d = i.getArgument(0);
            d.setId(1L);
            return d;
        });

        Documento out = service.subirDocumento(req);

        assertNotNull(out.getId());
        assertEquals(10L, out.getIdProyecto());
        assertEquals("FORMATO_A", out.getTipoDocumento());
        assertNotNull(out.getRutaArchivo());

        // Verificar archivo escrito
        Path written = Path.of(out.getRutaArchivo());
        assertTrue(Files.exists(written));
        assertArrayEquals(data, Files.readAllBytes(written));

        // Verificar persistencia
        ArgumentCaptor<Documento> cap = ArgumentCaptor.forClass(Documento.class);
        verify(repo).save(cap.capture());
        assertEquals("formatoA.pdf", cap.getValue().getNombreArchivo());
    }

    @Test
    void subirDocumento_noPdf_lanzaExcepcion() {
        DocumentoRepository repo = mock(DocumentoRepository.class);
        DocumentoService service = new DocumentoService();
        ReflectionTestUtils.setField(service, "documentoRepository", repo);
        ReflectionTestUtils.setField(service, "storageDir", tmp.toString());

        MockMultipartFile file = new MockMultipartFile("archivo", "no_pdf.txt",
                "text/plain", "x".getBytes());

        DocumentoRequest req = new DocumentoRequest();
        req.setIdProyecto(10L);
        req.setTipoDocumento("FORMATO_A");
        req.setArchivo(file);

        assertThrows(IllegalArgumentException.class, () -> service.subirDocumento(req));
        verify(repo, never()).save(any());
    }

    @Test
    void descargarDocumento_devuelveBytesDelArchivo() throws IOException, Exception {
        DocumentoRepository repo = mock(DocumentoRepository.class);
        DocumentoService service = new DocumentoService();
        ReflectionTestUtils.setField(service, "documentoRepository", repo);
        ReflectionTestUtils.setField(service, "storageDir", tmp.toString());

        Path f = tmp.resolve("test.pdf");
        byte[] data = "ABC".getBytes();
        Files.write(f, data);

        Documento d = new Documento();
        d.setId(99L);
        d.setRutaArchivo(f.toString());

        when(repo.findById(99L)).thenReturn(java.util.Optional.of(d));

        byte[] out = service.descargarDocumento(99L);

        assertArrayEquals(data, out);
    }
}
