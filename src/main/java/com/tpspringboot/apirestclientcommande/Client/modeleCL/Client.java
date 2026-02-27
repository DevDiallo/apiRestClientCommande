package com.tpspringboot.apirestclientcommande.Client.modeleCL;

import com.tpspringboot.apirestclientcommande.Commande.modeleCO.Commande;
import jakarta.persistence.*;
import lombok.Data;

import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Data
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    private String nom ;
    @Column(nullable = false)
    private String email ;
    /*
    @OneToMany(mappedBy = "client")
    private List<Commande> commandes;
     */


}
