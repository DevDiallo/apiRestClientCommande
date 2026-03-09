package com.tpspringboot.apirestclientcommande.produit.serviceProd;

import com.tpspringboot.apirestclientcommande.produit.modeleProd.Produit;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProduitService {

    private final ProduitRepository produitRepository ;

    public Iterable<Produit> getProduits(){
        return produitRepository.findAll() ;
    }

    public Optional<Produit> getProduit(Long id){
        return produitRepository.findById(id) ;
    }

    public Produit saveProduit(Produit produit){
        return produitRepository.save(produit) ;
    }

    public Optional<Produit> updateProduit(Long id , Produit produit){
        Optional<Produit> existingProduit = getProduit(id) ;
        if (existingProduit.isPresent()){
            Produit p = existingProduit.get() ;
            p.setNom(produit.getNom());
            p.setPrix(produit.getPrix());
            Produit produitSaved =  produitRepository.save(p) ;
            return Optional.of(produitSaved) ;
        } else {
            return Optional.empty() ;
        }
    }

    public void deleteProduit(Long id){
        produitRepository.deleteById(id);
    }

}
