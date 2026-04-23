# apiRestClientCommande - Backend Notes

Ce backend Spring Boot expose les APIs catalogue/commande avec securite JWT et endpoints admin.

## Swagger / OpenAPI

- OpenAPI JSON: `/v3/api-docs`
- Swagger UI: `/swagger-ui.html`

## Points backend appliques

- Details commande admin: `GET /api/commandes/{id}/details`
  - Retourne client: `nom`, `prenom`, `email`, `telephone`
  - Retourne articles: `produitId`, `nom`, `quantite`, `prixUnitaire`, `sousTotal`
- Protection admin renforcee:
  - Endpoints admin produits/stocks/categories proteges avec `ROLE_ADMIN`
  - Endpoint details commande admin protege avec `ROLE_ADMIN`
- Protection panier:
  - Ecriture panier/ligne panier interdite pour `ROLE_ADMIN` (403)
  - Verifie a la fois par regles HTTP et garde logique service
- Produit + stock:
  - Creation produit + creation/mise a jour stock dans une transaction
  - Update produit + update stock dans une transaction
  - Validation `quantite_stock >= 0`
- Erreurs API standardisees:
  - Format: `code`, `message`, `details`, `timestamp`
- Logs admin:
  - create/update/delete product
  - view order details

## Lancement local (adaptable)

```bash
./gradlew bootRun
```

## Tests (adaptable)

```bash
./gradlew test
```
