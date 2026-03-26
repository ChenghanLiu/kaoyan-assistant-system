package com.kaoyan.assistant.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturnBusinessExceptionStatusAndApiResponse() throws Exception {
        mockMvc.perform(get("/test/business"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(4040))
                .andExpect(jsonPath("$.message").value("material not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void shouldReturnValidationMessage() throws Exception {
        mockMvc.perform(post("/test/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TestRequest(""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4000))
                .andExpect(jsonPath("$.message").value("name is required"));
    }

    @Test
    void shouldReturnClearMessageForUploadSizeExceeded() throws Exception {
        mockMvc.perform(post("/test/upload-limit"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4000))
                .andExpect(jsonPath("$.message").value("file size exceeds limit"));
    }

    @Test
    void shouldHideUnhandledExceptionDetails() throws Exception {
        mockMvc.perform(get("/test/unhandled"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(5000))
                .andExpect(jsonPath("$.message").value("internal server error"));
    }

    record TestRequest(@NotBlank(message = "name is required") String name) {
    }

    @RestController
    @Validated
    @RequestMapping("/test")
    static class TestController {

        @GetMapping("/business")
        void business() {
            throw BusinessException.notFound("material not found");
        }

        @PostMapping("/validate")
        void validate(@Valid @RequestBody TestRequest request) {
        }

        @PostMapping("/upload-limit")
        void uploadLimit() {
            throw new MaxUploadSizeExceededException(1024);
        }

        @GetMapping("/unhandled")
        void unhandled() {
            throw new IllegalStateException("boom");
        }
    }
}
