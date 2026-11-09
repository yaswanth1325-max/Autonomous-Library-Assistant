package com.libraryrobot.task;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class RobotTaskController {
    private final RobotTaskRepository repository;

    public RobotTaskController(RobotTaskRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<RobotTask> getAll() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RobotTask create(@Valid @RequestBody RobotTask task) {
        task.setStatus("PENDING");
        return repository.save(task);
    }

    @PatchMapping("/{id}/status")
    public RobotTask updateStatus(@PathVariable Long id, @RequestBody StatusUpdate update) {
        RobotTask task = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        if (update.status() == null || !List.of("PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED")
                .contains(update.status().toUpperCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task status");
        }
        task.setStatus(update.status().toUpperCase());
        return repository.save(task);
    }

    public record StatusUpdate(String status) {}
}
