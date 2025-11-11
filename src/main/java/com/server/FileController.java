package com.server;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class FileController {

    private static final String UPLOAD_DIR = "static/uploads";

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/android")
    public String android(Model model) {
        String nombreVisible = "MiApp Android";
        String nombrePrograma = "APP.apk"; // archivo en src/main/resources/static/uploads/android/

        model.addAttribute("tipo", "android");
        model.addAttribute("nombreVisible", nombreVisible);
        model.addAttribute("nombrePrograma", nombrePrograma);
        model.addAttribute("disponible", true);

        model.addAttribute("descripcion", "Aplicación Android institucional - Permite gestionar procesos desde dispositivos móviles.");
        model.addAttribute("imagen", "/img/android.png");

        return "android";
    }

    @GetMapping("/escritorio")
    public String escritorio(Model model) {
        String nombreVisible = "GDOC Escritorio";
        String nombrePrograma = "GDOC.exe"; // archivo en src/main/resources/static/uploads/escritorio/

        model.addAttribute("tipo", "escritorio");
        model.addAttribute("nombreVisible", nombreVisible);
        model.addAttribute("nombrePrograma", nombrePrograma);
        model.addAttribute("disponible", true);

        model.addAttribute("descripcion", "Sistema de Gestión de Incapacidad de Docentes - Permite administrar incapacidades y generar reportes institucionales.");
        model.addAttribute("imagen", "/img/programa.png");

        return "escritorio";
    }

    @GetMapping("/descargar")
    public ResponseEntity<InputStreamResource> descargar(
            @RequestParam String tipo,
            @RequestParam String nombre) throws IOException {

        // Buscar el recurso dentro del classpath
        ClassPathResource resource = new ClassPathResource(UPLOAD_DIR + "/" + tipo + "/" + nombre);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Extensión según tipo
        String extension = tipo.equals("android") ? ".apk" : ".exe";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + extension + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(resource.getInputStream()));
    }
}
