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
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Lấy danh sách hóa đơn theo người dùng
    List<Invoice> findByUserId(Long userId);
    List<Invoice> findByStatus(int status);


    // Tùy chọn thêm: tìm theo trạng thái
    List<Invoice> findByUserIdAndStatusInOrderByPaymentTimeDesc(Long userId, List<Integer> statusList);
    List<Invoice> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Invoice> findByUser(User user);

    @Query(value = """
SELECT * FROM invoice i
WHERE i.status = 1
  AND CAST(CONCAT(i.show_date, ' ', i.show_time) AS DATETIME)
      <= :threshold
""", nativeQuery = true)
    List<Invoice> findInvoicesNeedProcess(
            @Param("threshold") LocalDateTime threshold
    );




    @Query(value = """
    SELECT * FROM invoice 
    WHERE status = 0
      AND DATE_ADD(created_at, INTERVAL 20 MINUTE) < NOW()
""", nativeQuery = true)
    List<Invoice> findExpiredPendingInvoices();


    Optional<Invoice> findByIdAndUserEmail(Long id, String email);

}
