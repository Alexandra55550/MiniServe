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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FileController {

    private static final String UPLOAD_DIR = "static/uploads";

    // Nombres por defecto si no envías ?nombre=
    private static final String DEFAULT_ANDROID = "practicaApp.apk";
    private static final String DEFAULT_ESCRITORIO = "Biblioteca.exe";

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("tipo", "android");
        model.addAttribute("nombreVisible", "Aplicación Android - prácticaApp");
        model.addAttribute("nombrePrograma", DEFAULT_ANDROID);
        model.addAttribute("disponible", true);
        model.addAttribute("descripcion", "Aplicación Android institucional - Permite gestionar procesos desde dispositivos móviles.");
        model.addAttribute("imagen", "/uploads/android/android.png");
        return "index";
    }


    @GetMapping("/android")
    public String android(Model model) {
        model.addAttribute("tipo", "android");
        model.addAttribute("nombreVisible", "Aplicación Android - prácticaApp");
        model.addAttribute("nombrePrograma", DEFAULT_ANDROID);
        model.addAttribute("disponible", true);
        //Descripcion
        model.addAttribute("descripcion", "Aplicación Android institucional - Permite gestionar procesos desde dispositivos móviles.");
        model.addAttribute("imagen", "/uploads/android/android.png");
        return "android";
    }

    @GetMapping("/escritorio")
    public String escritorio(Model model) {
        model.addAttribute("tipo", "escritorio");
        model.addAttribute("nombreVisible", "Aplicación de Escritorio - Biblioteca");
        model.addAttribute("nombrePrograma", DEFAULT_ESCRITORIO);
        model.addAttribute("disponible", true);
        //descripcion
        model.addAttribute("descripcion", "Sistema de Gestión de Biblioteca - Permite administrar libros, usuarios y préstamos.");
        model.addAttribute("imagen", "/uploads/escritorio/programa.png");
        return "escritorio";
    }

    /**
     * /descargar?tipo=android&nombre=practicaApp.apk
     * Si no se envía nombre, se usa el nombre por defecto según tipo.
     */
    @GetMapping("/descargar")
    public ResponseEntity<InputStreamResource> descargar(
            @RequestParam String tipo,
            @RequestParam(required = false) String nombre) throws IOException {

        // Nombre por defecto si no viene
        if (nombre == null || nombre.trim().isEmpty()) {
            nombre = tipo.equalsIgnoreCase("android") ? DEFAULT_ANDROID : DEFAULT_ESCRITORIO;
        }

        String baseName = quitarExtensiones(nombre);
        String extension = obtenerExtensionPorTipo(tipo, nombre);

        // 1) Buscar partes (part001, part002, ...)
        List<ClassPathResource> partes = listarPartes(tipo, baseName);

        if (!partes.isEmpty()) {
            // Unir partes en archivo temporal
            File archivoFinal = File.createTempFile(baseName + "_", extension);
            archivoFinal.deleteOnExit();

            try (FileOutputStream fos = new FileOutputStream(archivoFinal)) {
                for (ClassPathResource parte : partes) {
                    try (InputStream in = parte.getInputStream()) {
                        in.transferTo(fos);
                    }
                }
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + baseName + extension + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(new FileInputStream(archivoFinal)));
        }

        // 2) Intentar archivo único en classpath
        ClassPathResource resource = new ClassPathResource(UPLOAD_DIR + "/" + tipo + "/" + nombre);
        if (!resource.exists()) {
            // Intentar con nombre limpio (por si enviaron sin extensión)
            resource = new ClassPathResource(UPLOAD_DIR + "/" + tipo + "/" + baseName + extension);
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + baseName + extension + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(resource.getInputStream()));
    }

    /** Quita extensiones comunes para obtener el baseName */
    private String quitarExtensiones(String nombre) {
        String bn = nombre;
        if (bn.endsWith(".apk")) bn = bn.substring(0, bn.length() - 4);
        if (bn.endsWith(".exe")) bn = bn.substring(0, bn.length() - 4);
        if (bn.endsWith(".part")) bn = bn.substring(0, bn.length() - 5);
        // Si venía como GDOC.part001 -> quitar .partNNN
        bn = bn.replaceAll("\\.part\\d{1,3}$", "");
        return bn;
    }

    /** Devuelve extensión a usar en el nombre final */
    private String obtenerExtensionPorTipo(String tipo, String nombre) {
        if (nombre.toLowerCase().endsWith(".apk") || "android".equalsIgnoreCase(tipo)) return ".apk";
        if (nombre.toLowerCase().endsWith(".exe") || "escritorio".equalsIgnoreCase(tipo)) return ".exe";
        // fallback
        return ".bin";
    }

    /**
     * Lista partes en classpath buscando patrones:
     * base.part001, base.part002, ... (se detiene cuando falta la siguiente parte)
     */
    private List<ClassPathResource> listarPartes(String tipo, String baseName) {
        List<ClassPathResource> partes = new ArrayList<>();

        // Intentamos con sufijos de 3 dígitos: 001, 002, ...
        for (int i = 1; i <= 999; i++) {
            String suf = String.format("%03d", i);
            String ruta = String.format("%s/%s/%s.part%s", UPLOAD_DIR, tipo, baseName, suf);
            ClassPathResource r = new ClassPathResource(ruta);
            if (r.exists()) {
                partes.add(r);
            } else {
                // si la parte 1 no existe, asumimos que no hay más partes
                if (i == 1) {
                    break;
                } else {
                    // cuando una parte intermedia no existe, detenemos la búsqueda
                    break;
                }
            }
        }

        // También soportar nombres con sufijo .part (sin número), p.ej. archivo.part
        if (partes.isEmpty()) {
            String rutaSimple = String.format("%s/%s/%s.part", UPLOAD_DIR, tipo, baseName);
            ClassPathResource r = new ClassPathResource(rutaSimple);
            if (r.exists()) partes.add(r);
        }

        return partes;
    }
}
