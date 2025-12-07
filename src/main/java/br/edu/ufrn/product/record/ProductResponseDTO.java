package br.edu.ufrn.product.record;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductResponseDTO(
    String id,
    String name,
    Integer quantity,
    Double price,
    @JsonProperty("created_at") @JsonFormat(shape = JsonFormat.Shape.STRING) Instant createdAt
) {}
