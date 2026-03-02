package com.tpspringboot.apirestclientcommande.Client.serviceCL;

import com.tpspringboot.apirestclientcommande.Client.modeleCL.Client;
import com.tpspringboot.apirestclientcommande.Client.repositoryCL.ClientRepository;
import com.tpspringboot.apirestclientcommande.Exceptions.RessourceAlreadyExist;
import com.tpspringboot.apirestclientcommande.Exceptions.RessourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    ClientRepository clientRepository ;

    public ResponseEntity<Client> getClient(final Long id) {
        Optional<Client> client = clientRepository.findById(id) ;
        if (client.isPresent()){
            return  ResponseEntity.ok(client.get()) ;
        } else {
            throw new RessourceNotFoundException("Error getClient ! Client not found");
        }
    }

    public Iterable<Client> getClients (){
        return clientRepository.findAll();
    }

    public ResponseEntity<Void> deleteClient(Long id){
        clientRepository.deleteById(id) ;
        return ResponseEntity.noContent().build() ;
    }

    public ResponseEntity<Client> saveClient(Client client){
        Optional<Client> existingClient = clientRepository.findByEmail(client.getEmail()) ;
        if (existingClient.isPresent()){
            throw new RessourceAlreadyExist("Error saveClient ! Ressource Already Exist") ;
        } else {
            return ResponseEntity.ok(clientRepository.save(client)) ;
        }
    }

    public ResponseEntity<Client> updateClient(Long id , Client client){
        Optional<Client> existingClient = clientRepository.findByid(id) ;
        if (existingClient.isPresent()){
            Client c = existingClient.get() ;
            c.setNom(client.getNom());
            return ResponseEntity.ok(clientRepository.save(c)) ;
        } else {
            throw new RessourceNotFoundException("Error updateClient") ;
        }
    }

}
