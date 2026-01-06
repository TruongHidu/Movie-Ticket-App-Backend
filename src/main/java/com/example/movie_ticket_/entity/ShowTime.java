    package com.example.movie_ticket_.entity;

    import java.sql.Time;
    import java.time.LocalTime;

    import jakarta.persistence.Column;
    import jakarta.persistence.Entity;
    import jakarta.persistence.GeneratedValue;
    import jakarta.persistence.GenerationType;
    import jakarta.persistence.Id;
    import jakarta.persistence.Table;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Entity
    @Table(name = "xuatchieu")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class ShowTime {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increment
        @Column(name = "IDXUATCHIEU")
        private Long idXuatChieu; // dùng Long hoặc Integer

        @Column(name = "KHUNGGIO", nullable = false)
        private LocalTime khungGio;

        @Column(name = "STATUS", nullable = false)
        private Integer status = 1;
    }
