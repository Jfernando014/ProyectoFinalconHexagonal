package co.edu.unicauca.proyectos.services;

import co.edu.unicauca.proyectos.models.ProyectoGrado;
import co.edu.unicauca.proyectos.repository.ProyectoRepository;
import co.edu.unicauca.proyectos.util.ProyectoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProyectoService implements IProyectoService {

    private final ProyectoRepository repo;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Override
    public ProyectoGrado crear(ProyectoGrado proyecto) {
        return proyectoRepository.save(proyecto);
    }

    @Override
    public ProyectoGrado obtenerPorId(Long id) {
        return proyectoRepository.findById(id)
                .orElseThrow(() -> new ProyectoNoEncontradoException("Proyecto no encontrado con ID: " + id));
    }

    @Override
    public List<ProyectoGrado> findByEstudiante1Email(String email) {
        return proyectoRepository.findByEstudiante1Email(email);
    }

    @Override
    public List<ProyectoGrado> findByDirectorEmail(String email) {
        return proyectoRepository.findByDirectorEmail(email);
    }

    @Override
    public List<ProyectoGrado> obtenerTodos() {
        return proyectoRepository.findAll();
    }

    @Override
    public ProyectoGrado guardar(ProyectoGrado proyecto) {
        return proyectoRepository.save(proyecto);
    }

    @Override
    public List<ProyectoGrado> findByAnteproyectoTokenIsNotNull() {
        return repo.findByAnteproyectoTokenIsNotNull();
    }
}
