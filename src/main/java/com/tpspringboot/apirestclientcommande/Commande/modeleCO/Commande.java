package com.tpspringboot.apirestclientcommande.Commande.modeleCO;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tpspringboot.apirestclientcommande.User.modeleCL.User;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Commande_produit;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
@Entity
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @ManyToOne
    @JoinColumn(name="user_id" , nullable = false)
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "commande" , cascade = CascadeType.ALL)
    @JsonManagedReference(value = "commande-commandeProduit")
    private List<Commande_produit> commandeProduits = new ArrayList<>() ;


}
