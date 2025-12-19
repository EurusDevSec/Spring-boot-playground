package com.eurus.Identity_Service.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class TaskCreationRequest {

    @NotBlank
    String title;
    String description;
    String status;
    LocalDate dueDate;

}
