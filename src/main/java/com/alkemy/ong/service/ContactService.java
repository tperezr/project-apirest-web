package com.alkemy.ong.service;

import com.alkemy.ong.dto.ContactDto;

import java.util.List;

public interface ContactService {
    Boolean saveContact(ContactDto contactDto);

    List<ContactDto> findAllContacts();
}
