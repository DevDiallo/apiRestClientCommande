package com.tpspringboot.apirestclientcommande.produit.serviceProd;

import com.tpspringboot.apirestclientcommande.Commande.modeleCO.Commande;
import com.tpspringboot.apirestclientcommande.Commande.repositoryCO.CommandeRepository;
import com.tpspringboot.apirestclientcommande.Exceptions.RessourceNotFoundException;
import com.tpspringboot.apirestclientcommande.produit.dto.CommandeProduitResponseDto;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Commande_produit;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Produit;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.ComProdRepository;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Service
public class ComProdService {

    private final ComProdRepository comProdRepository;
    private final ProduitRepository produitRepository;
    private final CommandeRepository commandeRepository;

    @Transactional(readOnly = true)
    public List<CommandeProduitResponseDto> getCommandeProduits() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        String currentUsername = authentication != null ? authentication.getName() : null;

        return StreamSupport.stream(comProdRepository.findAll().spliterator(), false)
                .filter(cp -> isAdmin || isOwnedByCurrentUser(cp, currentUsername))
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CommandeProduitResponseDto getComProd(Long id) {
        Commande_produit cp = comProdRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("CommandeProduit introuvable : " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!hasRole(authentication, "ROLE_ADMIN")
                && !isOwnedByCurrentUser(cp, authentication != null ? authentication.getName() : null)) {
            throw new AccessDeniedException("Vous ne pouvez consulter que vos lignes de commande");
        }

        return toDto(cp);
    }

    @Transactional(readOnly = true)
    public List<CommandeProduitResponseDto> getMyCommandeProduits() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication != null ? authentication.getName() : null;

        return StreamSupport.stream(comProdRepository.findAll().spliterator(), false)
                .filter(cp -> isOwnedByCurrentUser(cp, currentUsername))
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CommandeProduitResponseDto getMyComProd(Long id) {
        Commande_produit cp = comProdRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("CommandeProduit introuvable : " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isOwnedByCurrentUser(cp, authentication != null ? authentication.getName() : null)) {
            throw new AccessDeniedException("Vous ne pouvez consulter que vos lignes de commande");
        }

        return toDto(cp);
    }

    @Transactional
    public CommandeProduitResponseDto saveComProd(Long prodId, Long comId, Commande_produit payload) {
        ensureCartWriteAllowed();

        Produit produit = produitRepository.findById(prodId)
                .orElseThrow(() -> new RessourceNotFoundException("Produit introuvable : " + prodId));
        Commande commande = commandeRepository.findById(comId)
                .orElseThrow(() -> new RessourceNotFoundException("Commande introuvable : " + comId));

        Commande_produit entity = new Commande_produit();
        entity.setProduit(produit);
        entity.setCommande(commande);
        entity.setQuantite(payload.getQuantite());
        validateQuantite(entity.getQuantite());

        if (payload.getPrixUnitaire() != null) {
            validatePrix(payload.getPrixUnitaire());
            validatePrixCoherence(payload.getPrixUnitaire(), produit.getPrix());
            entity.setPrixUnitaire(payload.getPrixUnitaire());
        } else if (produit.getPrix() != null) {
            validatePrix(produit.getPrix());
            entity.setPrixUnitaire(produit.getPrix());
        } else {
            throw new IllegalArgumentException("prixUnitaire invalide pour le produit " + prodId);
        }
        entity.setSousTotal(entity.getPrixUnitaire() * entity.getQuantite());

        Commande_produit saved = comProdRepository.save(entity);
        syncCommandeTotal(comId);
        return toDto(saved);
    }

    @Transactional
    public CommandeProduitResponseDto updateComProd(Long id, Commande_produit payload) {
        ensureCartWriteAllowed();

        Commande_produit existing = comProdRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("CommandeProduit introuvable : " + id));

        if (payload.getQuantite() != null) {
            validateQuantite(payload.getQuantite());
            existing.setQuantite(payload.getQuantite());
        }
        if (payload.getPrixUnitaire() != null) {
            validatePrix(payload.getPrixUnitaire());
            existing.setPrixUnitaire(payload.getPrixUnitaire());
        }
        if (payload.getProduit() != null && payload.getProduit().getId() != null) {
            Produit produit = produitRepository.findById(payload.getProduit().getId())
                    .orElseThrow(() -> new RessourceNotFoundException("Produit introuvable : " + payload.getProduit().getId()));
            existing.setProduit(produit);
        }
        if (payload.getCommande() != null && payload.getCommande().getId() != null) {
            Commande commande = commandeRepository.findById(payload.getCommande().getId())
                    .orElseThrow(() -> new RessourceNotFoundException("Commande introuvable : " + payload.getCommande().getId()));
            existing.setCommande(commande);
        }
        if (existing.getProduit() != null && existing.getProduit().getPrix() != null && existing.getPrixUnitaire() != null) {
            validatePrixCoherence(existing.getPrixUnitaire(), existing.getProduit().getPrix());
        }
        if (existing.getQuantite() != null && existing.getPrixUnitaire() != null) {
            existing.setSousTotal(existing.getQuantite() * existing.getPrixUnitaire());
        }

        Commande_produit saved = comProdRepository.save(existing);
        syncCommandeTotal(saved.getCommande() != null ? saved.getCommande().getId() : null);
        return toDto(saved);
    }

    @Transactional
    public void deleteComProd(Long id) {
        ensureCartWriteAllowed();

        Commande_produit existing = comProdRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("CommandeProduit introuvable : " + id));
        Long commandeId = existing.getCommande() != null ? existing.getCommande().getId() : null;

        comProdRepository.deleteById(id);
        syncCommandeTotal(commandeId);
    }

    private void ensureCartWriteAllowed() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return;
        }
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        if (isAdmin) {
            throw new AccessDeniedException("Les administrateurs ne peuvent pas ajouter au panier");
        }
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication != null
                && authentication.getAuthorities() != null
                && authentication.getAuthorities().stream().anyMatch(a -> role.equals(a.getAuthority()));
    }

    private boolean isOwnedByCurrentUser(Commande_produit cp, String currentUsername) {
        if (cp == null || cp.getCommande() == null || cp.getCommande().getUser() == null || currentUsername == null || currentUsername.isBlank()) {
            return false;
        }
        String username = cp.getCommande().getUser().getUsername();
        String email = cp.getCommande().getUser().getEmail();
        return currentUsername.equalsIgnoreCase(username) || currentUsername.equalsIgnoreCase(email);
    }

    private void validateQuantite(Integer quantite) {
        if (quantite == null || quantite <= 0) {
            throw new IllegalArgumentException("quantite doit etre > 0");
        }
    }

    private void validatePrix(Double prix) {
        if (prix == null || prix <= 0) {
            throw new IllegalArgumentException("prixUnitaire doit etre > 0");
        }
    }

    private void validatePrixCoherence(Double prixUnitaire, Double prixProduit) {
        if (prixProduit == null || prixUnitaire == null) {
            return;
        }
        if (Math.abs(prixUnitaire - prixProduit) > 0.0001d) {
            throw new IllegalArgumentException("prixUnitaire incoherent avec le prix du produit");
        }
    }

    private void syncCommandeTotal(Long commandeId) {
        if (commandeId == null) {
            return;
        }
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RessourceNotFoundException("Commande introuvable : " + commandeId));

        List<Commande_produit> lines = comProdRepository.findByCommande_Id(commandeId);
        double total = lines.stream()
                .map(line -> line.getSousTotal() != null ? line.getSousTotal() : 0.0)
                .reduce(0.0, Double::sum);
        commande.setTotal(total);
        commandeRepository.save(commande);
    }

    private CommandeProduitResponseDto toDto(Commande_produit cp) {
        return new CommandeProduitResponseDto(
                cp.getId(),
                cp.getProduit() != null ? cp.getProduit().getId() : null,
                cp.getCommande() != null ? cp.getCommande().getId() : null,
                cp.getQuantite(),
                cp.getPrixUnitaire(),
                cp.getSousTotal()
        );
    }
}