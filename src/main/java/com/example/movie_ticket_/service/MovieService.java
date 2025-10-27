package com.example.movie_ticket_.service;

import com.example.movie_ticket_.entity.Movie;
import com.example.movie_ticket_.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final CloudinaryService cloudinaryService;

    // Lấy tất cả phim
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    // Lấy phim theo status (nếu null thì lấy tất cả)
    public List<Movie> getMoviesByStatus(Integer status) {
        if (status == null) {
            return movieRepository.findAll();
        }
        return movieRepository.findByStatus(status);
    }

    public List<Movie> getActiveMovies() {
        return movieRepository.findByStatus(1);
    }

    // Lấy phim theo ID
    public Movie getMovieById(int id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie with id " + id + " not found"));
    }

    // Thêm phim mới
    public Movie addMovie(Movie movie, MultipartFile posterFile) {
        if (posterFile != null && !posterFile.isEmpty()) {
            movie.setPoster(uploadPoster(posterFile));
        }
        return movieRepository.save(movie);
    }

    // Thêm phim từ API (không có file, chỉ URL)
    public Movie addMovieFromApi(Movie movie) {
        return movieRepository.save(movie);
    }

    // Cập nhật phim (admin upload file)
    public Movie updateMovie(int id, Movie newMovie, MultipartFile posterFile) {
        Movie movie = getMovieById(id);

        movie.setTenPhim(newMovie.getTenPhim());
        movie.setTheloai(newMovie.getTheloai());
        movie.setThoiLuong(newMovie.getThoiLuong());
        movie.setDaoDien(newMovie.getDaoDien());
        movie.setNamPhatHanh(newMovie.getNamPhatHanh());
        movie.setDescrip(newMovie.getDescrip());
        movie.setDienVien(newMovie.getDienVien());
        movie.setStatus(newMovie.getStatus());

        if (posterFile != null && !posterFile.isEmpty()) {
            movie.setPoster(uploadPoster(posterFile));
        }

        return movieRepository.save(movie);
    }

    // Cập nhật phim từ API (chỉ dữ liệu JSON, không file)
    public Movie updateMovieFromApi(int id, Movie newMovie) {
        Movie movie = getMovieById(id);

        movie.setTenPhim(newMovie.getTenPhim());
        movie.setTheloai(newMovie.getTheloai());
        movie.setThoiLuong(newMovie.getThoiLuong());
        movie.setDaoDien(newMovie.getDaoDien());
        movie.setNamPhatHanh(newMovie.getNamPhatHanh());
        movie.setDescrip(newMovie.getDescrip());
        movie.setDienVien(newMovie.getDienVien());
        movie.setStatus(newMovie.getStatus());

        if (newMovie.getPoster() != null) {
            movie.setPoster(newMovie.getPoster()); // API gửi URL poster
        }

        return movieRepository.save(movie);
    }

    // Đổi trạng thái phim (0 = khóa, 1 = hoạt động)
    public Movie changeMovieStatus(int id, int status) {
        Movie movie = getMovieById(id);
        movie.setStatus(status);
        return movieRepository.save(movie);
    }

    // Toggle status (chuyển đổi nhanh)
    public int toggleStatus(int id) {
        Movie movie = getMovieById(id);
        int newStatus = (movie.getStatus() == 1) ? 0 : 1;
        movie.setStatus(newStatus);
        movieRepository.save(movie);
        return newStatus;
    }

    // Upload ảnh lên Cloudinary
    private String uploadPoster(MultipartFile posterFile) {
        Map<?, ?> uploadResult = cloudinaryService.uploadToFolder(posterFile,"movies");
        System.out.println("Cloudinary upload result: " + uploadResult);
        return uploadResult.get("secure_url").toString();
    }

    //Lấy api

    // Lấy danh sách phim đang chiếu
    public List<Movie> getPlayingMovies() {
        return movieRepository.findPlayingMovies(LocalDate.now());
    }

    // Lấy danh sách phim sắp chiếu
    public List<Movie> getUpcomingMovies() {
        return movieRepository.findUpcomingMovies(LocalDate.now());
    }
}
