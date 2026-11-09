package com.libraryrobot.book;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false)
    private String author;

    @Column(unique = true)
    private String isbn;

    private String shelfCode;

    @Column(nullable = false)
    private String status = "AVAILABLE";

    public Book() {}

    public Book(String title, String author, String isbn, String shelfCode, String status) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.shelfCode = shelfCode;
        this.status = status == null || status.isBlank() ? "AVAILABLE" : status;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getShelfCode() { return shelfCode; }
    public void setShelfCode(String shelfCode) { this.shelfCode = shelfCode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
