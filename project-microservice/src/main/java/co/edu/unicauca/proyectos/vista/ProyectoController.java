package co.edu.unicauca.proyectos.vista;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unicauca.proyectos.models.ProyectoGrado;
import co.edu.unicauca.proyectos.dto.ProyectoRequest;
import co.edu.unicauca.proyectos.services.IProyectoServiceFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/v1/proyectos") // <- versionado requerido
@Tag(name = "Gestión de Proyectos de Grado", description = "API para crear, evaluar y consultar proyectos de grado")
public class ProyectoController {

    @Autowired
    private IProyectoServiceFacade facade;

    @Operation(
            summary = "Crear un nuevo proyecto de grado",
            description = "Registra un nuevo proyecto de grado en la base de datos y publica un mensaje en RabbitMQ " +
                    "para que el microservicio de notificaciones envíe un correo al coordinador.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del proyecto a registrar",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProyectoRequest.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de proyecto",
                                    value = """
                                            {
                                              "titulo": "Sistema de Gestión de Bibliotecas",
                                              "modalidad": "INVESTIGACION",
                                              "directorEmail": "juan.perez@unicauca.edu.co",
                                              "codirectorEmail": "coordinador.sistemas@unicauca.edu.co",
                                              "estudiante1Email": "ana.gomez@unicauca.edu.co",
                                              "estudiante2Email": "carlos.martinez@unicauca.edu.co",
                                              "objetivoGeneral": "Desarrollar un sistema...",
                                              "objetivosEspecificos": "1. Diseñar... 2. Implementar..."
                                            }
                                            """
                            )
                    )
            )
    )
    @PostMapping
    public ResponseEntity<?> crearProyecto(@RequestBody ProyectoRequest request) {
        try {
            ProyectoGrado proyecto = new ProyectoGrado();
            proyecto.setTitulo(request.getTitulo());
            proyecto.setModalidad(request.getModalidad());
            proyecto.setDirectorEmail(request.getDirectorEmail());
            proyecto.setCodirectorEmail(request.getCodirectorEmail());
            proyecto.setEstudiante1Email(request.getEstudiante1Email());
            proyecto.setEstudiante2Email(request.getEstudiante2Email());
            proyecto.setObjetivoGeneral(request.getObjetivoGeneral());
            proyecto.setObjetivosEspecificos(request.getObjetivosEspecificos());

            ProyectoGrado resultado = facade.crearProyecto(proyecto);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"Error interno\"}");
        }
    }

    @Operation(
            summary = "Subir Formato A",
            description = "Recibe el PDF del Formato A y la carta (obligatoria si la modalidad es PRACTICA_PROFESIONAL)."
    )
    @PostMapping(value="/formatoA", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirFormatoA(
            @RequestParam("titulo") String titulo,
            @RequestParam("modalidad") String modalidad,
            @RequestParam("directorEmail") String directorEmail,
            @RequestParam(value="codirectorEmail", required=false) String codirectorEmail,
            @RequestParam("estudiante1Email") String estudiante1Email,
            @RequestPart("pdf") MultipartFile pdf,
            @RequestPart(value="carta", required=false) MultipartFile carta)
    {
        return facade.subirFormatoA(titulo, modalidad, directorEmail, codirectorEmail, estudiante1Email, pdf, carta);
    }

    @Operation(
            summary = "Evaluar un proyecto de grado",
            description = "Cambia el estado de un proyecto de grado a aprobado o rechazado y envía una notificación a los implicados."
    )
    @PostMapping("/{id}/evaluar")
    public ResponseEntity<?> evaluarProyecto(@PathVariable("id") Long id,
                                             @RequestParam("aprobado") boolean aprobado,
                                             @RequestParam("observaciones") String observaciones) {
        facade.evaluarProyecto(id, aprobado, observaciones);
        var p = facade.obtenerProyectoPorId(id);
        var resp = new java.util.HashMap<String,Object>();
        resp.put("mensaje", "Proyecto evaluado");
        resp.put("id", p.getId());
        resp.put("estadoActual", p.getEstadoActual());
        resp.put("numeroIntento", p.getNumeroIntento());
        resp.put("aprobado", aprobado);
        resp.put("observaciones", observaciones);
        return ResponseEntity.ok(resp);
    }

    @Operation(
            summary = "Subir anteproyecto",
            description = "Permite al docente subir el archivo PDF del anteproyecto una vez el Formato A ha sido aprobado. " +
                    "Envía notificación al jefe de departamento para asignar evaluadores."
    )
    @PostMapping(value="/{idProyecto}/anteproyecto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirAnteproyecto(
            @PathVariable("idProyecto") Long idProyecto,
            @RequestParam("jefeDepartamentoEmail") String jefeDepartamentoEmail,
            @RequestPart("pdf") MultipartFile anteproyectoPdf)  {
        return facade.subirAnteproyecto(idProyecto, jefeDepartamentoEmail, anteproyectoPdf); // <- delega al façade
    }

    @Operation(
            summary = "Obtener proyectos por estudiante",
            description = "Recupera todos los proyectos asociados a un estudiante específico."
    )
    @GetMapping("/estudiante/{email}")
    public ResponseEntity<?> obtenerPorEstudiante(@PathVariable String email) {
        try {
            List<ProyectoGrado> proyectos = facade.obtenerProyectosPorEstudiante(email);
            return ResponseEntity.ok(proyectos);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Operation(
            summary = "Obtener anteproyectos para evaluación (Jefe de Departamento)",
            description = "Recupera todos los proyectos que tienen anteproyecto subido y están pendientes de asignación de evaluadores."
    )
    @GetMapping("/anteproyectos/jefe/{emailJefe}")
    public ResponseEntity<?> obtenerAnteproyectosPorJefe(@PathVariable("emailJefe") String emailJefe) {
        try {
            List<ProyectoGrado> proyectos = facade.obtenerAnteproyectosPorJefe(emailJefe);
            return ResponseEntity.ok(proyectos);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Reintentar un proyecto de grado",
            description = "Permite subir una nueva versión del Formato A tras un rechazo."
    )
    @PostMapping("/{id}/reintentar")
    public ResponseEntity<?> reintentarProyecto(@PathVariable Long id) {
        try {
            facade.reintentarProyecto(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Operation(
            summary = "Obtener proyecto por ID",
            description = "Recupera los detalles completos de un proyecto específico por su identificador."
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProyectoPorId(@PathVariable Long id) {
        try {
            ProyectoGrado proyecto = facade.obtenerProyectoPorId(id); // <- fix
            return ResponseEntity.ok(proyecto);
        } catch (Exception e) {
            return ResponseEntity.status(404)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Obtener estado del proyecto",
            description = "Devuelve estado actual, intentos y últimas observaciones."
    )
    @GetMapping(path = "/{id}/estado", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> estado(@PathVariable Long id){
        ProyectoGrado p = facade.obtenerProyectoPorId(id);
        java.util.Map<String,Object> dto = new java.util.HashMap<>();
        dto.put("proyectoId", p.getId());
        dto.put("estado", p.getEstadoActual());
        dto.put("intentos", p.getIntentos());
        dto.put("observaciones", p.getObservacionesEvaluacion());
        dto.put("titulo", p.getTitulo());
        dto.put("modalidad", p.getModalidad());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{idProyecto}/evaluadores")
    public ResponseEntity<?> asignarEvaluadores(
            @PathVariable("idProyecto") Long idProyecto,
            @RequestParam("jefeDepartamentoEmail") String jefeDepartamentoEmail,
            @RequestParam("evaluador1Email") String evaluador1Email,
            @RequestParam("evaluador2Email") String evaluador2Email) {
        return facade.asignarEvaluadores(idProyecto, jefeDepartamentoEmail, evaluador1Email, evaluador2Email);
    }


}
