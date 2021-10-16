package com.alkemy.ong.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alkemy.ong.model.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    public Role findByName(String name);
}
