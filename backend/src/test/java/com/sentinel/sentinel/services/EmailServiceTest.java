package com.sentinel.sentinel.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setup() throws Exception {
        Field emailField = EmailService.class.getDeclaredField("email");
        emailField.setAccessible(true);
        emailField.set(emailService, "noreply@test.com");
    }

    @Test
    @DisplayName("sendEmail should send a simple mail message")
    void sendEmailShouldSendMessage() {
        emailService.sendEmail("to@test.com", "Subject line", "Hello body");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sent = captor.getValue();
        assertNotNull(sent);
        assertEquals("noreply@test.com", sent.getFrom());
        assertEquals("to@test.com", sent.getTo()[0]);
        assertEquals("Subject line", sent.getSubject());
        assertEquals("Hello body", sent.getText());
    }
}
