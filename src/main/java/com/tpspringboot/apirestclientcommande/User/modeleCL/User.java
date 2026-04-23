package com.tpspringboot.apirestclientcommande.User.modeleCL;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tpspringboot.apirestclientcommande.Commande.modeleCO.Commande;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    private String nom ;
    private String prenom ;
    private String email ;
    private String telephone ;
    private String username ;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password ;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String role ;
    /*
    public enum Role {
        ROLE_USER ,
        ROLE_ADMIN
    }
     */
    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Commande> commandes = new ArrayList<>();

    @JsonProperty("roles")
    public List<String> getRoles() {
        return role == null ? List.of() : List.of(role);
    }

}
