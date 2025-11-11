package com.example.movie_ticket_.service;

import com.example.movie_ticket_.config.VNPAYConfig;
import com.example.movie_ticket_.dto.InvoiceRequest;
import com.example.movie_ticket_.entity.*;
import com.example.movie_ticket_.repository.*;
import com.example.movie_ticket_.security.VNPayUtil;
import com.example.movie_ticket_.util.BarcodeGenerator;
import com.example.movie_ticket_.util.ByteArrayMultipartFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final SeatStatusRepository seatStatusRepository;
    private final BarcodeGenerator barcodeGenerator;
    private final CloudinaryService cloudinaryService;


    public List<String> checkSeatsAvailability(Long scheduleId, List<String> seatCodes) {
        List<SeatStatus> seatStatuses = seatStatusRepository.findByScheduleAndSeatCodes(scheduleId, seatCodes);
        LocalDateTime now = LocalDateTime.now();

        return seatStatuses.stream()
                .filter(s -> s.getStatus() == 1 ||
                        (s.getStatus() == 2 && s.getHoldUntil() != null && s.getHoldUntil().isAfter(now)))
                .map(s -> s.getSeatNumber().getId().getIdGhe())
                .collect(Collectors.toList());
    }


    public void updateSeatStatus(Long scheduleId, List<String> seatCodes, Long userId) {
        List<SeatStatus> seatStatuses = seatStatusRepository.findByScheduleAndSeatCodes(scheduleId, seatCodes);
        LocalDateTime now = LocalDateTime.now();

        for (SeatStatus seatStatus : seatStatuses) {
            // Chỉ cập nhật ghế trống (status = 0) hoặc ghế đang giữ nhưng hết hạn
            if (seatStatus.getStatus() == 0 ||
                    (seatStatus.getStatus() == 2 && seatStatus.getHoldUntil() != null && seatStatus.getHoldUntil().isBefore(now))) {

                seatStatus.setStatus(2); // Đang giữ
                seatStatus.setHoldUntil(now.plusMinutes(15)); // Giữ trong 15 phút
                seatStatus.setHoldBy(userId);
                seatStatusRepository.save(seatStatus);
            }
        }
    }

    public void releaseSeats(Long scheduleId, List<String> seatCodes) {
        List<SeatStatus> seatStatuses = seatStatusRepository.findByScheduleAndSeatCodes(scheduleId, seatCodes);

        for (SeatStatus seatStatus : seatStatuses) {
            if (seatStatus.getStatus() == 2) { // Chỉ cập nhật ghế đang giữ
                seatStatus.setStatus(0); // Trở về trạng thái trống
                seatStatus.setHoldUntil(null);
                seatStatus.setHoldBy(null);
                seatStatusRepository.save(seatStatus);
            }
        }
    }


    public Invoice createInvoice(InvoiceRequest request) {
        // 1️⃣ Tìm user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        // 2️⃣ Tìm lịch chiếu
        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch chiếu"));

        // 3️⃣ Tìm phương thức thanh toán
        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy payment"));

        // 4️⃣ Cập nhật trạng thái ghế
        List<String> seatCodeList = Arrays.asList(request.getSeatCodes().split(","));
        updateSeatStatus(request.getScheduleId(), seatCodeList, request.getUserId());

        // 5️⃣ Điểm thưởng
        int usedPoint = request.getUsedPoint();
        int rewardPoints = request.getTotalPrice().intValue() / 100; // Ví dụ: 1% tổng giá

        // 6️⃣ Tạo đối tượng Invoice
        Invoice invoice = Invoice.builder()
                .user(user)
                .schedule(schedule)
                .movieName(schedule.getMovie().getTenPhim())
                .roomName(schedule.getRoom().getTenPhongChieu())
                .showDate(schedule.getNgayChieu())
                .showTime(schedule.getShowTime().getKhungGio())
                .seatCodes(request.getSeatCodes())
                .foodDrinks(request.getFoodDrinks())
                .paymentMethodId(paymentMethod.getId())
                .totalPrice(request.getTotalPrice())
                .usedPoint(usedPoint)
                .createdAt(LocalDateTime.now())
                .bonusPoint(rewardPoints)
                .build();

        // 7️⃣ Lưu trước để có ID (hoặc transaction_no)
        invoice = invoiceRepository.save(invoice);

        try {
            // 8️⃣ Sinh mã barcode từ ID hoặc mã hóa đơn
            String barcodeText = "INV-" + invoice.getId() + "-" + System.currentTimeMillis();

            byte[] barcodeBytes = barcodeGenerator.generateBarcodeImage(barcodeText);

            // 9️⃣ Chuyển sang MultipartFile để upload Cloudinary
            MultipartFile barcodeFile = new ByteArrayMultipartFile(
                    "barcode",
                    "barcode.png",
                    "image/png",
                    barcodeBytes
            );


            // 10️⃣ Upload lên Cloudinary (thư mục barcodes)
            Map uploadResult = cloudinaryService.uploadToFolder(barcodeFile, "barcodes");
            String barcodeUrl = uploadResult.get("secure_url").toString();

            // 11️⃣ Cập nhật lại hóa đơn với link barcode
            invoice.setBarcodeUrl(barcodeUrl);
            invoiceRepository.save(invoice);

        } catch (Exception e) {
            e.printStackTrace();
            // Có thể log hoặc ném lỗi tuỳ ý
        }

        return invoice;
    }

    public List<Invoice> getInvoicesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        return invoiceRepository.findByUser(user);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public void deleteInvoice(Long id) {
        invoiceRepository.deleteById(id);
    }

    public Map<String, String> createInvoiceAndPaymentWithVNPay(InvoiceRequest request) throws Exception {
        // 1️⃣ Tạo hóa đơn pending
        Invoice invoice = createInvoice(request); // dùng logic bạn đã có
        invoice.setStatus(0); // PENDING
        invoiceRepository.save(invoice);



        List<String> seatCodes = List.of(invoice.getSeatCodes().split(","));
        List<SeatStatus> seatStatuses = seatStatusRepository.findByScheduleAndSeatCodes(invoice.getSchedule().getIdLichChieu(), seatCodes);
        for (SeatStatus s : seatStatuses) {
            s.setStatus(2); // 2 = đã giữ
            s.setHoldBy(invoice.getId());
            s.setHoldUntil(LocalDateTime.now().plusMinutes(20));
        }


        // 2️⃣ Tạo URL VNPay
        String vnp_TxnRef = String.valueOf(invoice.getId()); // mã hóa đơn
        String vnp_TmnCode = VNPAYConfig.vnp_TmnCode;
        String vnp_HashSecret = VNPAYConfig.vnp_HashSecret;
        String vnp_Url = VNPAYConfig.vnp_Url;
        String vnp_ReturnUrl = VNPAYConfig.vnp_ReturnUrl;

        String vnp_Version = VNPAYConfig.vnp_Version;
        String vnp_Command = VNPAYConfig.vnp_Command;
        String orderType = VNPAYConfig.orderType;
        String amount = String.valueOf(invoice.getTotalPrice().multiply(BigDecimal.valueOf(100)).intValue());

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", amount);
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan ve xem phim #" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
        vnp_Params.put("vnp_CreateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        // Sắp xếp và tạo hash
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII)).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (!fieldName.equals(fieldNames.get(fieldNames.size() - 1))) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        String vnp_SecureHash = VNPayUtil.hmacSHA512(vnp_HashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        String paymentUrl = vnp_Url + "?" + query.toString();

        // 3️⃣ Trả kết quả
        Map<String, String> result = new HashMap<>();
        result.put("invoiceId", String.valueOf(invoice.getId()));
        result.put("paymentUrl", paymentUrl);
        return result;
    }
}
