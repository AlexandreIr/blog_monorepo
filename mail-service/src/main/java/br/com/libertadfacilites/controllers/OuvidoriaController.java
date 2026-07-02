package br.com.libertadfacilites.controllers;


import br.com.libertadfacilites.dtos.OuvidoriaRequest;
import br.com.libertadfacilites.services.OuvidoriaMailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ouvidoria")
public class OuvidoriaController {

    private final OuvidoriaMailService ouvidoriaMailService;

    public OuvidoriaController(OuvidoriaMailService ouvidoriaMailService) {
        this.ouvidoriaMailService = ouvidoriaMailService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> registrarManifestacao(
            @Valid @RequestBody OuvidoriaRequest request
    ) {
        ouvidoriaMailService.enviarManifestacao(request);

        return ResponseEntity.ok(
                Map.of("message", "Manifestação enviada com sucesso.")
        );
    }

    @GetMapping
    public String retorno () {
        return "Olá mundo";
    }
}
