package com.example.movie_ticket_.controller.admin;

import com.example.movie_ticket_.entity.Invoice;
import com.example.movie_ticket_.entity.PaymentMethod;
import com.example.movie_ticket_.repository.PaymentMethodRepository;
import com.example.movie_ticket_.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/invoices")
@RequiredArgsConstructor
public class InvoiceAdminController {

    private final InvoiceService invoiceService;
    private final PaymentMethodRepository paymentMethodRepository;

    @GetMapping
    public String listInvoices(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "movieName", required = false) String movieName,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            Model model) {
        
        // Nếu có month nhưng không có year, sử dụng năm hiện tại
        Integer searchYear = year;
        if (month != null && searchYear == null) {
            searchYear = java.time.LocalDate.now().getYear();
        }
        
        // Sử dụng method tìm kiếm mới
        List<Invoice> invoices = invoiceService.searchInvoices(status, movieName, month, searchYear);
        
        // Sắp xếp theo thứ tự phòng (Phòng 1, Phòng 2, Phòng 3...)
        invoices.sort((i1, i2) -> {
            String room1 = i1.getRoomName() != null ? i1.getRoomName() : "";
            String room2 = i2.getRoomName() != null ? i2.getRoomName() : "";
            
            // Extract số từ tên phòng (ví dụ: "Phòng 1" -> 1, "Phòng 2" -> 2)
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher1 = pattern.matcher(room1);
            Matcher matcher2 = pattern.matcher(room2);
            
            Integer num1 = null;
            Integer num2 = null;
            
            if (matcher1.find()) {
                try {
                    num1 = Integer.parseInt(matcher1.group());
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
            
            if (matcher2.find()) {
                try {
                    num2 = Integer.parseInt(matcher2.group());
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
            
            // Nếu cả hai đều có số, so sánh số
            if (num1 != null && num2 != null) {
                return num1.compareTo(num2);
            }
            
            // Nếu chỉ một trong hai có số, ưu tiên cái có số
            if (num1 != null) return -1;
            if (num2 != null) return 1;
            
            // Nếu cả hai đều không có số, sắp xếp theo alphabet
            return room1.compareToIgnoreCase(room2);
        });
        
        // Tính tổng tiền của các hóa đơn tìm được (không tính "Chờ thanh toán" và "Đã hủy")
        // Chỉ tính: status = 1 (Đã thanh toán) và status = 2 (Hoàn thành)
        java.math.BigDecimal totalAmount = invoices.stream()
                .filter(inv -> inv.getTotalPrice() != null)
                .filter(inv -> inv.getStatus() == 1 || inv.getStatus() == 2) // Chỉ tính đã thanh toán và hoàn thành
                .map(Invoice::getTotalPrice)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        
        // Kiểm tra xem có đang tìm kiếm không (có ít nhất 1 tham số tìm kiếm - không tính status)
        boolean isSearching = (movieName != null && !movieName.trim().isEmpty()) || 
                              (month != null) || 
                              (searchYear != null);
        
        // Tạo map payment method để hiển thị tên
        Map<Long, String> paymentMethodMap = paymentMethodRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        PaymentMethod::getId,
                        PaymentMethod::getNameMethod
                ));

        // ================= DOANH THU THEO PHIM =================
// Chỉ tính hóa đơn đã thanh toán và hoàn thành
        Map<String, java.math.BigDecimal> revenueByMovie = invoices.stream()
                .filter(inv -> inv.getStatus() == 1 || inv.getStatus() == 2)
                .filter(inv -> inv.getMovieName() != null)
                .filter(inv -> inv.getTotalPrice() != null)
                .collect(Collectors.groupingBy(
                        Invoice::getMovieName,
                        Collectors.reducing(
                                java.math.BigDecimal.ZERO,
                                Invoice::getTotalPrice,
                                java.math.BigDecimal::add
                        )
                ));


        // Sắp xếp doanh thu giảm dần để hiển thị dạng xếp hạng
        Map<String, java.math.BigDecimal> revenueSorted = new java.util.LinkedHashMap<>();
        revenueByMovie.entrySet().stream()
                .sorted(Map.Entry.<String, java.math.BigDecimal>comparingByValue().reversed())
                .forEach(e -> revenueSorted.put(e.getKey(), e.getValue()));
        revenueByMovie = revenueSorted;
        java.math.BigDecimal revenueMax = revenueSorted.values().stream()
                .findFirst().orElse(java.math.BigDecimal.ZERO);

        model.addAttribute("invoices", invoices);
        model.addAttribute("searchStatus", status);
        model.addAttribute("revenueMax", revenueMax);
        model.addAttribute("searchMovieName", movieName);
        model.addAttribute("searchMonth", month);
        model.addAttribute("searchYear", searchYear);
        model.addAttribute("paymentMethodMap", paymentMethodMap);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("isSearching", isSearching);
        model.addAttribute("invoiceCount", invoices.size());
        model.addAttribute("revenueByMovie", revenueByMovie);

        return "invoice/list";
    }

    @GetMapping("/{id}")
    public String viewInvoiceDetail(@PathVariable Long id, Model model) {
        Invoice invoice = invoiceService.getInvoiceById(id);
        
        // Lấy tên payment method nếu có
        final String[] paymentMethodName = {null};
        if (invoice.getPaymentMethodId() != 0) {
            paymentMethodRepository.findById(invoice.getPaymentMethodId())
                    .ifPresent(pm -> paymentMethodName[0] = pm.getNameMethod());
        }
        
        model.addAttribute("invoice", invoice);
        model.addAttribute("paymentMethodName", paymentMethodName[0]);
        return "invoice/detail";
    }
}

