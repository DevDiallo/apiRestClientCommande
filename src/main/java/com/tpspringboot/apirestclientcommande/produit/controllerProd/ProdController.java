package com.tpspringboot.apirestclientcommande.produit.controllerProd;

import com.tpspringboot.apirestclientcommande.produit.modeleProd.Produit;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.ProduitRepository;
import com.tpspringboot.apirestclientcommande.produit.serviceProd.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/")
public class ProdController {

    private final ProduitService produitService ;
    private final ProduitRepository produitRepository ;

    @GetMapping("/produits")
    public ResponseEntity<?> getProduits(){
        return ResponseEntity.ok(produitService.getProduits()) ;
    }

    @GetMapping("/produits/{id}")
    public ResponseEntity<?> getProduit(@PathVariable Long id){
        return ResponseEntity.ok(produitService.getProduit(id)) ;
    }

    @PostMapping("/produits")
    public ResponseEntity<?> saveProduit(@RequestBody Produit produit){
        return ResponseEntity.ok(produitService.saveProduit(produit)) ;
    }

    @PutMapping("/produits/{id}")
    public ResponseEntity<?> updateProduit(@PathVariable Long id , @RequestBody Produit produit){
        return ResponseEntity.ok(produitService.updateProduit(id , produit)) ;
    }

    @DeleteMapping("/produits/{id}")
    public ResponseEntity<?> deleteProduit(@PathVariable Long id){
        if (produitRepository.existsById(id)){
            produitService.deleteProduit(id) ;
            return ResponseEntity.status(HttpStatus.OK).body("Produit : " + id + "est bien Supprimé") ;
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produit : " + id + "existe pas") ;
    }

}
