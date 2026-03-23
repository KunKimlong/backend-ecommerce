package com.dyc.backendecommerce.color;

import com.dyc.backendecommerce.shared.exception.DuplicateException;
import com.dyc.backendecommerce.shared.exception.NotFoundException;
import com.dyc.backendecommerce.shared.util.ResponseData;
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
  public static final String NOT_FOUND_MESSAGE = "Color not found";
  private static final String DUPLICATE_CODE_MESSAGE = "Code already exists";
  private final ColorRepository colorRepository;
  private final ModelMapper modelMapper;

  @Transactional(readOnly = true)
  public ResponseData<ColorResponse> getAllColor(Pageable pageable) {
    Page<Color> colors = colorRepository.findAll(pageable);
    List<ColorResponse> colorData =
        colors.stream().map(color -> modelMapper.map(color, ColorResponse.class)).toList();

    return ResponseData.<ColorResponse>builder()
        .data(colorData)
        .total(colors.getTotalElements())
        .page(colors.getNumber())
        .pageSize(colors.getSize())
        .build();
  }

  public ColorResponse saveColor(ColorRequest request) {
    var existingColor = colorRepository.findColorByCode(request.getCode());
    if (existingColor != null) {
      throw new DuplicateException(DUPLICATE_CODE_MESSAGE);
    }
    var color =
        Color.builder().name(request.getName().toUpperCase()).code(request.getCode()).build();
    colorRepository.save(color);
    return modelMapper.map(color, ColorResponse.class);
  }

  public ColorResponse updateColor(Long id, ColorRequest colorRequest) {
    var color = colorRepository.findById(id).orElse(null);
    if (color != null) {
      color.setName(colorRequest.getName().toUpperCase());
      color.setCode(colorRequest.getCode());
      colorRepository.save(color);
      return modelMapper.map(color, ColorResponse.class);
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
