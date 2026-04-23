package com.tpspringboot.apirestclientcommande.Commande.controllerCO;

import com.tpspringboot.apirestclientcommande.Commande.dto.CommandeAdminDetailsResponseDto;
import com.tpspringboot.apirestclientcommande.Commande.dto.CommandeResponseDto;
import com.tpspringboot.apirestclientcommande.Commande.modeleCO.Commande;
import com.tpspringboot.apirestclientcommande.Commande.serviceCO.CommandeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/", "/api"})
@RequiredArgsConstructor
public class CommandeController {

    private final CommandeService commandeService;

    @GetMapping("/commandes")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<CommandeResponseDto>> getCommandes() {
        return ResponseEntity.ok(commandeService.getCommandes());
    }

    @GetMapping("/commandes/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<CommandeResponseDto> getCommande(@PathVariable Long id) {
        return ResponseEntity.ok(commandeService.getCommande(id));
    }

    @GetMapping("/my/commandes")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<CommandeResponseDto>> getMyCommandes() {
        return ResponseEntity.ok(commandeService.getMyCommandes());
    }

    @GetMapping("/my/commandes/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<CommandeResponseDto> getMyCommande(@PathVariable Long id) {
        return ResponseEntity.ok(commandeService.getMyCommande(id));
    }

    @GetMapping("/commandes/{id}/details")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommandeAdminDetailsResponseDto> getCommandeDetailsForAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(commandeService.getCommandeDetailsForAdmin(id));
    }

    @PostMapping("/commandes/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommandeResponseDto> saveCommande(@PathVariable Long userId, @RequestBody Commande commande) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandeService.saveCommande(userId, commande));
    }

    @PutMapping("/users/commandes/{comId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommandeResponseDto> updateCommande(@PathVariable Long comId, @RequestBody Commande commande) {
        return ResponseEntity.ok(commandeService.updateCommande(comId, commande));
    }

    @DeleteMapping("/commandes/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> deletCommande(@PathVariable Long id) {
        commandeService.deleteCommande(id);
        return ResponseEntity.noContent().build();
    }
}