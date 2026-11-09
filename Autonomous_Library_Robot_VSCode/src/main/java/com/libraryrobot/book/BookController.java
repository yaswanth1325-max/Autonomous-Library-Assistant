package com.libraryrobot.book;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {
    private final BookRepository repository;

    public BookController(BookRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Book> getAll(@RequestParam(required = false) String search) {
        if (search == null || search.isBlank()) return repository.findAll();
        return repository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(search, search);
    }

    @GetMapping("/{id}")
    public Book getOne(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@Valid @RequestBody Book book) {
        book.setStatus(book.getStatus() == null ? "AVAILABLE" : book.getStatus());
        return repository.save(book);
    }

    @PutMapping("/{id}")
    public Book update(@PathVariable Long id, @Valid @RequestBody Book input) {
        Book book = getOne(id);
        book.setTitle(input.getTitle());
        book.setAuthor(input.getAuthor());
        book.setIsbn(input.getIsbn());
        book.setShelfCode(input.getShelfCode());
        book.setStatus(input.getStatus());
        return repository.save(book);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.delete(getOne(id));
    }
}
