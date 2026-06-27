package com.dyc.backendecommerce.store.store;

import com.dyc.backendecommerce.store.Store;
import com.dyc.backendecommerce.store.StoreRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/store/info")
@AllArgsConstructor
public class StoreInfoController {
    private final StoreRepository storeRepository;

    @GetMapping
    public ResponseEntity<StoreInfoResponse> getStoreInfo() {
        List<Store> stores = storeRepository.findAll();
        Store store = stores.stream()
                .findFirst()
                .orElse(null);

        if (store == null) {
            return ResponseEntity.ok(new StoreInfoResponse(null, null, null));
        }

        String logoPath = store.getAsset() != null
                ? "/api/asset/image/" + store.getAsset().getUuid()
                : null;

        return ResponseEntity.ok(new StoreInfoResponse(store.getName(), store.getDescription(), logoPath));
    }
}
