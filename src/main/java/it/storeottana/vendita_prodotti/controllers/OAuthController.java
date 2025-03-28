package it.storeottana.vendita_prodotti.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    @GetMapping("/Callback")
    public ResponseEntity<String> handleOAuthCallback(@RequestParam String code) {
        // Gestisci il codice di autorizzazione ricevuto da Google
        return ResponseEntity.ok("Autenticazione completata!");
    }
}

