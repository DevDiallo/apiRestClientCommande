package com.tpspringboot.apirestclientcommande.produit.controllerProd;

import com.tpspringboot.apirestclientcommande.produit.dto.CommandeProduitResponseDto;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Commande_produit;
import com.tpspringboot.apirestclientcommande.produit.serviceProd.ComProdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping({"/", "/api"})
public class ComProdController {

    private final ComProdService comProdService;

    @GetMapping("/commandesProduits")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<CommandeProduitResponseDto>> getComProd() {
        return ResponseEntity.ok(comProdService.getCommandeProduits());
    }

    @GetMapping("/commandesProduits/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<CommandeProduitResponseDto> getComProd(@PathVariable Long id) {
        return ResponseEntity.ok(comProdService.getComProd(id));
    }

    @GetMapping("/my/commandesProduits")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<CommandeProduitResponseDto>> getMyComProd() {
        return ResponseEntity.ok(comProdService.getMyCommandeProduits());
    }

    @GetMapping("/my/commandesProduits/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<CommandeProduitResponseDto> getMyComProd(@PathVariable Long id) {
        return ResponseEntity.ok(comProdService.getMyComProd(id));
    }

    @PostMapping("/commandesProduits/{prodId}/{comId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommandeProduitResponseDto> saveComProd(
            @RequestBody Commande_produit comProd,
            @PathVariable Long prodId,
            @PathVariable Long comId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(comProdService.saveComProd(prodId, comId, comProd));
    }

    @PutMapping("/commandesProduits/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommandeProduitResponseDto> updateComProd(@PathVariable Long id, @RequestBody Commande_produit comProd) {
        return ResponseEntity.ok(comProdService.updateComProd(id, comProd));
    }

    @DeleteMapping("/commandesProduits/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteComProd(@PathVariable Long id) {
        comProdService.deleteComProd(id);
        return ResponseEntity.noContent().build();
    }
}