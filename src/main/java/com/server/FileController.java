package com.server;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FileController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/android")
    public String android(Model model) {
        // Nombre visible y archivo estático
        String nombreVisible = "MiApp Android";
        String nombrePrograma = "APP.apk"; // archivo colocado en src/main/resources/static/uploads/android/

        model.addAttribute("tipo", "android");
        model.addAttribute("nombreVisible", nombreVisible);
        model.addAttribute("nombrePrograma", nombrePrograma);
        model.addAttribute("disponible", true);

        String descripcion = "Aplicación Android institucional - Permite gestionar procesos desde dispositivos móviles.";
        model.addAttribute("descripcion", descripcion);

        String imagen = "/img/android.png";
        model.addAttribute("imagen", imagen);

        // El botón de descarga debe apuntar a /uploads/android/APP.apk
        return "android";
    }

    @GetMapping("/escritorio")
    public String escritorio(Model model) {
        String nombreVisible = "GDOC Escritorio";
        String nombrePrograma = "GDOC.exe"; // archivo colocado en src/main/resources/static/uploads/escritorio/

        model.addAttribute("tipo", "escritorio");
        model.addAttribute("nombreVisible", nombreVisible);
        model.addAttribute("nombrePrograma", nombrePrograma);
        model.addAttribute("disponible", true);

        String descripcion = "Sistema de Gestión de Incapacidad de Docentes - Permite administrar incapacidades y generar reportes institucionales.";
        model.addAttribute("descripcion", descripcion);

        String imagen = "/img/programa.png";
        model.addAttribute("imagen", imagen);

        // El botón de descarga debe apuntar a /uploads/escritorio/GDOC.exe
        return "escritorio";
    }
}
