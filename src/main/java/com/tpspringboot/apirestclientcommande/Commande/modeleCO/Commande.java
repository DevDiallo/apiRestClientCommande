package com.tpspringboot.apirestclientcommande.Commande.modeleCO;

import com.tpspringboot.apirestclientcommande.Client.modeleCL.Client;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Data
@Entity
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    private String produit ;
    private Integer quantite ;

    @ManyToOne
    @JoinColumn(name="client_id" , nullable = false)
    private Client client ;


}
