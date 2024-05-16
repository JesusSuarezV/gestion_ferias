package com.ufps.seminario.service;
import jakarta.mail.MessagingException;

public interface MailService {
    public void enviarCorreo(String destinatario, String asunto, String cuerpo) throws MessagingException;
}