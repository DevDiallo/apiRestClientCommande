package com.tpspringboot.apirestclientcommande.Commande.serviceCO;

import com.tpspringboot.apirestclientcommande.Client.modeleCL.User;
import com.tpspringboot.apirestclientcommande.Client.repositoryCL.CrudUserRepository;
import com.tpspringboot.apirestclientcommande.Commande.modeleCO.Commande;
import com.tpspringboot.apirestclientcommande.Commande.repositoryCO.CommandeRepository;
import com.tpspringboot.apirestclientcommande.Exceptions.RessourceNotFoundException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Data
@Service
public class CommandeService {

    @Autowired
    private CrudUserRepository crudUserRepository ;

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

    public ResponseEntity<Commande> saveCommande(Long userId, Commande commande) {
        Optional<User> existingUser = crudUserRepository.findById(userId) ;
        if(existingUser.isPresent()){
            User u = existingUser.get() ;
            // mettre à jour l'attribut List<Commande> commandes de l'entité User
            u.getCommandes().add(commande) ;
            // mettre à jour l'entité commande
            commande.setUser(u);

            return ResponseEntity.ok(commandeRepository.save(commande)) ;
        } else {
            throw new RessourceNotFoundException("Error saveCommande ! customer_id does not exist ");
        }
    }

    public ResponseEntity<Commande> updateCommande(Long id , Commande commande) {
        Optional<Commande> existingCommande = commandeRepository.findById(id) ;
        if (existingCommande.isPresent()){
            Commande c = existingCommande.get() ;
            c.setCommandeProduits(commande.getCommandeProduits());
            c.setUser(commande.getUser());

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
