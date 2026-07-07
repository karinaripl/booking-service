package com.booking.auth_service.repository;

import com.booking.auth_service.entity.User;
import com.booking.model.generated.auth.tables.Users;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public class UserRepository {

    private final DSLContext dsl;

    public UserRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public User save(User user) {
        var record = dsl.insertInto(Users.USERS)
                .set(Users.USERS.USERNAME, user.getUsername())
                .set(Users.USERS.EMAIL, user.getEmail())
                .set(Users.USERS.PASSWORD_HASH, user.getPasswordHash())
                .returning()
                .fetchOne();

        return User.builder()
                .id(record.getId())
                .username(record.getUsername())
                .email(record.getEmail())
                .passwordHash(record.getPasswordHash())
                .build();
    }

    public Optional<User> findByEmail(String email) {
        return dsl.selectFrom(Users.USERS)
                .where(Users.USERS.EMAIL.eq(email))
                .fetchOptional()
                .map(record -> User.builder()
                        .id(record.getId())
                        .username(record.getUsername())
                        .email(record.getEmail())
                        .passwordHash(record.getPasswordHash())
                        .build());
    }
}