package com.ticket.TicketSystem.mail;

// Java Program to Illustrate Creation Of
// Service Interface

// Interface

import com.ticket.TicketSystem.entities.EmailDetails;

public interface EmailService {

	// Method
	// To send a simple email
	String sendSimpleMail(EmailDetails details);

	// Method
	// To send an email with attachment
	String sendMailWithAttachment(EmailDetails details);
}
