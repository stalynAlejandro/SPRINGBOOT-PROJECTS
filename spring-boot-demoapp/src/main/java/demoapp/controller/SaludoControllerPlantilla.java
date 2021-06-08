package demoapp.controller;

import demoapp.service.SaludoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class SaludoControllerPlantilla {

    private final SaludoService service;

    @Autowired
    public SaludoControllerPlantilla(SaludoService service) {
        this.service = service;
    }

    @RequestMapping("/saludoplantilla/{nombre}")
    public String saludo(@PathVariable(value="nombre") String nombre, Model model) {
        // Se añade la respuesta a la clave 'mensaje' que se usa
        // en la plantilla
        model.addAttribute("mensaje", service.saluda(nombre));
        // Se llama a la plantilla 'saludo.html'
        return "saludo";
    }
}
