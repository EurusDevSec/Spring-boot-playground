package com.eurus.Identity_Service.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity // Đánh dấu đây là bảng dữ liệu
@Table(name="user") // Ten bảng trong MYSQL
@Data // Lombok: tu generate Getter/setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE) // Mac dinh cac bien ben duoi la private
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // tu sinh ID ngau nhien (UUID)
    String id;

    String username;
    String password;
    String firstName;
    String lastName;
    LocalDate dob; //Ngay sinh
}
