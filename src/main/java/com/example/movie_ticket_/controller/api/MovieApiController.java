package com.example.movie_ticket_.controller.api;

import com.example.movie_ticket_.entity.Movie;
import com.example.movie_ticket_.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieApiController {

    private final MovieService movieService;

    // Lấy danh sách phim đang hoạt động (status = 1)
    @GetMapping
    public List<Movie> getActiveMovies() {
        return movieService.getMoviesByStatus(1);
    }
    @GetMapping("/playing")
    public List<Movie> getPlayingMovies() {
        return movieService.getPlayingMovies();
    }

    // Lấy danh sách phim sắp chiếu
    @GetMapping("/upcoming")
    public List<Movie> getUpcomingMovies() {
        return movieService.getUpcomingMovies();
    }

    // Lấy chi tiết phim theo ID (chỉ trả về nếu phim đang hoạt động)
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable int id) {
        Movie movie = movieService.getMovieById(id);
        if (movie.getStatus() == 1) {
            return ResponseEntity.ok(movie);
        }
        return ResponseEntity.notFound().build();
    }

//    // Thêm phim từ API (dùng khi bạn muốn cho mobile quản lý phim, nếu không cần
//    // thì bỏ đi)
//    @PostMapping
//    public ResponseEntity<?> addMovie(@RequestBody Movie movie) {
//        try {
//            return ResponseEntity.ok(movieService.addMovieFromApi(movie));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }
//
//    // Cập nhật phim từ API
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateMovie(@PathVariable int id, @RequestBody Movie movie) {
//        try {
//            return ResponseEntity.ok(movieService.updateMovieFromApi(id, movie));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }
//
//    // Toggle status (thường chỉ để admin dùng, API ít dùng)
//    @PatchMapping("/{id}/status")
//    public ResponseEntity<?> toggleStatus(@PathVariable int id) {
//        int newStatus = movieService.toggleStatus(id);
//        return ResponseEntity.ok("Status updated to " + newStatus);
//    }




}
