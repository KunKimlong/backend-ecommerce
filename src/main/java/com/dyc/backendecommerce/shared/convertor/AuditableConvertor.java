package com.dyc.backendecommerce.shared.convertor;

import com.dyc.backendecommerce.shared.entity.AuditableResult;
import com.dyc.backendecommerce.user.UserRepository;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

@Component
public class AuditableConvertor implements Converter<Long, AuditableResult> {
  private final UserRepository userRepository;

  public AuditableConvertor(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public AuditableResult convert(MappingContext<Long, AuditableResult> context) {
    Long source = context.getSource();
    if (source == null) return null;

    var user = userRepository.findById(source).orElse(null);
    if (user == null) return null;
    return AuditableResult.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
//            .gender(user.getGender())
            .build();

  }
}
