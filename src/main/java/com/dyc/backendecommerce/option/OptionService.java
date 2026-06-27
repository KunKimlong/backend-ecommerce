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
public class OptionService {
  public static final String NOT_FOUND_MESSAGE = "Option not found in list";

  private final OptionRepository optionRepository;
  private final ModelMapper modelMapper;

  private Page<Option> getAllOptionsPage(Pageable pageable) {
    return optionRepository.findAll(pageable);
  }

  @Transactional(readOnly = true)
  public ResponseData<OptionResponse> getAllOptions(String name, Pageable pageable) {
    Page<Option> options;
    if (name != null && !name.isBlank()) {
      options = optionRepository.findByNameContainingIgnoreCase(name, pageable);
    } else {
      options = getAllOptionsPage(pageable);
    }
    List<OptionResponse> optionResponses =
        options.getContent().stream()
            .map(option -> modelMapper.map(option, OptionResponse.class))
            .toList();

    return ResponseData.<OptionResponse>builder()
        .data(optionResponses)
        .page(options.getNumber())
        .pageSize(options.getSize())
        .total(options.getTotalElements())
        .build();
  }

  public OptionResponse saveOption(OptionRequest optionRequest) {
    var option = Option.builder().name(optionRequest.getName()).build();
    optionRepository.save(option);
    return modelMapper.map(option, OptionResponse.class);
  }

  public OptionResponse updateOption(Long id, OptionRequest optionRequest) {
    var option = optionRepository.findById(id).orElse(null);
    if (option != null) {
      option.setName(optionRequest.getName());
      optionRepository.save(option);
      return modelMapper.map(option, OptionResponse.class);
    } else {
      throw new NotFoundException(NOT_FOUND_MESSAGE);
    }
  }

  public void deleteOption(Long id) {
    var option = optionRepository.findById(id).orElse(null);
    if (option != null) {
      optionRepository.delete(option);
    } else {
      throw new NotFoundException(NOT_FOUND_MESSAGE);
    }
  }

  public Option getOption(Long id) {
    return optionRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
  }
}
