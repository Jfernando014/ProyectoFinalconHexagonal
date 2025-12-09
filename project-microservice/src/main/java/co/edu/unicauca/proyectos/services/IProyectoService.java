package co.edu.unicauca.proyectos.services;

import co.edu.unicauca.proyectos.models.ProyectoGrado;
import java.util.List;

public interface IProyectoService {
    ProyectoGrado crear(ProyectoGrado proyecto);
    ProyectoGrado obtenerPorId(Long id);
    List<ProyectoGrado> findByEstudiante1Email(String email);
    List<ProyectoGrado> findByDirectorEmail(String email);
    List<ProyectoGrado> findByAnteproyectoTokenIsNotNull();
    List<ProyectoGrado> obtenerFormatoAPendientes();
    List<ProyectoGrado> obtenerTodos();
    ProyectoGrado guardar(ProyectoGrado proyecto);
}
