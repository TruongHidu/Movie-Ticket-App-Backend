package com.example.movie_ticket_.controller.admin;

import com.example.movie_ticket_.entity.Invoice;
import com.example.movie_ticket_.repository.InvoiceRepository;
import com.example.movie_ticket_.repository.MovieRepository;
import com.example.movie_ticket_.repository.ScheduleRepository;
import com.example.movie_ticket_.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private static final List<Integer> PAID_STATUSES = List.of(1, 2);
    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("dd/MM");

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final InvoiceRepository invoiceRepository;

    @Getter
    @AllArgsConstructor
    public static class DayRevenue {
        private final String weekday;
        private final String date;
        private final BigDecimal amount;
        private final int percent;
        private final boolean today;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        LocalDate today = LocalDate.now();

        // ---- Số liệu tổng quan ----
        model.addAttribute("movieTotal", movieRepository.count());
        model.addAttribute("movieActive", movieRepository.findByStatus(1).size());
        model.addAttribute("userTotal", userRepository.count());
        model.addAttribute("scheduleToday", scheduleRepository.findActiveSchedulesByDateNoTime(today, 1).size());
        model.addAttribute("invoiceTotal", invoiceRepository.count());
        model.addAttribute("invoicePending", invoiceRepository.countByStatus(0));
        model.addAttribute("revenueTotal", invoiceRepository.sumPaidRevenue());
        model.addAttribute("revenueMonth", invoiceRepository.sumPaidRevenueSince(today.withDayOfMonth(1).atStartOfDay()));

        // ---- Doanh thu 7 ngày gần nhất ----
        LocalDate from = today.minusDays(6);
        Map<LocalDate, BigDecimal> byDay = new TreeMap<>();
        for (int i = 0; i < 7; i++) byDay.put(from.plusDays(i), BigDecimal.ZERO);
        for (Invoice inv : invoiceRepository.findByStatusInAndCreatedAtGreaterThanEqual(PAID_STATUSES, from.atStartOfDay())) {
            if (inv.getTotalPrice() == null || inv.getCreatedAt() == null) continue;
            byDay.computeIfPresent(inv.getCreatedAt().toLocalDate(), (d, v) -> v.add(inv.getTotalPrice()));
        }
        BigDecimal max = byDay.values().stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal weekTotal = byDay.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        List<DayRevenue> chart = new ArrayList<>();
        byDay.forEach((day, amount) -> {
            int percent = max.signum() == 0 ? 0
                    : amount.multiply(BigDecimal.valueOf(100)).divide(max, 0, java.math.RoundingMode.HALF_UP).intValue();
            chart.add(new DayRevenue(weekdayVi(day.getDayOfWeek()), day.format(DAY_FMT), amount, percent, day.equals(today)));
        });
        model.addAttribute("revenueChart", chart);
        model.addAttribute("revenueWeek", weekTotal);

        // ---- Hóa đơn gần đây ----
        model.addAttribute("recentInvoices", invoiceRepository.findTop6ByOrderByCreatedAtDesc());

        return "dashboard";
    }

    private static String weekdayVi(DayOfWeek d) {
        return switch (d) {
            case MONDAY -> "T2";
            case TUESDAY -> "T3";
            case WEDNESDAY -> "T4";
            case THURSDAY -> "T5";
            case FRIDAY -> "T6";
            case SATURDAY -> "T7";
            case SUNDAY -> "CN";
        };
    }
}
