package com.example.movie_ticket_.repository;

import com.example.movie_ticket_.entity.Invoice;
import com.example.movie_ticket_.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    // Tìm kiếm theo tên phim (không phân biệt hoa thường)
    @Query("SELECT i FROM Invoice i WHERE LOWER(i.movieName) LIKE LOWER(CONCAT('%', :movieName, '%'))")
    List<Invoice> findByMovieNameContainingIgnoreCase(@Param("movieName") String movieName);

    // Tìm kiếm theo tháng và năm
    @Query("SELECT i FROM Invoice i WHERE MONTH(i.showDate) = :month AND YEAR(i.showDate) = :year")
    List<Invoice> findByMonthAndYear(@Param("month") int month, @Param("year") int year);

    // Tìm kiếm kết hợp: tên phim và tháng/năm
    @Query("SELECT i FROM Invoice i WHERE LOWER(i.movieName) LIKE LOWER(CONCAT('%', :movieName, '%')) AND MONTH(i.showDate) = :month AND YEAR(i.showDate) = :year")
    List<Invoice> findByMovieNameAndMonthYear(@Param("movieName") String movieName, @Param("month") int month, @Param("year") int year);

    // Tìm kiếm kết hợp: status và tên phim
    @Query("SELECT i FROM Invoice i WHERE i.status = :status AND LOWER(i.movieName) LIKE LOWER(CONCAT('%', :movieName, '%'))")
    List<Invoice> findByStatusAndMovieName(@Param("status") int status, @Param("movieName") String movieName);

    // Tìm kiếm kết hợp: status, tên phim và tháng/năm
    @Query("SELECT i FROM Invoice i WHERE i.status = :status AND LOWER(i.movieName) LIKE LOWER(CONCAT('%', :movieName, '%')) AND MONTH(i.showDate) = :month AND YEAR(i.showDate) = :year")
    List<Invoice> findByStatusAndMovieNameAndMonthYear(@Param("status") int status, @Param("movieName") String movieName, @Param("month") int month, @Param("year") int year);


    boolean existsBySchedule_IdLichChieu(Long scheduleId);

    // ===== Thống kê cho dashboard admin =====
    long countByStatus(int status);

    List<Invoice> findTop6ByOrderByCreatedAtDesc();

    List<Invoice> findByStatusInAndCreatedAtGreaterThanEqual(List<Integer> statuses, LocalDateTime from);

    @Query("SELECT COALESCE(SUM(i.totalPrice), 0) FROM Invoice i WHERE i.status IN (1, 2)")
    BigDecimal sumPaidRevenue();

    @Query("SELECT COALESCE(SUM(i.totalPrice), 0) FROM Invoice i WHERE i.status IN (1, 2) AND i.createdAt >= :from")
    BigDecimal sumPaidRevenueSince(@Param("from") LocalDateTime from);
}
