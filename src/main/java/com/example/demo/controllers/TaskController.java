package com.example.demo.controllers;

import com.example.demo.entities.TaskEntity;
import com.example.demo.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.Task;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/todos")
@CrossOrigin("http://localhost:5173")
public class TaskController {

    @Autowired  // Inject the repository
    private TaskRepository taskRepository;


    @GetMapping
    public ResponseEntity<?> getAllTasks() {
        return ResponseEntity.ok().body(taskRepository.findAll());  // Use findAll() instead of getAll()
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        var user = taskRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity
                    .status(404)
                    .body(Map.of("message", "User with id " + id + " not found"));
        }

        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        var task = taskRepository.findById(id).orElse(null);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        if (updates.containsKey("title")) {
            task.setTitle((String) updates.get("title"));
        }

        if (updates.containsKey("completed")) {
            task.setCompleted((Boolean) updates.get("completed"));
        }

        // Don't update id or createdAt
        TaskEntity updatedTask = taskRepository.save(task);
        return ResponseEntity.ok(updatedTask);
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskEntity newTask) {
        try {

            TaskEntity savedTask = taskRepository.save(newTask);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating task: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            // Check if the resource exists
            if (taskRepository.existsById(id)) {
                taskRepository.deleteById(id);
                return ResponseEntity.ok().build(); // 200 OK with no body
            } else {
                return ResponseEntity.notFound().build(); // 404 Not Found
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting task: " + e.getMessage());
        }
    }

}
