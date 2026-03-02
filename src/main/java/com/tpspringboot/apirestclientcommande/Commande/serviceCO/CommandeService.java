package com.tpspringboot.apirestclientcommande.Commande.serviceCO;

import com.tpspringboot.apirestclientcommande.Client.modeleCL.Client;
import com.tpspringboot.apirestclientcommande.Client.repositoryCL.ClientRepository;
import com.tpspringboot.apirestclientcommande.Client.serviceCL.ClientService;
import com.tpspringboot.apirestclientcommande.Commande.modeleCO.Commande;
import com.tpspringboot.apirestclientcommande.Commande.repositoryCO.CommandeRepository;
import com.tpspringboot.apirestclientcommande.Exceptions.RessourceAlreadyExist;
import com.tpspringboot.apirestclientcommande.Exceptions.RessourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
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
            throw new RessourceNotFoundException("Error getCommande ! Ressource not Found");
        }
    }

    public ResponseEntity<Commande> saveCommande(Long clientId, Commande commande) {
        Optional<Client> existingClient = clientRepository.findById(clientId) ;
        if(existingClient.isPresent()){
            Client c = existingClient.get() ;
            // mettre à jour l'attribut List<Commande> commandes de l'entité Client
            c.getCommandes().add(commande) ;
            // mettre à jour l'entité commande
            commande.setClient(c);

            return ResponseEntity.ok(commandeRepository.save(commande)) ;
        } else {
            throw new RessourceNotFoundException("Error saveCommande ! customer_id does not exist ");
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
        throw new RessourceNotFoundException("Error updateCommande ! Ressource not Found") ;

    }

    public ResponseEntity<Void> deleteCommande(Long id) {
        if (!commandeRepository.existsById(id)){
            throw new RessourceNotFoundException("Error deleteCommande ! Ressource not Found ") ;
        }
        commandeRepository.deleteById(id);
        return ResponseEntity.noContent().build() ;
    }

}
