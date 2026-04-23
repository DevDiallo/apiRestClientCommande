package com.tpspringboot.apirestclientcommande.produit.controllerProd;

import com.tpspringboot.apirestclientcommande.produit.modeleProd.Categorie;
import com.tpspringboot.apirestclientcommande.produit.serviceProd.CategorieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping({"/", "/api"})
public class CategorieController {

    private final CategorieService categorieService;

    @GetMapping("/categories")
    public ResponseEntity<Iterable<Categorie>> getAll() {
        return ResponseEntity.ok(categorieService.getCategories());
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<Categorie> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categorieService.getCategorie(id));
    }

    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Categorie> create(@RequestBody Categorie categorie) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categorieService.saveCategorie(categorie));
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Categorie> update(@PathVariable Long id, @RequestBody Categorie categorie) {
        return ResponseEntity.ok(categorieService.updateCategorie(id, categorie));
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categorieService.deleteCategorie(id);
        return ResponseEntity.noContent().build();
    }
}
