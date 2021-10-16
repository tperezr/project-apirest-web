package com.alkemy.ong.repository;

import com.alkemy.ong.model.Contacts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactsRepository extends JpaRepository <Contacts, Long> {
    public List<Contacts> findAll();
}
