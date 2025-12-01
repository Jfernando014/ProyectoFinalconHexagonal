package co.edu.unicauca.proyectos.models;

/**
 * Puerto de dominio (DDD) para el patrón State del proyecto de grado.
 * Cada implementación representa un estado del ciclo de vida del Formato A.
 */

public interface EstadoProyecto {
    void evaluar(ProyectoGrado proyecto, boolean aprobado, String observaciones);
    void reintentar(ProyectoGrado proyecto);
    String getNombreEstado();
}
