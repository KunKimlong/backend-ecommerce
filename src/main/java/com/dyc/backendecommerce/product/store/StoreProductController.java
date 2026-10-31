package com.dyc.backendecommerce.product.store;

import com.dyc.backendecommerce.product.ProductService;
import com.dyc.backendecommerce.shared.util.ResponseData;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/store/product")
@AllArgsConstructor
public class StoreProductController {
    private static final int MAX_PAGE_SIZE = 48;
    private static final int MAX_SUGGESTIONS = 10;
    private static final int MAX_QUERY_LENGTH = 100;
    private final ProductService productService;
    @GetMapping("/new-arrival")
    public ResponseEntity<ResponseData<ProductData>> getNewArrivalProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "false") boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return new ResponseEntity<>(productService.getNewArrivalProducts(pageable), HttpStatus.OK);
    }

    @GetMapping("/best-seller")
    public ResponseEntity<List<ProductData>> getBestSellers(@RequestParam(defaultValue = "6") int size) {
        return new ResponseEntity<>(
                productService.getBestSellers(Math.min(Math.max(size, 1), MAX_PAGE_SIZE)), HttpStatus.OK);
    }

    @GetMapping("/suggest")
    public ResponseEntity<List<ProductData>> getSearchSuggestions(
            @RequestParam String q,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue = "6") int size) {
        if (q.isBlank() || q.length() > MAX_QUERY_LENGTH) {
            return new ResponseEntity<>(List.of(), HttpStatus.OK);
        }
        var filter = new StoreProductFilter(q, categoryId, null, null);
        return new ResponseEntity<>(
                productService.getSearchSuggestions(filter, Math.min(Math.max(size, 1), MAX_SUGGESTIONS)),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductData> getProductById(@PathVariable UUID id) {
        return new ResponseEntity<>(productService.getStoreProductById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ResponseData<ProductData>> getProducts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "NEWEST") StoreProductSort sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable =
                PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), MAX_PAGE_SIZE), sort.toSort());
        var filter = new StoreProductFilter(q, categoryId, minPrice, maxPrice);
        return new ResponseEntity<>(productService.getStoreProducts(filter, pageable), HttpStatus.OK);
    }

}
