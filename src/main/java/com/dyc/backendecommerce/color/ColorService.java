package com.dyc.backendecommerce.color;

import com.dyc.backendecommerce.shared.exception.DuplicateException;
import com.dyc.backendecommerce.shared.exception.NotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@AllArgsConstructor
public class ColorService {
  private static final String NOT_FOUND_MESSAGE = "Color not found";
  private final ColorRepository colorRepository;
  private final ModelMapper modelMapper;

  @Transactional(readOnly = true)
  public ColorResponse getAllColor(Pageable pageable) {
    Page<Color> colors = colorRepository.findAll(pageable);
    List<ColorData> colorData =
        colors.stream().map(color -> modelMapper.map(color, ColorData.class)).toList();
    return ColorResponse.builder()
            .colorData(colorData)
            .total(colors.getTotalElements())
            .page(colors.getNumber())
            .pageSize(colors.getSize())
            .build();
  }

  public ColorData saveColor(ColorRequest request) {
    var existingColor = colorRepository.findColorByCode(request.getCode());
    if (existingColor != null) {
      throw new DuplicateException("Code already exists");
    }
    var color = Color.builder().name(request.getName().toUpperCase()).code(request.getCode()).build();
    colorRepository.save(color);
    return modelMapper.map(color, ColorData.class);
  }

  public ColorData updateColor(Long id, ColorRequest colorRequest) {
    var color = colorRepository.findById(id).orElse(null);
    if (color != null) {
      color.setName(colorRequest.getName().toUpperCase());
      color.setCode(colorRequest.getCode());
      colorRepository.save(color);
      return modelMapper.map(color, ColorData.class);
    } else {
      throw new NotFoundException(NOT_FOUND_MESSAGE);
    }
  }

  public void deleteColor(Long id) {
    var color = colorRepository.findById(id).orElse(null);
    if (color != null) {
      colorRepository.delete(color);
    } else {
      throw new NotFoundException(NOT_FOUND_MESSAGE);
    }
  }
}
