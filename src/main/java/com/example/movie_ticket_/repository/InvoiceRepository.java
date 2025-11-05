package com.example.movie_ticket_.repository;

import com.example.movie_ticket_.entity.Invoice;
import com.example.movie_ticket_.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Lấy danh sách hóa đơn theo người dùng
    List<Invoice> findByUserId(Long userId);

    // Tùy chọn thêm: tìm theo trạng thái
    List<Invoice> findByStatus(int status);
    List<Invoice> findByUser(User user);
}
