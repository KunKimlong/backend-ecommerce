package com.dyc.backendecommerce.banner_type;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BannerTypeRepository extends JpaRepository<BannerType, UUID> {}
