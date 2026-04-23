package com.tpspringboot.apirestclientcommande.produit.modeleProd;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ligne_stocks")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LigneStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    @JsonBackReference
    private Stock stock;

    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;

    @JsonProperty("quantite_stock")
    private Integer quantiteStock;

    @JsonProperty("stock_id")
    public Long getStockId() {
        return stock != null ? stock.getId() : null;
    }

    @JsonProperty("stock_id")
    public void setStockId(Long stockId) {
        if (stockId == null) {
            this.stock = null;
            return;
        }
        Stock stock = new Stock();
        stock.setId(stockId);
        this.stock = stock;
    }

    @JsonProperty("produitId")
    public Long getProduitId() {
        return produit != null ? produit.getId() : null;
    }

    @JsonProperty("produitId")
    public void setProduitId(Long produitId) {
        if (produitId == null) {
            this.produit = null;
            return;
        }
        Produit produit = new Produit();
        produit.setId(produitId);
        this.produit = produit;
    }
}
