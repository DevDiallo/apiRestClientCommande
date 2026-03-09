package com.tpspringboot.apirestclientcommande.produit.controllerProd;

import com.tpspringboot.apirestclientcommande.Commande.modeleCO.Commande;
import com.tpspringboot.apirestclientcommande.Commande.repositoryCO.CommandeRepository;
import com.tpspringboot.apirestclientcommande.Commande.serviceCO.CommandeService;
import com.tpspringboot.apirestclientcommande.Exceptions.RessourceNotFoundException;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Commande_produit;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Produit;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.ComProdRepository;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.ProduitRepository;
import com.tpspringboot.apirestclientcommande.produit.serviceProd.ComProdService;
import com.tpspringboot.apirestclientcommande.produit.serviceProd.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/")
public class ComProdController {

    private final ComProdRepository comProdRepository ;
    private final ComProdService comProdService ;
    private final ProduitRepository produitRepository ;
    private final CommandeRepository commandeRepository ;

    @GetMapping("/commandesProduits")
    public ResponseEntity<?> getComProd(){
        return ResponseEntity.ok(comProdService.getcommandeProduits()) ;
    }

    @GetMapping("/commandesProduits/{id}")
    public ResponseEntity<?> getComProd(@PathVariable Long id){
        return ResponseEntity.ok(comProdService.getComProd(id)) ;
    }

    @PostMapping("/commandesProduits/{prodId}/{comId}")
    public ResponseEntity<?> saveComProd(@RequestBody Commande_produit comProd,@PathVariable Long prodId, @PathVariable Long comId){
        Optional<Commande> existingCommande = commandeRepository.findById(comId) ;
        Optional<Produit> existingProduit = produitRepository.findById(prodId) ;
        if(existingProduit.isPresent() && existingCommande.isPresent()){
            Commande com = existingCommande.get() ;
            comProd.setProduit(existingProduit.get());
            com.getCommandeProduits().add(comProd);
            comProd.setCommande(existingCommande.get());

            return ResponseEntity.ok(comProdService.saveComProd(comProd)) ;
        } else {
            throw new RessourceNotFoundException("Invalid Product or Commande !") ;
        }
    }

    @PutMapping("/commandesProduits/{id}")
    public ResponseEntity<?> updateComProd(@PathVariable Long id , @RequestBody Commande_produit comProd){
        return ResponseEntity.ok(comProdService.updateComProd(id, comProd)) ;
    }

    @DeleteMapping("/commandesProduits/{id}")
    public ResponseEntity<?> deleteComProd(@PathVariable Long id){
        if (comProdRepository.existsById(id)){
            comProdService.deleteComProd(id); ;
            return ResponseEntity.status(HttpStatus.OK).body("Produit : " + id + "est bien Supprimé") ;
        } else {
            throw new RessourceNotFoundException("Produit : " + id + "existe pas") ;
        }

    }

}
