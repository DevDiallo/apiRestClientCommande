package com.tpspringboot.apirestclientcommande.Commande.serviceCO;

import com.tpspringboot.apirestclientcommande.Commande.dto.ClientDetailsDto;
import com.tpspringboot.apirestclientcommande.Commande.dto.CommandeAdminDetailsResponseDto;
import com.tpspringboot.apirestclientcommande.Commande.dto.CommandeItemResponseDto;
import com.tpspringboot.apirestclientcommande.Commande.dto.CommandeResponseDto;
import com.tpspringboot.apirestclientcommande.Commande.modeleCO.Commande;
import com.tpspringboot.apirestclientcommande.Commande.repositoryCO.CommandeRepository;
import com.tpspringboot.apirestclientcommande.Exceptions.RessourceNotFoundException;
import com.tpspringboot.apirestclientcommande.User.modeleCL.User;
import com.tpspringboot.apirestclientcommande.User.repositoryCL.CrudUserRepository;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Commande_produit;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.LigneStock;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Produit;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.LigneStockRepository;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.ProduitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandeService {

    private final CrudUserRepository crudUserRepository;
    private final CommandeRepository commandeRepository;
    private final ProduitRepository produitRepository;
    private final LigneStockRepository ligneStockRepository;

    @Transactional(readOnly = true)
    public List<CommandeResponseDto> getCommandes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        String currentUsername = authentication != null ? authentication.getName() : null;

        return StreamSupport.stream(commandeRepository.findAll().spliterator(), false)
                .filter(commande -> isAdmin || isOwnedByCurrentUser(commande, currentUsername))
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CommandeResponseDto getCommande(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Error getCommande ! Ressource not Found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!hasRole(authentication, "ROLE_ADMIN")
                && !isOwnedByCurrentUser(commande, authentication != null ? authentication.getName() : null)) {
            throw new AccessDeniedException("Vous ne pouvez consulter que vos commandes");
        }
        return toDto(commande);
    }

    @Transactional(readOnly = true)
    public List<CommandeResponseDto> getMyCommandes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication != null ? authentication.getName() : null;

        return StreamSupport.stream(commandeRepository.findAll().spliterator(), false)
                .filter(commande -> isOwnedByCurrentUser(commande, currentUsername))
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CommandeResponseDto getMyCommande(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Error getCommande ! Ressource not Found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isOwnedByCurrentUser(commande, authentication != null ? authentication.getName() : null)) {
            throw new AccessDeniedException("Vous ne pouvez consulter que vos commandes");
        }
        return toDto(commande);
    }

    @Transactional(readOnly = true)
    public CommandeAdminDetailsResponseDto getCommandeDetailsForAdmin(Long id) {
        ensureAdminReadAllowed();

        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Error getCommande details ! Ressource not Found"));

        log.info("[ADMIN_ACTION] view-order-details | admin={} | commandeId={}", getCurrentUsername(), id);
        return toAdminDetailsDto(commande);
    }

    @Transactional
    public CommandeResponseDto saveCommande(Long userId, Commande commande) {
        ensureCartWriteAllowed();

        User user = crudUserRepository.findById(userId)
                .orElseThrow(() -> new RessourceNotFoundException("Error saveCommande ! customer_id does not exist"));

        commande.setUser(user);
        if (commande.getDateValidation() == null) {
            commande.setDateValidation(LocalDateTime.now());
        }
        if (commande.getCommandeProduits() == null) {
            commande.setCommandeProduits(new java.util.ArrayList<>());
        }

        validateAndReserveStock(commande);
        commande.setTotal(calculateTotal(commande));
        user.getCommandes().add(commande);

        Commande saved = commandeRepository.save(commande);
        log.info("[ADMIN_ACTION] create-order | actor={} | commandeId={} | userId={}", getCurrentUsername(), saved.getId(), userId);
        return toDto(saved);
    }

    @Transactional
    public CommandeResponseDto updateCommande(Long id, Commande payload) {
        ensureCartWriteAllowed();

        Commande existing = commandeRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Error updateCommande ! Ressource not Found"));

        if (payload.getCommandeProduits() != null) {
            existing.setCommandeProduits(payload.getCommandeProduits());
            validateAndReserveStock(existing);
        }
        if (payload.getUser() != null) {
            existing.setUser(payload.getUser());
        }
        if (payload.getDateValidation() != null) {
            existing.setDateValidation(payload.getDateValidation());
        }

        existing.setTotal(payload.getTotal() != null ? payload.getTotal() : calculateTotal(existing));

        Commande saved = commandeRepository.save(existing);
        log.info("[ADMIN_ACTION] update-order | actor={} | commandeId={}", getCurrentUsername(), saved.getId());
        return toDto(saved);
    }

    @Transactional
    public void deleteCommande(Long id) {
        // Les admins peuvent supprimer n'importe quelle commande
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!hasRole(authentication, "ROLE_ADMIN")) {
            ensureCartWriteAllowed();
        }

        if (!commandeRepository.existsById(id)) {
            throw new RessourceNotFoundException("Error deleteCommande ! Ressource not Found");
        }
        commandeRepository.deleteById(id);
        log.info("[ADMIN_ACTION] delete-order | actor={} | commandeId={}", getCurrentUsername(), id);
    }

    private void validateAndReserveStock(Commande commande) {
        if (commande.getCommandeProduits() == null || commande.getCommandeProduits().isEmpty()) {
            return;
        }

        for (Commande_produit item : commande.getCommandeProduits()) {
            if (item.getProduit() == null || item.getProduit().getId() == null) {
                throw new IllegalArgumentException("Chaque ligne commande doit contenir un produitId valide");
            }
            if (item.getQuantite() == null || item.getQuantite() <= 0) {
                throw new IllegalArgumentException("La quantite doit etre superieure a 0");
            }

            Produit produit = produitRepository.findById(item.getProduit().getId())
                    .orElseThrow(() -> new RessourceNotFoundException("Produit introuvable : " + item.getProduit().getId()));

            if (produit.getLigneStockId() == null || produit.getLigneStockId().isBlank()) {
                throw new IllegalArgumentException("Aucun stock associe au produit : " + produit.getNom());
            }

            Long ligneStockId;
            try {
                ligneStockId = Long.parseLong(produit.getLigneStockId());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("ligneStockId invalide pour le produit : " + produit.getNom());
            }

            LigneStock ligneStock = ligneStockRepository.findById(ligneStockId)
                    .orElseThrow(() -> new RessourceNotFoundException("LigneStock introuvable : " + ligneStockId));

            int stockDisponible = ligneStock.getQuantiteStock() == null ? 0 : ligneStock.getQuantiteStock();
            if (stockDisponible < item.getQuantite()) {
                throw new IllegalArgumentException("Stock insuffisant pour le produit : " + produit.getNom());
            }

            ligneStock.setQuantiteStock(stockDisponible - item.getQuantite());
            ligneStockRepository.save(ligneStock);

            item.setCommande(commande);
            item.setProduit(produit);
            if (item.getPrixUnitaire() == null) {
                item.setPrixUnitaire(produit.getPrix());
            }
            item.setSousTotal(item.getPrixUnitaire() * item.getQuantite());
        }
    }

    private Double calculateTotal(Commande commande) {
        if (commande.getCommandeProduits() == null || commande.getCommandeProduits().isEmpty()) {
            return 0.0;
        }
        return commande.getCommandeProduits().stream()
                .map(item -> item.getSousTotal() != null ? item.getSousTotal() : 0.0)
                .reduce(0.0, Double::sum);
    }

    private CommandeResponseDto toDto(Commande commande) {
        User client = commande.getUser();
        ClientDetailsDto clientDto = client == null
                ? null
                : new ClientDetailsDto(client.getNom(), client.getPrenom(), client.getEmail(), client.getTelephone());

        List<CommandeItemResponseDto> items = commande.getCommandeProduits() == null
                ? List.of()
                : commande.getCommandeProduits().stream()
                .map(item -> new CommandeItemResponseDto(
                        item.getProduit() != null ? item.getProduit().getId() : null,
                        item.getProduit() != null ? item.getProduit().getNom() : null,
                        item.getQuantite(),
                        item.getPrixUnitaire(),
                        item.getSousTotal()
                ))
                .toList();

        return new CommandeResponseDto(
                commande.getId(),
                commande.getDateValidation(),
                commande.getTotal(),
                client != null ? client.getId() : null,
                client != null ? client.getId() : null,
                client != null ? client.getId() : null,
                clientDto,
                items,
                items
        );
    }

    private CommandeAdminDetailsResponseDto toAdminDetailsDto(Commande commande) {
        User client = commande.getUser();
        ClientDetailsDto clientDto = client == null
                ? null
                : new ClientDetailsDto(client.getNom(), client.getPrenom(), client.getEmail(), client.getTelephone());

        List<CommandeItemResponseDto> items = commande.getCommandeProduits() == null
                ? List.of()
                : commande.getCommandeProduits().stream()
                .map(item -> new CommandeItemResponseDto(
                        item.getProduit() != null ? item.getProduit().getId() : null,
                        item.getProduit() != null ? item.getProduit().getNom() : null,
                        item.getQuantite(),
                        item.getPrixUnitaire(),
                        item.getSousTotal()
                ))
                .toList();

        return new CommandeAdminDetailsResponseDto(
                commande.getId(),
                commande.getDateValidation(),
                commande.getTotal(),
                client != null ? client.getId() : null,
                clientDto,
                items
        );
    }

    private void ensureCartWriteAllowed() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return;
        }
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (isAdmin) {
            throw new AccessDeniedException("Les administrateurs ne peuvent pas ajouter au panier");
        }
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "anonymous";
    }

    private void ensureAdminReadAllowed() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            throw new AccessDeniedException("Acces admin requis");
        }
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        if (!isAdmin) {
            throw new AccessDeniedException("Acces admin requis");
        }
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication != null
                && authentication.getAuthorities() != null
                && authentication.getAuthorities().stream().anyMatch(a -> role.equals(a.getAuthority()));
    }

    private boolean isOwnedByCurrentUser(Commande commande, String currentUsername) {
        if (commande == null || commande.getUser() == null || currentUsername == null || currentUsername.isBlank()) {
            return false;
        }
        String username = commande.getUser().getUsername();
        String email = commande.getUser().getEmail();
        return currentUsername.equalsIgnoreCase(username) || currentUsername.equalsIgnoreCase(email);
    }

}