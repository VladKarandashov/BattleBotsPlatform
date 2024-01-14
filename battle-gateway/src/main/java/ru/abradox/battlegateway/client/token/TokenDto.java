package ru.abradox.battlegateway.client.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {

    private String id;

    private Integer userId;

    private String title;

    private String type;

    private boolean blocked;
}