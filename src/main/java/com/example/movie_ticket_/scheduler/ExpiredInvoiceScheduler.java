package com.example.movie_ticket_.scheduler;

import com.example.movie_ticket_.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpiredInvoiceScheduler {
    private final InvoiceService invoiceService;

    @Scheduled(fixedRate = 60000)
    public void updateExpriedInvoice(){
        invoiceService.expirePendingInvoices();
    }
}
