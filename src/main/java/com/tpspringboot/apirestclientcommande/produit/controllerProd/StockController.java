package com.tpspringboot.apirestclientcommande.produit.controllerProd;

import com.tpspringboot.apirestclientcommande.produit.dto.StockResponseDto;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Stock;
import com.tpspringboot.apirestclientcommande.produit.serviceProd.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@RestController
@RequestMapping({"/", "/api"})
public class StockController {

    private final StockService stockService;

    @GetMapping("/stocks")
    public ResponseEntity<List<StockResponseDto>> getAll() {
        List<StockResponseDto> payload = StreamSupport.stream(stockService.getStocks().spliterator(), false)
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/stocks/{id}")
    public ResponseEntity<StockResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(stockService.getStock(id)));
    }

    @PostMapping("/stocks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StockResponseDto> create(@RequestBody Stock stock) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(stockService.saveStock(stock)));
    }

    @PutMapping("/stocks/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StockResponseDto> update(@PathVariable Long id, @RequestBody Stock stock) {
        return ResponseEntity.ok(toDto(stockService.updateStock(id, stock)));
    }

    @DeleteMapping("/stocks/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }

    private StockResponseDto toDto(Stock stock) {
        return new StockResponseDto(stock.getId(), stock.getDateStock());
    }
}
