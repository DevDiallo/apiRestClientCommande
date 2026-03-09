package com.tpspringboot.apirestclientcommande.produit.serviceProd;

import com.tpspringboot.apirestclientcommande.Commande.repositoryCO.CommandeRepository;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Commande_produit;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.ComProdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ComProdService {

    private final ComProdRepository comProdRepository ;

    public Iterable<Commande_produit> getcommandeProduits(){
        return comProdRepository.findAll() ;
    }

    public Optional<Commande_produit> getComProd(Long id){
        return comProdRepository.findById(id) ;
    }

    public Commande_produit saveComProd(Commande_produit commandeProduit){
        return comProdRepository.save(commandeProduit) ;
    }

    public Optional<Commande_produit> updateComProd(Long id , Commande_produit commandeProduit){
        Optional<Commande_produit> existingComProd = getComProd(id) ;
        if (existingComProd.isPresent()){
            Commande_produit ComProd = existingComProd.get() ;
            ComProd.setQuantite(commandeProduit.getQuantite());
            ComProd.setProduit(commandeProduit.getProduit());
            Commande_produit comProdSaved = saveComProd(ComProd) ;

            return Optional.of(comProdSaved) ;
        } else{
            return Optional.empty() ;
        }

    }

    public void deleteComProd(Long id){
        comProdRepository.deleteById(id);
    }
}
