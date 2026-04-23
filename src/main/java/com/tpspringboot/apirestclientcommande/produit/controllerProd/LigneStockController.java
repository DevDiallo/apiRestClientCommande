package com.tpspringboot.apirestclientcommande.produit.controllerProd;

import com.tpspringboot.apirestclientcommande.produit.dto.LigneStockResponseDto;
import com.tpspringboot.apirestclientcommande.produit.dto.ProduitLiteDto;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.LigneStock;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Produit;
import com.tpspringboot.apirestclientcommande.produit.serviceProd.LigneStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@RestController
@RequestMapping({"/", "/api"})
public class LigneStockController {

    private final LigneStockService ligneStockService;

    @PostMapping("/ligneStocks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LigneStockResponseDto> create(@RequestBody LigneStock ligneStock) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(ligneStockService.saveLigneStock(ligneStock)));
    }

    @GetMapping("/ligneStocks")
    public ResponseEntity<List<LigneStockResponseDto>> getAll() {
        List<LigneStockResponseDto> payload = StreamSupport.stream(ligneStockService.getLigneStocks().spliterator(), false)
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/ligneStocks/{id}")
    public ResponseEntity<LigneStockResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(ligneStockService.getLigneStock(id)));
    }

    @PutMapping("/ligneStocks/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<LigneStockResponseDto> update(@PathVariable Long id, @RequestBody LigneStock ligneStock) {
        return ResponseEntity.ok(toDto(ligneStockService.updateLigneStock(id, ligneStock)));
    }

    @DeleteMapping("/ligneStocks/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ligneStockService.deleteLigneStock(id);
        return ResponseEntity.noContent().build();
    }

    private LigneStockResponseDto toDto(LigneStock ligneStock) {
        ProduitLiteDto produit = toProduitLiteDto(ligneStock.getProduit());
        return new LigneStockResponseDto(
                ligneStock.getId(),
                ligneStock.getStockId(),
                ligneStock.getQuantiteStock(),
                produit
        );
    }

    private ProduitLiteDto toProduitLiteDto(Produit produit) {
        if (produit == null) {
            return null;
        }
        return new ProduitLiteDto(
                produit.getId(),
                produit.getNom(),
                produit.getPrix(),
                produit.getImagePath(),
                produit.getCategorieId()
        );
    }
}