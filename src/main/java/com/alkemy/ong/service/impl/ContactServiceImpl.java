package com.alkemy.ong.service.impl;

import com.alkemy.ong.dto.ContactDto;
import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.model.Contacts;
import com.alkemy.ong.model.User;
import com.alkemy.ong.repository.ContactsRepository;
import com.alkemy.ong.service.ContactService;
import com.alkemy.ong.service.MailContentBuilder;
import com.alkemy.ong.service.SendGridService;
import com.sendgrid.Response;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactServiceImpl implements ContactService {
	
    private final ContactsRepository contactsRepository;
    private final SendGridService sendGridService;
    private final MailContentBuilder mailContentBuilder;
    private final MessageSource messageSource;

    @Override
    public Boolean saveContact(ContactDto contactDto) {
        Contacts contact = new Contacts(
                contactDto.getName(),
                contactDto.getPhone(),
                contactDto.getEmail(),
                contactDto.getMessage()
        );
        contactsRepository.save(contact);
        
        Response emailSendResponse = sendGridService.sendContactEmail(contact, mailContentBuilder.buildContactEmail(contact));
        log.info(messageSource.getMessage("sendgrid.status.code",
                new Object[]{emailSendResponse.getStatusCode(), emailSendResponse.getBody()},
                Locale.US));
        return true;
    }

    @Override
    public List<ContactDto> findAllContacts() {
        List<Contacts> listContacts = contactsRepository.findAll();
        List<ContactDto> listContactsDTO = new ArrayList<>();

        listContactsDTO = listContacts.stream().map(new Function<Contacts, ContactDto>() {
                                                @Override
                                                public ContactDto apply(Contacts c) {
                                                    return new ContactDto(
                                                            c.getName(),
                                                            c.getPhone(),
                                                            c.getEmail(),
                                                            c.getMessage()
                                                    );
                                                }
                                            }
        ).collect(Collectors.toList());


        return listContactsDTO;
    }


}
