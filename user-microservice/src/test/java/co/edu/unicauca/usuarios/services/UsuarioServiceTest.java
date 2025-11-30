package co.edu.unicauca.usuarios.services;

import co.edu.unicauca.usuarios.models.Docente;
import co.edu.unicauca.usuarios.models.Usuario;
import co.edu.unicauca.usuarios.repository.UsuarioRepository;
import co.edu.unicauca.usuarios.util.InvalidUserDataException;
import co.edu.unicauca.usuarios.util.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    private UsuarioRepository repo;
    private UsuarioService service;

    @BeforeEach
    void setUp() {
        repo = mock(UsuarioRepository.class);
        service = new UsuarioService(repo);
    }

    @Test
    void registraDocente_ok_normalizaCampos() throws Exception {
        Docente d = new Docente();
        d.setEmail("docente@unicauca.edu.co"); // dominio válido
        d.setPassword("Abcdef1!");
        d.setNombres("  Ana  ");
        d.setApellidos("  Pérez ");
        d.setPrograma("  Sistemas ");
        d.setCelular("  3001234567 ");

        when(repo.existsById(d.getEmail())).thenReturn(false);
        when(repo.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario out = service.registrarDocente(d);

        assertEquals("docente@unicauca.edu.co", out.getEmail());
        ArgumentCaptor<Usuario> cap = ArgumentCaptor.forClass(Usuario.class);
        verify(repo).save(cap.capture());
        Usuario persistido = cap.getValue();
        assertEquals("Ana", persistido.getNombres());
        assertEquals("Pérez", persistido.getApellidos());
        assertEquals("Sistemas", persistido.getPrograma());
        assertEquals("3001234567", persistido.getCelular());
    }

    @Test
    void registraDocente_duplicado_lanzaExcepcion() {
        Docente d = new Docente();
        d.setEmail("doc@unicauca.edu.co"); // dominio válido para llegar a la rama de duplicado
        d.setPassword("Abcdef1!");

        when(repo.existsById(d.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> service.registrarDocente(d));
        verify(repo, never()).save(any());
    }

    @Test
    void registraDocente_passwordInvalida_lanzaExcepcion() {
        Docente d = new Docente();
        d.setEmail("doc@unicauca.edu.co"); // dominio válido
        d.setPassword("abcdef"); // sin mayúscula, sin especial

        when(repo.existsById(d.getEmail())).thenReturn(false);

        assertThrows(InvalidUserDataException.class, () -> service.registrarDocente(d));
        verify(repo, never()).save(any());
    }

    @Test
    void existeUsuario_delegaEnRepositorio() {
        when(repo.existsById("a@unicauca.edu.co")).thenReturn(true);
        assertTrue(service.existeUsuario("a@unicauca.edu.co"));
        verify(repo).existsById("a@unicauca.edu.co");
    }

    @Test
    void obtenerPorEmail_invalido_lanzaExcepcion() {
        assertThrows(InvalidUserDataException.class, () -> service.obtenerPorEmail(" "));
        assertThrows(InvalidUserDataException.class, () -> service.obtenerPorEmail(null));
    }

    // Extra: documenta regla de dominio en un test dedicado
    @Test
    void registraDocente_emailDominioInvalido_lanzaExcepcion() {
        Docente d = new Docente();
        d.setEmail("doc@otro.edu.co"); // dominio inválido
        d.setPassword("Abcdef1!");

        assertThrows(InvalidUserDataException.class, () -> service.registrarDocente(d));
        verify(repo, never()).save(any());
    }
}
