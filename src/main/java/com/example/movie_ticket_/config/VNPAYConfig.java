package com.example.movie_ticket_.config;


public class VNPAYConfig {
    public static final String vnp_TmnCode = "QL8I2GXS"; // Mã website của bạn
    public static final String vnp_HashSecret = "SC8JFJI3NSLCBPEXDOS1956TIOW0X462"; // Chuỗi bí mật
    public static final String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String vnp_ReturnUrl = "http://nvtcinema.id.vn/api/payment/vnpay-return";
    public static final String vnp_Version = "2.1.0";
    public static final String vnp_Command = "pay";
    public static final String orderType = "movie_ticket";
}
