package ru.abradox.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    private Integer id;

    private String login;

    private String email;

    private String nickName;

    private String firstName;

    private String lastName;

    private String fullName;
}
