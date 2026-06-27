package com.dyc.backendecommerce.asset;

import com.dyc.backendecommerce.shared.enums.AssetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    Asset findByUuid(UUID uuid);

    Page<Asset> findAllByAssetType(AssetType assetType, Pageable pageable);

    Page<Asset> findAllByAssetTypeAndNameContainingIgnoreCase(AssetType assetType, String name, Pageable pageable);
}
