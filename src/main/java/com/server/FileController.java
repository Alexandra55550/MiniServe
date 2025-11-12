package com.server;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
public class FileController {

    // Nuevas rutas absolutas de los programas
    private static final String ANDROID_PATH = "C:\\Users\\New user\\Desktop\\server\\src\\main\\resources\\static\\uploads\\android\\practicaApp.apk";
    private static final String ESCRITORIO_PATH = "C:\\Users\\New user\\Desktop\\server\\src\\main\\resources\\static\\uploads\\escritorio\\Biblioteca.exe";

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/android")
    public String android(Model model) {
        model.addAttribute("tipo", "android");
        model.addAttribute("nombreVisible", "Aplicación Android - prácticaApp");
        model.addAttribute("nombrePrograma", "practicaApp.apk");
        model.addAttribute("disponible", true);
        model.addAttribute("descripcion", "Aplicación Android institucional - Permite gestionar procesos desde dispositivos móviles.");
        model.addAttribute("imagen", "/uploads/android/android.png");
        return "android";
    }

    @GetMapping("/escritorio")
    public String escritorio(Model model) {
        model.addAttribute("tipo", "escritorio");
        model.addAttribute("nombreVisible", "Aplicación de Escritorio - Biblioteca");
        model.addAttribute("nombrePrograma", "Biblioteca.exe");
        model.addAttribute("disponible", true);
        model.addAttribute("descripcion", "Sistema de Gestión de Biblioteca - Permite administrar libros, usuarios y préstamos.");
        model.addAttribute("imagen", "/uploads/escritorio/programa.png");
        return "escritorio";
    }

    @GetMapping("/descargar")
    public ResponseEntity<InputStreamResource> descargar(@RequestParam String tipo) throws IOException {
        File archivo;

        // Determinar qué archivo descargar
        if ("android".equalsIgnoreCase(tipo)) {
            archivo = new File(ANDROID_PATH);
        } else if ("escritorio".equalsIgnoreCase(tipo)) {
            archivo = new File(ESCRITORIO_PATH);
        } else {
            return ResponseEntity.badRequest().build();
        }

        if (!archivo.exists()) {
            return ResponseEntity.notFound().build();
        }

        String nombreArchivo = archivo.getName();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(new FileInputStream(archivo)));
    }
}
