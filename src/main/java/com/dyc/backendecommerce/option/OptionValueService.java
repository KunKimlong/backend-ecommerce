package com.dyc.backendecommerce.option;

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
public class OptionValueService {
  public static final String NOT_FOUND_MESSAGE = "Option value not found in list";
  public static final String OPTION_NOT_FOUND_MESSAGE = "Parent Option not found";

  private final OptionValueRepository optionValueRepository;
  private final OptionRepository optionRepository;
  private final ModelMapper modelMapper;

  private Page<OptionValue> getAllOptionValuesPage(Pageable pageable) {
    return optionValueRepository.findAll(pageable);
  }

  @Transactional(readOnly = true)
  public ResponseData<OptionValueResponse> getAllOptionValues(Pageable pageable) {
    Page<OptionValue> optionValues = getAllOptionValuesPage(pageable);
    List<OptionValueResponse> responses =
        optionValues.getContent().stream()
            .map(val -> modelMapper.map(val, OptionValueResponse.class))
            .toList();

    return ResponseData.<OptionValueResponse>builder()
        .data(responses)
        .page(optionValues.getNumber())
        .pageSize(optionValues.getSize())
        .total(optionValues.getTotalElements())
        .build();
  }

  public OptionValueResponse saveOptionValue(OptionValueRequest request) {
    var option = optionRepository.findById(request.getOptionId())
        .orElseThrow(() -> new NotFoundException(OPTION_NOT_FOUND_MESSAGE));

    var optionValue = OptionValue.builder()
        .name(request.getName())
        .option(option)
        .build();

    optionValueRepository.save(optionValue);
    return modelMapper.map(optionValue, OptionValueResponse.class);
  }

  public OptionValueResponse updateOptionValue(Long id, OptionValueRequest request) {
    var optionValue = optionValueRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

    var option = optionRepository.findById(request.getOptionId())
        .orElseThrow(() -> new NotFoundException(OPTION_NOT_FOUND_MESSAGE));

    optionValue.setName(request.getName());
    optionValue.setOption(option);
    optionValueRepository.save(optionValue);

    return modelMapper.map(optionValue, OptionValueResponse.class);
  }

  public void deleteOptionValue(Long id) {
    var optionValue = optionValueRepository.findById(id).orElse(null);
    if (optionValue != null) {
      optionValueRepository.delete(optionValue);
    } else {
      throw new NotFoundException(NOT_FOUND_MESSAGE);
    }
  }
}
