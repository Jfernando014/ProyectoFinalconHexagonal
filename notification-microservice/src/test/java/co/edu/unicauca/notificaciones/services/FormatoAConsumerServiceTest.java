package co.edu.unicauca.notificaciones.services;

import co.edu.unicauca.notificaciones.dto.FormatoASubidoEvent;
import co.edu.unicauca.notificaciones.models.FormatoANotificacion;
import co.edu.unicauca.notificaciones.repository.FormatoANotificacionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FormatoAConsumerServiceTest {

    @Test
    void onMessage_mapeaYGuarda() throws Exception {
        FormatoANotificacionRepository repo = mock(FormatoANotificacionRepository.class);
        FormatoAConsumerService svc = new FormatoAConsumerService();

        // inyectar repo (@Autowired) por reflexión
        var f = FormatoAConsumerService.class.getDeclaredField("repo");
        f.setAccessible(true);
        f.set(svc, repo);

        FormatoASubidoEvent ev = new FormatoASubidoEvent();
        ev.setIdProyecto(123L);
        ev.setTitulo("Mi Formato A");
        ev.setCoordinadorEmail("coord@uni.edu.co");

        svc.onMessage(ev);

        ArgumentCaptor<FormatoANotificacion> cap = ArgumentCaptor.forClass(FormatoANotificacion.class);
        verify(repo).save(cap.capture());
        FormatoANotificacion saved = cap.getValue();

        assertEquals(123L, saved.getIdProyecto());
        assertEquals("Mi Formato A", saved.getTitulo());
        assertEquals("coord@uni.edu.co", saved.getCoordinadorEmail());
    }
}
