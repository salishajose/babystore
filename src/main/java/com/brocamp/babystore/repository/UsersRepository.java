package com.brocamp.babystore.repository;

import com.brocamp.babystore.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users,Long> {
    boolean existsByEmail(String email);

    Users findByEmail(String username);

    Users findById(long userId);
}
