package com.dyc.backendecommerce.color;

import com.dyc.backendecommerce.shared.exception.DuplicateException;
import com.dyc.backendecommerce.shared.exception.NotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
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
  public List<ColorResponse> getAllColor(Pageable pageable) {
    List<Color> colors = colorRepository.findAll(pageable).getContent();
    return colors.stream().map(color -> modelMapper.map(color, ColorResponse.class)).toList();
  }

  public ColorResponse saveColor(ColorRequest request) {
    var existingColor = colorRepository.findColorByCode(request.getCode());
    if (existingColor != null) {
      throw new DuplicateException("Code already exists");
    }
    Color color = Color.builder().name(request.getName()).code(request.getCode()).build();
    colorRepository.save(color);
    return modelMapper.map(color, ColorResponse.class);
  }

  public ColorResponse updateColor(Long id, ColorRequest colorRequest) {
    Color color = colorRepository.findById(id).orElse(null);
    if (color != null) {
      color.setName(colorRequest.getName());
      color.setCode(colorRequest.getCode());
      colorRepository.save(color);
      return modelMapper.map(color, ColorResponse.class);
    } else {
      throw new NotFoundException(NOT_FOUND_MESSAGE);
    }
  }

  public ColorResponse deleteColor(Long id) {
    Color color = colorRepository.findById(id).orElse(null);
    if (color != null) {
      colorRepository.delete(color);
      return modelMapper.map(color, ColorResponse.class);
    } else {
      throw new NotFoundException(NOT_FOUND_MESSAGE);
    }
  }
}
