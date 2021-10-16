package com.alkemy.ong.controller;

import com.alkemy.ong.dto.ContactDto;
import com.alkemy.ong.service.ContactService;
import com.alkemy.ong.service.impl.ContactServiceImpl;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

@Tag(name = "Contact")
@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
@Validated
public class ContactController {

    private final ContactService contactService;
    private final MessageSource messageSource;

    @PostMapping
    public ResponseEntity<?> saveContact(
            @Valid @RequestBody ContactDto contactDto) throws Exception {
        if(contactService.saveContact(contactDto)){
            return ResponseEntity.ok(messageSource.getMessage("entity.created", new Object[] {"Contact"}, Locale.US));
        }
        throw new Exception(messageSource.getMessage("entity.created.error", new Object[]{"Contact"}, Locale.US));
    }

    @GetMapping
    public ResponseEntity<?> getContactList () {
        List<ContactDto> response = contactService.findAllContacts();
        if(response.isEmpty()){
            return ResponseEntity.ok(messageSource.getMessage("error.entity.empty", new Object[] {"Contacts"}, Locale.US));

        }else{
            return ResponseEntity.ok(response);
        }
    }
}
