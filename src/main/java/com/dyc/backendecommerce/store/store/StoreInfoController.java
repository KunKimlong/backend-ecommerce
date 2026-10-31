package com.dyc.backendecommerce.store.store;

import com.dyc.backendecommerce.store.StoreService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store/info")
@AllArgsConstructor
public class StoreInfoController {
    private final StoreService storeService;

    @GetMapping
    public ResponseEntity<StoreInfoResponse> getStoreInfo() {
        return ResponseEntity.ok(storeService.getStoreInfo());
    }
}
