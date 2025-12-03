package com.example.movie_ticket_.repository;

import com.example.movie_ticket_.entity.Invoice;
import com.example.movie_ticket_.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Lấy danh sách hóa đơn theo người dùng
    List<Invoice> findByUserId(Long userId);

    // Tùy chọn thêm: tìm theo trạng thái
    List<Invoice> findByUserIdAndStatusOrderByPaymentTimeDesc(Long userId, int status);
    List<Invoice> findByUser(User user);

    @Query(value = """
    SELECT * FROM invoice i
    WHERE i.status = 1
      AND CAST(CONCAT(i.show_date, ' ', i.show_time) AS DATETIME) 
          BETWEEN :now AND :threshold
""", nativeQuery = true)
    List<Invoice> findInvoicesForNotify(
            @Param("now") LocalDateTime now,
            @Param("threshold") LocalDateTime threshold
    );

}
