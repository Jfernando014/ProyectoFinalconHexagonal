package co.edu.unicauca.proyectos.services.evaluacion;

import co.edu.unicauca.proyectos.models.ProyectoGrado;
import co.edu.unicauca.proyectos.models.estados.FormatoARechazadoState;
import org.springframework.stereotype.Component;

@Component
public class EvaluadorRechazo extends EvaluadorProyecto {
    @Override
    protected void aplicarEvaluacion(ProyectoGrado p, boolean aprobado, String observaciones) {
        p.setObservacionesEvaluacion(observaciones);
        p.setEstado(new FormatoARechazadoState());
    }
}
