package com.tpspringboot.apirestclientcommande.produit.controllerProd;

import com.tpspringboot.apirestclientcommande.Exceptions.RessourceNotFoundException;
import com.tpspringboot.apirestclientcommande.produit.dto.ProduitAdminUpsertRequestDto;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.LigneStock;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Produit;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.LigneStockRepository;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.ProduitRepository;
import com.tpspringboot.apirestclientcommande.produit.serviceProd.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@RestController
@RequestMapping({"/", "/api"})
public class ProdController {

    private final ProduitService produitService;
    private final ProduitRepository produitRepository;
    private final LigneStockRepository ligneStockRepository;

    @Value("${app.public-base-url:http://localhost:7000}")
    private String publicBaseUrl;

    @GetMapping("/produits")
    public ResponseEntity<?> getProduits() {
        List<Map<String, Object>> payload = StreamSupport.stream(produitService.getProduits().spliterator(), false)
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/produits/{id}")
    public ResponseEntity<?> getProduit(@PathVariable Long id) {
        Produit produit = produitService.getProduit(id)
                .orElseThrow(() -> new RessourceNotFoundException("Produit introuvable : " + id));
        return ResponseEntity.ok(toResponse(produit));
    }

    @PostMapping("/produits")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> saveProduit(@RequestBody ProduitAdminUpsertRequestDto payload) {
        Produit saved = produitService.saveProduit(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @PutMapping("/produits/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProduit(@PathVariable Long id, @RequestBody ProduitAdminUpsertRequestDto payload) {
        Produit updated = produitService.updateProduit(id, payload)
                .orElseThrow(() -> new RessourceNotFoundException("Produit introuvable : " + id));
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/produits/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduit(@PathVariable Long id) {
        if (!produitRepository.existsById(id)) {
            throw new RessourceNotFoundException("Produit introuvable : " + id);
        }
        produitService.deleteProduit(id);
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> toResponse(Produit p) {
        String imageUrl = normalizeImageUrlForFrontend(p.getImageUrl());

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", p.getId());
        payload.put("nom", p.getNom());
        payload.put("description", p.getDescription());
        payload.put("prix", p.getPrix());
        payload.put("imageUrl", imageUrl);
        payload.put("imagePath", imageUrl);
        payload.put("categorieId", p.getCategorieId());
        payload.put("ligneStockId", p.getLigneStockId());
        payload.put("quantite_stock", resolveStockQuantity(p));
        return payload;
    }

    private Integer resolveStockQuantity(Produit produit) {
        if (produit.getLigneStockId() == null || produit.getLigneStockId().isBlank()) {
            return null;
        }
        try {
            Long ligneStockId = Long.parseLong(produit.getLigneStockId());
            return ligneStockRepository.findById(ligneStockId)
                    .map(LigneStock::getQuantiteStock)
                    .orElse(null);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String normalizeImageUrlForFrontend(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return rawValue;
        }

        String value = rawValue.trim();

        // Keep already proxied path as-is (works with /api proxy in Angular dev server)
        if (value.startsWith("/api/images/")) {
            return value;
        }

        // Convert backend static path to proxied API path
        if (value.startsWith("/images/")) {
            return "/api" + value;
        }
        if (value.startsWith("images/")) {
            return "/api/" + value;
        }

        // Normalize absolute backend URLs to proxied API path
        if (value.startsWith("http://") || value.startsWith("https://")) {
            int imagesIndex = value.indexOf("/images/");
            if (imagesIndex >= 0) {
                return "/api" + value.substring(imagesIndex);
            }
            return value;
        }

        // Fallback: keep compatibility and expose a usable backend URL
        return publicBaseUrl + (value.startsWith("/") ? "" : "/") + value;
    }
}