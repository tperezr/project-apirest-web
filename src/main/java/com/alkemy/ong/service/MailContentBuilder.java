package com.alkemy.ong.service;

import com.alkemy.ong.model.Contacts;
import com.alkemy.ong.model.User;

public interface MailContentBuilder {

	String buildWelcomeEmail(User user);
	String buildContactEmail(Contacts contact);
	
}
