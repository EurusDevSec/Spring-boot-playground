package com.eurus.Identity_Service.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data // Su dung LomBok Data de tao san getter, setter
@NoArgsConstructor //Tao san constructor khong có tham số
@AllArgsConstructor//Tao constructor co tham số
@Builder// Su dung builder pattern
@FieldDefaults(level = AccessLevel.PRIVATE) // Dat prive tu dong cho tat ca biến
public class TaskDeleteRequest {

    String title;
    String description;
    String status;
    LocalDate dueDate;

}
