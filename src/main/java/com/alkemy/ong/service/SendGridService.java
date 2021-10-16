package com.alkemy.ong.service;

import com.alkemy.ong.model.Contacts;
import com.alkemy.ong.model.User;
import com.sendgrid.Response;

public interface SendGridService {
	
	Response sendEmail(User user, String template);

	Response sendContactEmail(Contacts contact, String template);
}
