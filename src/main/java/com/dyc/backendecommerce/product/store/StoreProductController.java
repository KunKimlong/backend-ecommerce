package com.dyc.backendecommerce.product.store;

import com.dyc.backendecommerce.product.admin.ProductResponse;
import com.dyc.backendecommerce.product.ProductService;
import com.dyc.backendecommerce.shared.util.ResponseData;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/store/product")
@AllArgsConstructor
public class StoreProductController {
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

    @GetMapping
    public ResponseEntity<ResponseData<List<ProductResponse>>> getAllProducts(){
        return null;
    }

}
