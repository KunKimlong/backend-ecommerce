package com.dyc.backendecommerce.shared.config;

import com.dyc.backendecommerce.asset.Asset;
import com.dyc.backendecommerce.asset.AssetData;
import com.dyc.backendecommerce.shared.convertor.AuditableConvertor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class BeanConfig {
  private final AuditableConvertor auditableConvertor;

  @Bean
  public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    modelMapper.addConverter(auditableConvertor);
    // Expose the public image URL, never the storage location on disk
    modelMapper
        .typeMap(Asset.class, AssetData.class)
        .addMappings(
            mapper ->
                mapper
                    .using(ctx -> "/image/" + ((Asset) ctx.getSource()).getUuid())
                    .map(src -> src, AssetData::setPath));

    return modelMapper;
  }
}
