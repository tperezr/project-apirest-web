package com.alkemy.ong.service.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alkemy.ong.model.Contacts;
import com.alkemy.ong.model.User;
import com.alkemy.ong.service.SendGridService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SendGridServiceImpl implements SendGridService{
	
	private SendGrid sendGrid;
	
	
	@Autowired
	public SendGridServiceImpl(SendGrid sendGrid) {
		this.sendGrid = sendGrid;
	}
	
	@Async
	@Override
	public Response sendEmail(User user,String template ) {
		
		Email from = new Email("brunoleonelolea@gmail.com");
		String subject = "Welcome to Alkemy";
		Email to = new Email(user.getEmail());
		Content content = new Content("text/html",template );
		Mail mail = new Mail(from,subject, to, content);
		
		Request request = new Request();
		Response response = null;
		
		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			response = this.sendGrid.api(request);
			log.info("Sendgrid status: {}", response.getStatusCode());
		} catch (IOException ioex) {
			log.error(ioex.getMessage());
		}
		
		return response;
	}
	
	@Async
	@Override
	public Response sendContactEmail(Contacts contact,String template ) {
		
		Email from = new Email("brunoleonelolea@gmail.com");
		String subject = "Welcome to Alkemy";
		Email to = new Email(contact.getEmail());
		Content content = new Content("text/html",template );
		Mail mail = new Mail(from,subject, to, content);
		
		Request request = new Request();
		Response response = null;
		
		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			response = this.sendGrid.api(request);
			
		} catch (IOException ioex) {
			log.error(ioex.getMessage());
		}
		
		return response;
	}

	

}
