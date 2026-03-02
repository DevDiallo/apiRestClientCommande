package com.tpspringboot.apirestclientcommande.Client.modeleCL;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tpspringboot.apirestclientcommande.Commande.modeleCO.Commande;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    private String nom ;
    @Column(nullable = false)
    private String email ;

    @OneToMany(mappedBy = "client" , cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Commande> commandes = new ArrayList<>() ;

}
