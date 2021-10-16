package com.alkemy.ong.service.impl;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import com.alkemy.ong.exception.EntityNotFoundException;
import com.alkemy.ong.model.Contacts;
import com.alkemy.ong.model.Organization;
import com.alkemy.ong.model.User;
import com.alkemy.ong.repository.OrganizationRepository;

import com.alkemy.ong.service.MailContentBuilder;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MailContentBuilderImpl implements MailContentBuilder {

	private final OrganizationRepository organizationRepository;
	private final TemplateEngine templateEngine;
	private final MessageSource messageSource;
	
	@Override
	public String buildWelcomeEmail(User user) {
		String error = messageSource.getMessage("error.entity.not-found", new Object[] {"Organization"}, Locale.US);
		Organization ong = organizationRepository.findAll().stream().findFirst().orElseThrow(() -> new EntityNotFoundException(error));
		Context context = new Context();
		context.setVariable("logo", ong.getImage());
		context.setVariable("title", messageSource.getMessage("email.welcome.title", null, Locale.US));
		context.setVariable("message", messageSource.getMessage("email.welcome.message", new Object[] {user.getFirstName(), ong.getName()}, Locale.US));
		context.setVariable("contact", messageSource.getMessage("email.welcome.contact", new Object[] {ong.getEmail(), String.valueOf(ong.getPhone())}, Locale.US));
		return templateEngine.process("welcome-email", context);
	}
	
	public String buildContactEmail(Contacts contact) {
		String error = messageSource.getMessage("error.entity.not-found", new Object[] {"Organization"}, Locale.US);
		Organization ong = organizationRepository.findAll().stream().findFirst().orElseThrow(() -> new EntityNotFoundException(error));
		Context context = new Context();
		context.setVariable("logo", ong.getImage());
		context.setVariable("title", messageSource.getMessage("email.contact.title", null, Locale.US));
		context.setVariable("message", messageSource.getMessage("email.contact.message", new Object[] {contact.getName(), ong.getName()}, Locale.US));
		return templateEngine.process("contact-email", context);
	}

}
