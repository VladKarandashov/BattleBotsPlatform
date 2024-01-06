package ru.abradox.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    private UUID id;

    private String login;

    private String email;

    private String nickName;

    private String firstName;

    private String lastName;

    private String fullName;
}
