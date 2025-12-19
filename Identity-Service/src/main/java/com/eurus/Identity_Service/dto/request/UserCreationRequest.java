package com.eurus.Identity_Service.dto.request;


import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)


public class UserCreationRequest {

    //Khong co Id vi ID tu generate
    @Size(min=3,message = "Username must be at least 3 characters")
    String username;
    @Size(min=8,message = "Password must be at least 8 characters")
    String password;
    String firstName;
    String lastName;
    LocalDate dob;

}
