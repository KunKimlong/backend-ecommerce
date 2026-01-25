package com.dyc.backendecommerce.shared.config;

import com.dyc.backendecommerce.shared.convertor.AuditableConvertor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class BeanConfig {
    private final AuditableConvertor auditableConvertor;
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(auditableConvertor);

        return modelMapper;
    }
}
