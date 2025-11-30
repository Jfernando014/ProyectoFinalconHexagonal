package co.edu.unicauca.proyectos.services;

import co.edu.unicauca.proyectos.repository.ProyectoRepository;
import co.edu.unicauca.proyectos.services.clients.UsuariosClient;
import co.edu.unicauca.proyectos.services.clients.DocumentosClient;
import co.edu.unicauca.proyectos.models.ProyectoGrado;
import co.edu.unicauca.proyectos.models.estados.EnPrimeraEvaluacionState;
import co.edu.unicauca.proyectos.dto.FormatoASubidoEvent;
import co.edu.unicauca.proyectos.dto.AnteproyectoSubidoEvent;
import co.edu.unicauca.proyectos.dto.EvaluacionFormatoAEvent;
import co.edu.unicauca.proyectos.services.evaluacion.EvaluadorAprobacion;
import co.edu.unicauca.proyectos.services.evaluacion.EvaluadorRechazo;
import co.edu.unicauca.proyectos.models.estados.FormatoAAprobadoState;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class ProyectoServiceFacade implements IProyectoServiceFacade {

    // Mensajería
    private static final String EXCHANGE = "notificaciones.exchange";
    private static final String RK_FORMATO_A_SUBIDO = "formatoA.subido";
    private static final String RK_ANTEPROYECTO_SUBIDO = "anteproyecto.subido";
    private static final String RK_FORMATO_A_EVALUADO = "formatoA.evaluado";
    private static final String RK_EVALUADORES_ASIGNADOS = "evaluadores.asignados";

    // Dependencias
    private final ProyectoRepository proyectoRepository;
    private final IProyectoService proyectoService;
    private final EvaluadorAprobacion evaluadorAprobacion;
    private final EvaluadorRechazo evaluadorRechazo;
    private final UsuariosClient userClient;
    private final DocumentosClient documentosClient;
    private final RabbitTemplate rabbitTemplate;

    public ProyectoServiceFacade(ProyectoRepository proyectoRepository,
                                 IProyectoService proyectoService,
                                 EvaluadorAprobacion evaluadorAprobacion,
                                 EvaluadorRechazo evaluadorRechazo,
                                 UsuariosClient userClient,
                                 DocumentosClient documentosClient,
                                 RabbitTemplate rabbitTemplate) {
        this.proyectoRepository = proyectoRepository;
        this.proyectoService = proyectoService;
        this.evaluadorAprobacion = evaluadorAprobacion;
        this.evaluadorRechazo = evaluadorRechazo;
        this.userClient = userClient;
        this.documentosClient = documentosClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public ResponseEntity<?> subirFormatoA(
            String titulo,
            String modalidad,
            String directorEmail,
            String codirectorEmail,
            String estudiante1Email,
            MultipartFile pdf,
            MultipartFile carta
    ){
        if ("PRACTICA_PROFESIONAL".equalsIgnoreCase(modalidad) && (carta == null || carta.isEmpty())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Se requiere carta de aceptación"));
        }

        String formatoTok = documentosClient.subir(0L, "FORMATO_A", pdf);
        String cartaTok = (carta != null && !carta.isEmpty())
                ? documentosClient.subir(0L, "CARTA_EMPRESA", carta) : null;

        ProyectoGrado p = new ProyectoGrado();
        p.setTitulo(titulo);
        p.setModalidad(modalidad);
        p.setDirectorEmail(directorEmail);
        p.setCodirectorEmail(codirectorEmail);
        p.setEstudiante1Email(estudiante1Email);
        p.setFormatoAToken(formatoTok);
        p.setCartaToken(cartaTok);
        p.setFechaFormatoA(java.time.LocalDate.now());

        p = proyectoRepository.save(p);

        // evento
        FormatoASubidoEvent ev = new FormatoASubidoEvent();
        ev.setIdProyecto(p.getId());
        ev.setTitulo(p.getTitulo());
        ev.setCoordinadorEmail("coordinador.sistemas@unicauca.edu.co");
        rabbitTemplate.convertAndSend(EXCHANGE, RK_FORMATO_A_SUBIDO, ev);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("idProyecto", p.getId());
        respuesta.put("formatoAToken", formatoTok);
        if (cartaTok != null) respuesta.put("cartaToken", cartaTok);
        return ResponseEntity.ok(respuesta);
    }

    private void validarUsuario(String email, String rolEsperado) {
        Map<String, Object> r = userClient.validarUsuario(email);
        Boolean existe = (Boolean) r.get("existe");
        String rol = (String) r.get("rol");
        if (!Boolean.TRUE.equals(existe)) throw new RuntimeException("El usuario " + email + " no existe.");
        if (!rolEsperado.equals(rol)) throw new RuntimeException("El usuario " + email + " no es " + rolEsperado + ".");
    }

    public ResponseEntity<?> asignarEvaluadores(
            Long idProyecto,
            String jefeDepartamentoEmail,
            String evaluador1Email,
            String evaluador2Email) {

        var p = proyectoService.obtenerPorId(idProyecto);
        if (p == null) return ResponseEntity.status(404).body(Map.of("error","Proyecto no encontrado"));

        validarUsuario(jefeDepartamentoEmail, "JEFE_DEPARTAMENTO");
        validarUsuario(evaluador1Email, "DOCENTE");
        validarUsuario(evaluador2Email, "DOCENTE");

        p.setEvaluador1Email(evaluador1Email);
        p.setEvaluador2Email(evaluador2Email);
        proyectoService.guardar(p);

        var ev = new co.edu.unicauca.proyectos.dto.AsignacionEvaluadoresEvent();
        ev.setIdProyecto(p.getId());
        ev.setTitulo(p.getTitulo());
        ev.setJefeDepartamentoEmail(jefeDepartamentoEmail);
        ev.setEvaluador1Email(evaluador1Email);
        ev.setEvaluador2Email(evaluador2Email);
        ev.setEstudianteEmail1(p.getEstudiante1Email());
        ev.setEstudianteEmail2(p.getEstudiante2Email());
        ev.setDirectorEmail(p.getDirectorEmail());
        ev.setCodirectorEmail(p.getCodirectorEmail());

        rabbitTemplate.convertAndSend(EXCHANGE, RK_EVALUADORES_ASIGNADOS, ev);

        return ResponseEntity.ok(Map.of(
                "idProyecto", p.getId(),
                "evaluador1", evaluador1Email,
                "evaluador2", evaluador2Email
        ));
    }


    @Override
    public ProyectoGrado crearProyecto(ProyectoGrado proyecto) {
        validarUsuario(proyecto.getDirectorEmail(), "DOCENTE");
        if (proyecto.getCodirectorEmail()!=null && !proyecto.getCodirectorEmail().isEmpty())
            validarUsuario(proyecto.getCodirectorEmail(), "DOCENTE");
        validarUsuario(proyecto.getEstudiante1Email(), "ESTUDIANTE");
        if (proyecto.getEstudiante2Email()!=null && !proyecto.getEstudiante2Email().isEmpty())
            validarUsuario(proyecto.getEstudiante2Email(), "ESTUDIANTE");

        // estado inicial como POJO, no bean
        proyecto.setEstado(new EnPrimeraEvaluacionState());
        ProyectoGrado guardado = proyectoService.crear(proyecto);

        FormatoASubidoEvent ev = new FormatoASubidoEvent();
        ev.setIdProyecto(guardado.getId());
        ev.setTitulo(guardado.getTitulo());
        ev.setCoordinadorEmail("coordinador.sistemas@unicauca.edu.co");
        rabbitTemplate.convertAndSend(EXCHANGE, RK_FORMATO_A_SUBIDO, ev);

        return guardado;
    }

    @Override
    @jakarta.transaction.Transactional
    public void evaluarProyecto(Long id, boolean aprobado, String observaciones) {
        ProyectoGrado p = proyectoService.obtenerPorId(id);
        if (p == null) return;

        if (aprobado) {
            evaluadorAprobacion.evaluarProyecto(p, true, observaciones);
        } else {
            evaluadorRechazo.evaluarProyecto(p, false, observaciones);
        }
        proyectoService.guardar(p);

        var ev = new EvaluacionFormatoAEvent();
        ev.setIdProyecto(p.getId());
        ev.setTitulo(p.getTitulo());
        ev.setAprobado(aprobado);
        ev.setObservaciones(observaciones);
        ev.setEstudianteEmail1(p.getEstudiante1Email());
        ev.setEstudianteEmail2(p.getEstudiante2Email());
        ev.setDirectorEmail(p.getDirectorEmail());
        ev.setCodirectorEmail(p.getCodirectorEmail());
        ev.setCoordinadorEmail("coordinador.sistemas@unicauca.edu.co");

        rabbitTemplate.convertAndSend(EXCHANGE, RK_FORMATO_A_EVALUADO, ev);
    }

    @Override
    public void reintentarProyecto(Long id) {
        ProyectoGrado p = proyectoService.obtenerPorId(id);
        p.reintentar();
        proyectoService.guardar(p);

        // re-notificar
        FormatoASubidoEvent ev = new FormatoASubidoEvent();
        ev.setIdProyecto(p.getId());
        ev.setTitulo(p.getTitulo());
        ev.setCoordinadorEmail("coordinador.sistemas@unicauca.edu.co");
        rabbitTemplate.convertAndSend(EXCHANGE, RK_FORMATO_A_SUBIDO, ev);
    }

    @Override
    public ResponseEntity<?> subirAnteproyecto(Long idProyecto, String jefeDepartamentoEmail, MultipartFile anteproyectoPdf) {
        try {
            ProyectoGrado p = proyectoService.obtenerPorId(idProyecto);
            if (p == null) return ResponseEntity.status(404).body(Map.of("error", "Proyecto no encontrado"));

            if (!(p.getEstado() instanceof FormatoAAprobadoState)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Solo se puede subir anteproyecto si el Formato A está aprobado.",
                        "estadoActual", p.getEstado().getClass().getSimpleName()
                ));
            }

            if (anteproyectoPdf == null || anteproyectoPdf.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El PDF del anteproyecto es obligatorio"));
            }

            validarUsuario(jefeDepartamentoEmail, "JEFE_DEPARTAMENTO");

            String anteproyectoToken = documentosClient.subir(idProyecto, "ANTEPROYECTO", anteproyectoPdf);

            p.setAnteproyectoToken(anteproyectoToken);
            proyectoService.guardar(p);

            AnteproyectoSubidoEvent ev = new AnteproyectoSubidoEvent();
            ev.setIdProyecto(p.getId());
            ev.setTitulo(p.getTitulo());
            ev.setJefeDepartamentoEmail(jefeDepartamentoEmail);
            ev.setEstudianteEmail(p.getEstudiante1Email());
            ev.setTutor1Email(p.getDirectorEmail());
            p.setFechaAnteproyecto(java.time.LocalDate.now());
            if (p.getCodirectorEmail() != null && !p.getCodirectorEmail().isEmpty()) {
                ev.setTutor2Email(p.getCodirectorEmail());
            }
            rabbitTemplate.convertAndSend(EXCHANGE, RK_ANTEPROYECTO_SUBIDO, ev);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("idProyecto", p.getId());
            respuesta.put("anteproyectoToken", anteproyectoToken);
            respuesta.put("mensaje", "Anteproyecto subido.");
            return ResponseEntity.ok(respuesta);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al subir anteproyecto: " + e.getMessage()));
        }
    }

    @Override
    public List<ProyectoGrado> obtenerProyectosPorEstudiante(String email) {
        return proyectoService.findByEstudiante1Email(email);
    }

    @Override
    public List<ProyectoGrado> obtenerAnteproyectosPorJefe(String emailJefe) {
        validarUsuario(emailJefe, "JEFE_DEPARTAMENTO");
        return proyectoService.findByAnteproyectoTokenIsNotNull();
    }

    @Override
    public ProyectoGrado obtenerProyectoPorId(Long id) {
        return proyectoService.obtenerPorId(id);
    }

    @Override
    public List<ProyectoGrado> obtenerTodosProyectos() {
        return proyectoService.obtenerTodos();
    }

    // Soporte directo a evaluaciones de estado interno
    @Transactional
    public ProyectoGrado evaluarFormatoA(Long idProyecto, boolean aprobado, String observaciones){
        ProyectoGrado p = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));
        p.evaluar(aprobado, observaciones);
        p.setObservacionesEvaluacion(observaciones);
        p = proyectoRepository.save(p);

        EvaluacionFormatoAEvent ev = new EvaluacionFormatoAEvent();
        ev.setIdProyecto(p.getId());
        ev.setTitulo(p.getTitulo());
        ev.setAprobado(aprobado);
        ev.setObservaciones(observaciones);
        ev.setEstudianteEmail1(p.getEstudiante1Email());
        ev.setEstudianteEmail2(p.getEstudiante2Email());
        ev.setDirectorEmail(p.getDirectorEmail());
        ev.setCodirectorEmail(p.getCodirectorEmail());
        ev.setCoordinadorEmail("coordinador.sistemas@unicauca.edu.co");
        rabbitTemplate.convertAndSend(EXCHANGE, RK_FORMATO_A_EVALUADO, ev);

        return p;
    }

    public ProyectoGrado buscarProyecto(Long id){
        return proyectoRepository.findById(id).orElseThrow();
    }
}
