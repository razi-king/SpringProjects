package com.example.practice.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Async
public class EmailService {
	private final JavaMailSender mailSender;
	public void sendEmail(String to,String subject,String message) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom("raziinsane@gmail.com");
		mailMessage.setTo(to);
		mailMessage.setSubject(subject);
		mailMessage.setText(message);
		mailSender.send(mailMessage);
	}
}
