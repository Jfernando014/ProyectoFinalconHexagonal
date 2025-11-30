package co.edu.unicauca.mensajeria.services;

import co.edu.unicauca.mensajeria.models.MensajeInterno;
import co.edu.unicauca.mensajeria.repository.MensajeInternoRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MensajeInternoServiceImplTest {

    @Test
    void marcarComoLeido_trueCuandoExiste() {
        MensajeInternoRepository repo = mock(MensajeInternoRepository.class);
        MensajeInternoServiceImpl service = new MensajeInternoServiceImpl();

        // inyectar repo (@Autowired) por reflexión
        try {
            var f = MensajeInternoServiceImpl.class.getDeclaredField("mensajeRepository");
            f.setAccessible(true);
            f.set(service, repo);
        } catch (Exception e) {
            fail(e);
        }

        MensajeInterno msg = new MensajeInterno();
        msg.setId(1L);
        msg.setEstado("ENVIADO");

        when(repo.findById(1L)).thenReturn(Optional.of(msg));
        when(repo.save(any(MensajeInterno.class))).thenAnswer(i -> i.getArgument(0));

        boolean ok = service.marcarComoLeido(1L);

        assertTrue(ok);
        assertEquals("LEIDO", msg.getEstado());
        verify(repo).save(msg);
    }

    @Test
    void marcarComoLeido_falseCuandoNoExiste() {
        MensajeInternoRepository repo = mock(MensajeInternoRepository.class);
        MensajeInternoServiceImpl service = new MensajeInternoServiceImpl();
        try {
            var f = MensajeInternoServiceImpl.class.getDeclaredField("mensajeRepository");
            f.setAccessible(true);
            f.set(service, repo);
        } catch (Exception e) {
            fail(e);
        }

        when(repo.findById(7L)).thenReturn(Optional.empty());

        assertFalse(service.marcarComoLeido(7L));
        verify(repo, never()).save(any());
    }
}
