package ru.abradox.crmservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class UserEntity {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "user_seq_id", sequenceName = "user_seq_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_id")
    private Integer id;

    @Column(name = "provider_id", nullable = false, unique = true, length = 256)
    private String providerId;

    @Column(name = "login", nullable = false, length = 256)
    private String login;

    @Column(name = "email", nullable = false, length = 256)
    private String email;

    @Column(name = "nick_name", nullable = false, unique = true, length = 256)
    private String nickName;

    @Column(name = "first_name", nullable = false, length = 256)
    private String firstName;

    @Column(name = "last_name", length = 256)
    private String lastName;

    @Column(name = "full_name", nullable = false, length = 256)
    private String fullName;

    @Builder.Default
    @Column(name = "blocked")
    private boolean blocked = false;
}