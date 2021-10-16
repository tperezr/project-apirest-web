package com.alkemy.ong.repository;

import com.alkemy.ong.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <User, Long> {
    public List<User> findAll();
    public Optional<User> findByEmail(String email);
}
