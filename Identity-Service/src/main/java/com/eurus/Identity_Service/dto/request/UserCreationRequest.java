package com.eurus.Identity_Service.dto.request;


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
    String username;
    String password;
    String firstName;
    String lastName;
    LocalDate dob;

}
