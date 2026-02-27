package com.tpspringboot.apirestclientcommande.Client.controllerCL;

import com.tpspringboot.apirestclientcommande.Client.modeleCL.Client;
import com.tpspringboot.apirestclientcommande.Client.serviceCL.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class ClientController {

    @Autowired
    ClientService clientService ;

    @GetMapping("/clients")
    public ResponseEntity<Iterable<Client>> getClients(){
        return ResponseEntity.ok(clientService.getClients()) ;
    }

    @GetMapping("/clients/{id}")
    public ResponseEntity<Client> getClient(@PathVariable Long id){
        return clientService.getClient(id) ;
    }

    @PostMapping("/clients")
    public ResponseEntity<Client> saveClient(@RequestBody Client client){
        return clientService.saveClient(client) ;
    }

    @PutMapping("/clients/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id , @RequestBody Client client) {
        return clientService.updateClient(id , client) ;
    }

    @DeleteMapping("/clients/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        return clientService.deleteClient(id) ;
    }


}
