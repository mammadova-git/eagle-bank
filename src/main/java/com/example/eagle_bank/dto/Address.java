package com.example.eagle_bank.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Address {

    @NotBlank(message = "Line1 is required")
    private String line1;

    private String line2;

    private String line3;

    @NotBlank(message = "Town is required")
    private String town;

    @NotBlank(message = "County is required")
    private String county;

    @NotBlank(message = "Postcode is required")
    private String postcode;
}
