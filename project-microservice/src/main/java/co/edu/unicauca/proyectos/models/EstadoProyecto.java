package co.edu.unicauca.proyectos.models;

public interface EstadoProyecto {
    void evaluar(ProyectoGrado proyecto, boolean aprobado, String observaciones);
    void reintentar(ProyectoGrado proyecto);
    String getNombreEstado();
}
