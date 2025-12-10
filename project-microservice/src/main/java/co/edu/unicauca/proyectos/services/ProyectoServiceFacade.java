package co.edu.unicauca.proyectos.services;

import co.edu.unicauca.proyectos.repository.ProyectoRepository;
import co.edu.unicauca.proyectos.services.clients.UsuariosClient;
import co.edu.unicauca.proyectos.services.clients.DocumentosClient;
import co.edu.unicauca.proyectos.models.ProyectoGrado;
import co.edu.unicauca.proyectos.models.estados.EnPrimeraEvaluacionState;
import co.edu.unicauca.proyectos.dto.FormatoASubidoEvent;
import co.edu.unicauca.proyectos.dto.AnteproyectoSubidoEvent;
import co.edu.unicauca.proyectos.dto.EvaluacionFormatoAEvent;
import co.edu.unicauca.proyectos.dto.AsignacionEvaluadoresEvent;
import co.edu.unicauca.proyectos.services.evaluacion.EvaluadorAprobacion;
import co.edu.unicauca.proyectos.services.evaluacion.EvaluadorRechazo;
import co.edu.unicauca.proyectos.models.estados.FormatoAAprobadoState;
import co.edu.unicauca.proyectos.dto.FormatoAInfoDTO;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class ProyectoServiceFacade implements IProyectoServiceFacade {

    // Dependencias
    private final ProyectoRepository proyectoRepository;
    private final IProyectoService proyectoService;
    private final EvaluadorAprobacion evaluadorAprobacion;
    private final EvaluadorRechazo evaluadorRechazo;
    private final UsuariosClient userClient;
    private final DocumentosClient documentosClient;
    private final INotificacionesClient notificacionesClient;

    public ProyectoServiceFacade(ProyectoRepository proyectoRepository,
                                 IProyectoService proyectoService,
                                 EvaluadorAprobacion evaluadorAprobacion,
                                 EvaluadorRechazo evaluadorRechazo,
                                 UsuariosClient userClient,
                                 DocumentosClient documentosClient,
                                 INotificacionesClient notificacionesClient) {
        this.proyectoRepository = proyectoRepository;
        this.proyectoService = proyectoService;
        this.evaluadorAprobacion = evaluadorAprobacion;
        this.evaluadorRechazo = evaluadorRechazo;
        this.userClient = userClient;
        this.documentosClient = documentosClient;
        this.notificacionesClient = notificacionesClient;
    }

    public ResponseEntity<?> subirFormatoA(
            String titulo,
            String modalidad,
            String directorEmail,
            String codirectorEmail,
            String estudiante1Email,
            MultipartFile pdf,
            MultipartFile carta
    ){
        if ("PRACTICA_PROFESIONAL".equalsIgnoreCase(modalidad)
                && (carta == null || carta.isEmpty())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Se requiere carta de aceptación"));
        }

        ProyectoGrado p = new ProyectoGrado();
        p.setTitulo(titulo);
        p.setModalidad(modalidad);
        p.setDirectorEmail(directorEmail);
        p.setCodirectorEmail(codirectorEmail);
        p.setEstudiante1Email(estudiante1Email);
        p.setFechaFormatoA(java.time.LocalDate.now());
        p.setFormatA(true);


        p = proyectoRepository.save(p);

        String formatoTok = documentosClient.subir(p.getId(), "FORMATO_A", pdf);

        String cartaTok = (carta != null && !carta.isEmpty())
                ? documentosClient.subir(p.getId(), "CARTA_EMPRESA", carta)
                : null;

        p.setFormatoAToken(formatoTok);
        p.setCartaToken(cartaTok);
        p = proyectoRepository.save(p);

        FormatoASubidoEvent ev = new FormatoASubidoEvent();
        ev.setIdProyecto(p.getId());
        ev.setTitulo(p.getTitulo());
        ev.setCoordinadorEmail("coordinador.sistemas@unicauca.edu.co");
        notificacionesClient.notificarFormatoASubido(ev);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("idProyecto", p.getId());
        respuesta.put("formatoAToken", formatoTok);
        if (cartaTok != null) respuesta.put("cartaToken", cartaTok);

        return ResponseEntity.ok(respuesta);
    }

    @Override
    public List<ProyectoGrado> obtenerFormatoAPendientes() {
        return proyectoService.obtenerFormatoAPendientes();
    }

    @Override
    public List<ProyectoGrado> obtenerFormatoARechazados() {
        return proyectoService.obtenerFormatoARechazados();
    }

    @Override
    public List<ProyectoGrado> obtenerFormatoAAprobados() {
        return proyectoService.obtenerFormatoAAprobados();
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

        AsignacionEvaluadoresEvent ev = new AsignacionEvaluadoresEvent();
        ev.setIdProyecto(p.getId());
        ev.setTitulo(p.getTitulo());
        ev.setJefeDepartamentoEmail(jefeDepartamentoEmail);
        ev.setEvaluador1Email(evaluador1Email);
        ev.setEvaluador2Email(evaluador2Email);
        ev.setEstudianteEmail1(p.getEstudiante1Email());
        ev.setEstudianteEmail2(p.getEstudiante2Email());
        ev.setDirectorEmail(p.getDirectorEmail());
        ev.setCodirectorEmail(p.getCodirectorEmail());

        notificacionesClient.notificarAsignacionEvaluadores(ev);

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
        notificacionesClient.notificarFormatoASubido(ev);

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

        notificacionesClient.notificarEvaluacionFormatoA(ev);
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
        notificacionesClient.notificarFormatoASubido(ev);
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
            p.setFechaAnteproyecto(java.time.LocalDate.now());
            proyectoService.guardar(p);

            AnteproyectoSubidoEvent ev = new AnteproyectoSubidoEvent();
            ev.setIdProyecto(p.getId());
            ev.setTitulo(p.getTitulo());
            ev.setJefeDepartamentoEmail(jefeDepartamentoEmail);
            ev.setEstudianteEmail(p.getEstudiante1Email());
            ev.setTutor1Email(p.getDirectorEmail());
            if (p.getCodirectorEmail() != null && !p.getCodirectorEmail().isEmpty()) {
                ev.setTutor2Email(p.getCodirectorEmail());
            }
            notificacionesClient.notificarAnteproyectoSubido(ev);

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
    @Override
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
        notificacionesClient.notificarEvaluacionFormatoA(ev);

        return p;
    }

    public ProyectoGrado buscarProyecto(Long id){
        return proyectoRepository.findById(id).orElseThrow();
    }

    @Override
    public List<FormatoAInfoDTO> obtenerFormatosAPorEmail(String emailEstudiante) {
        // 1. Buscar proyectos donde el estudiante1 tenga ese email
        List<ProyectoGrado> proyectos = proyectoService.findByEstudiante1Email(emailEstudiante);

        // 2. Filtrar solo los que tengan Formato A ya subido (token no nulo)
        return proyectos.stream()
                .filter(p -> p.getFormatoAToken() != null && !p.getFormatoAToken().isBlank())
                .map(p -> {
                    FormatoAInfoDTO dto = new FormatoAInfoDTO();
                    dto.setIdProyecto(p.getId());
                    dto.setTituloProyecto(p.getTitulo());
                    dto.setEstudiante1Email(p.getEstudiante1Email());
                    dto.setEstudiante2Email(p.getEstudiante2Email());
                    dto.setFormatoAToken(p.getFormatoAToken());
                    dto.setFechaFormatoA(p.getFechaFormatoA());
                    return dto;
                })
                .toList();
    }


}
