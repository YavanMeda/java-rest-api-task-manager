package com.yflyi.task_manager_api;

import com.yflyi.task_manager_api.model.Task;
import com.yflyi.task_manager_api.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskApiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void createTaskReturnsCreatedTask() throws Exception {
        String requestBody = """
                {
                  "title": "Write integration tests",
                  "description": "Verify create flow",
                  "status": "TODO"
                }
                """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Write integration tests"))
                .andExpect(jsonPath("$.description").value("Verify create flow"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void getTaskByIdReturnsExistingTask() throws Exception {
        Task task = new Task();
        task.setTitle("Saved task");
        task.setDescription("Stored in database");
        task.setStatus(TaskStatus.IN_PROGRESS);
        Task savedTask = taskRepository.save(task);

        mockMvc.perform(get("/api/tasks/{id}", savedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedTask.getId()))
                .andExpect(jsonPath("$.title").value("Saved task"))
                .andExpect(jsonPath("$.description").value("Stored in database"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void getTaskByIdReturnsNotFoundWhenMissing() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Task with id 999999 was not found"))
                .andExpect(jsonPath("$.path").value("/api/tasks/999999"));
    }

    @Test
    void createTaskReturnsBadRequestWhenTitleIsBlank() throws Exception {
        String requestBody = """
                {
                  "title": "   ",
                  "description": "Invalid request",
                  "status": "TODO"
                }
                """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/api/tasks"))
                .andExpect(jsonPath("$.details[0]").value("title: Title is required"));
    }
}
