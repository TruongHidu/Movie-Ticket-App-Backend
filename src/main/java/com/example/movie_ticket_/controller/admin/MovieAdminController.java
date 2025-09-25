package com.example.movie_ticket_.controller.admin;

import com.example.movie_ticket_.entity.Movie;
import com.example.movie_ticket_.repository.MovieRepository;
import com.example.movie_ticket_.service.MovieService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; // Use @Controller for MVC views
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller // Use @Controller for MVC controllers returning view names
@RequestMapping("/admin/movies")
@RequiredArgsConstructor
public class MovieAdminController {

    // Lombok's @RequiredArgsConstructor will generate a constructor
    // that injects these dependencies.
    private final MovieService movieService;
    private final MovieRepository movieRepository;

    @GetMapping
    public String listMovies(@RequestParam(value = "status", required = false) Integer status, Model model) {
        List<Movie> movies;
        if (status == null) {
            movies = movieRepository.findAll(); // Use repository directly here if needed for admin listing
        } else {
            movies = movieRepository.findByStatus(status);
        }
        model.addAttribute("movies", movies);
        return "movie/movies"; // Assumes you have a view named movies.html in
                               // src/main/resources/templates/movie/
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable int id) {
        Optional<Movie> movie = movieRepository.findById(id);
        if (movie.isPresent()) {
            return ResponseEntity.ok(movie.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("movie", new Movie()); // Add a new Movie object to the model
        return "movie/add-movie"; // Assumes you have add-movie.html
    }

    @PostMapping("/add")
    public String addMovie(@ModelAttribute Movie movie,
            @RequestParam("posterFile") MultipartFile posterFile) {
        try {
            // Use the service to add the movie, which handles file uploads
            movieService.addMovie(movie, posterFile);
        } catch (Exception e) {
            // Log the exception properly in a real application
            e.printStackTrace();
            // Consider returning an error view or redirecting with an error message
        }
        // Redirect to the movie list page after adding
        return "redirect:/admin/movies";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        // Get the movie using the service to ensure proper handling (e.g., exceptions)
        Movie movie = movieService.getMovieById(id);
        model.addAttribute("movie", movie);
        return "movie/edit-movie"; // Assumes you have edit-movie.html
    }

    @PostMapping("/edit/{id}")
    public String updateMovie(@PathVariable int id,
            @ModelAttribute Movie movie,
            @RequestParam("posterFile") MultipartFile posterFile) {
        try {
            // Use the service to update the movie, passing the poster file
            movieService.updateMovie(id, movie, posterFile);
        } catch (Exception e) {
            // Log the exception properly
            e.printStackTrace();
            // Consider returning an error view or redirecting with an error message
        }
        // Redirect to the movie list page after updating
        return "redirect:/admin/movies";
    }

    @PostMapping("/status/{id}")
    public String changeStatus(@PathVariable int id) {
        // Fetch the movie, toggle its status, and save
        Movie movie = movieService.getMovieById(id); // Use service to get the movie
        int newStatus = movie.getStatus() == 1 ? 0 : 1; // Toggle logic
        movieService.changeMovieStatus(id, newStatus); // Use service to change status
        return "redirect:/admin/movies"; // Redirect back to the list
    }
}