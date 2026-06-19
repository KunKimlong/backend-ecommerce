package com.dyc.backendecommerce.banner_type;

import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class BannerTypeService {
  private final BannerTypeRepository bannerTypeRepository;
  private final ModelMapper modelMapper;

  @Transactional(readOnly = true)
  public List<BannerTypeResponse> findAll() {
    return bannerTypeRepository.findAll().stream()
        .map(
            type -> {
              BannerTypeResponse response = modelMapper.map(type, BannerTypeResponse.class);
              response.setName(response.getName().toUpperCase());
              return response;
            })
        .toList();
  }
}
