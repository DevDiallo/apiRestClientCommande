package com.tpspringboot.apirestclientcommande.produit.serviceProd;

import com.tpspringboot.apirestclientcommande.Exceptions.RessourceNotFoundException;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Categorie;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.CategorieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategorieService {

    private final CategorieRepository categorieRepository;

    public Iterable<Categorie> getCategories() {
        return categorieRepository.findAll();
    }

    public Categorie getCategorie(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Categorie introuvable"));
    }

    public Categorie saveCategorie(Categorie categorie) {
        return categorieRepository.save(categorie);
    }

    public Categorie updateCategorie(Long id, Categorie categorie) {
        Optional<Categorie> existing = categorieRepository.findById(id);
        if (existing.isEmpty()) {
            throw new RessourceNotFoundException("Categorie introuvable");
        }
        Categorie c = existing.get();
        if (categorie.getCategorieName() != null) {
            c.setCategorieName(categorie.getCategorieName());
        }
        return categorieRepository.save(c);
    }

    public void deleteCategorie(Long id) {
        categorieRepository.deleteById(id);
    }
}
