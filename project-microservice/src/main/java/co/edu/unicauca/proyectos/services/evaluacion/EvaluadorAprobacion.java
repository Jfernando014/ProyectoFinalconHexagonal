package co.edu.unicauca.proyectos.services.evaluacion;

import co.edu.unicauca.proyectos.models.ProyectoGrado;
import co.edu.unicauca.proyectos.models.estados.FormatoAAprobadoState;
import org.springframework.stereotype.Component;

@Component
public class EvaluadorAprobacion extends EvaluadorProyecto {
    @Override
    protected void aplicarEvaluacion(ProyectoGrado p, boolean aprobado, String observaciones) {
        p.setObservacionesEvaluacion(observaciones);
        p.setEstado(new FormatoAAprobadoState());
    }
}
