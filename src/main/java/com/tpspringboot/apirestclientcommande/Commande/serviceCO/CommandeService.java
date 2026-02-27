package com.tpspringboot.apirestclientcommande.Commande.serviceCO;

import com.tpspringboot.apirestclientcommande.Client.modeleCL.Client;
import com.tpspringboot.apirestclientcommande.Client.repositoryCL.ClientRepository;
import com.tpspringboot.apirestclientcommande.Client.serviceCL.ClientService;
import com.tpspringboot.apirestclientcommande.Commande.modeleCO.Commande;
import com.tpspringboot.apirestclientcommande.Commande.repositoryCO.CommandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommandeService {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    CommandeRepository commandeRepository ;

    public Iterable<Commande> getCommandes(){
        return commandeRepository.findAll() ;
    }

    public ResponseEntity<Commande> getCommande(Long id) {
        Optional<Commande> existingCommande = commandeRepository.findById(id) ;
        if (existingCommande.isPresent()){
            return ResponseEntity.ok(existingCommande.get()) ;
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build() ;
        }
    }

    public ResponseEntity<Commande> saveCommande(Long clientId, Commande commande) {
        Optional<Client> existingClient = clientRepository.findById(clientId) ;
        if(existingClient.isPresent()){
            commande.setClient(existingClient.get());
            return ResponseEntity.ok(commandeRepository.save(commande)) ;
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build() ;
        }
    }

    public ResponseEntity<Commande> updateCommande(Long id , Commande commande) {
        Optional<Commande> existingCommande = commandeRepository.findById(id) ;
        if (existingCommande.isPresent()){
            Commande c = existingCommande.get() ;
            c.setProduit(commande.getProduit());
            c.setQuantite(commande.getQuantite());

            return ResponseEntity.ok(commandeRepository.save(c)) ;
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build() ;

    }

    public ResponseEntity<Void> deleteCommande(Long id) {
        if (!commandeRepository.existsById(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build() ;
        }
        commandeRepository.deleteById(id);
        return ResponseEntity.noContent().build() ;
    }

}
