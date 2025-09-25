package com.example.movie_ticket_.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dygg9cgrw",
                "api_key", "655477743245821",
                "api_secret", "pwFAEj-qcpdiSjQPQdQm4Os1Ts0",
                "secure", true
        ));
    }
}
