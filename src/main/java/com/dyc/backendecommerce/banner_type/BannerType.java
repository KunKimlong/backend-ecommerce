package com.dyc.backendecommerce.banner_type;

import java.util.UUID;
import com.dyc.backendecommerce.shared.entity.UuidV7;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "banner_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BannerType {
    @Id
    @UuidV7
    private UUID id;

    private String name;
}
