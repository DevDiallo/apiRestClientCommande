package com.tpspringboot.apirestclientcommande.Commande.controllerCO;

import com.tpspringboot.apirestclientcommande.Commande.modeleCO.Commande;
import com.tpspringboot.apirestclientcommande.Commande.serviceCO.CommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommandeController {

    @Autowired
    private CommandeService commandeService ;

    @GetMapping("/commandes")
    public ResponseEntity<Iterable<Commande>> getCommandes(){
        return ResponseEntity.ok(commandeService.getCommandes()) ;
    }

    @GetMapping("/commandes/{id}")
    public ResponseEntity<Commande> getCommande(@PathVariable Long id){
        return commandeService.getCommande(id) ;
    }

    @PostMapping("/clients/{clientId}/commandes")
    public ResponseEntity<Commande> saveCommande(@PathVariable Long clientId , @RequestBody Commande commande){
        return commandeService.saveCommande(clientId , commande) ;
    }

    @PutMapping("/clients/commandes/{id}")
    public ResponseEntity<Commande> updateCommande(@PathVariable Long id , @RequestBody Commande commande){
        return commandeService.updateCommande(id , commande) ;
    }

    @DeleteMapping("/commandes/{id}")
    public ResponseEntity<Void> deletCommande(@PathVariable Long id){
        return commandeService.deleteCommande(id) ;
    }

}
