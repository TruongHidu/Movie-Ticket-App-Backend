package com.example.movie_ticket_.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Cau hinh VNPAY. Gia tri bi mat duoc nap tu .env / bien moi truong
 * thong qua application.properties (vnpay.*), khong hardcode trong code.
 */
@Getter
@Component
public class VNPAYConfig {
    public static final String VERSION = "2.1.0";
    public static final String COMMAND = "pay";

    @Value("${vnpay.tmn-code}")
    private String tmnCode;

    @Value("${vnpay.hash-secret}")
    private String hashSecret;

    @Value("${vnpay.url}")
    private String url;

    @Value("${vnpay.return-url}")
    private String returnUrl;

    @Value("${vnpay.order-type}")
    private String orderType;
}
