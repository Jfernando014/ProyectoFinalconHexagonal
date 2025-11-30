package co.edu.unicauca.proyectos.services.evaluacion;

import co.edu.unicauca.proyectos.models.ProyectoGrado;

/** Strategy base sin dependencias ni anotaciones Spring. */
public abstract class EvaluadorProyecto {

    public final void evaluarProyecto(ProyectoGrado proyecto, boolean aprobado, String observaciones) {
        aplicarEvaluacion(proyecto, aprobado, observaciones);
    }

    protected abstract void aplicarEvaluacion(ProyectoGrado proyecto, boolean aprobado, String observaciones);
}
