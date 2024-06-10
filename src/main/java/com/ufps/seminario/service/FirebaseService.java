package com.ufps.seminario.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FirebaseService {

    public String uploadFile(MultipartFile multipartFile) throws IOException;
}
