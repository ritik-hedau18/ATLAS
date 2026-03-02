package com.atlas.userservice.controller;

import com.atlas.userservice.dto.ProfileDto;
import com.atlas.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testGetProfileSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        ProfileDto mockProfile = ProfileDto.builder()
                .id(userId)
                .fullName("John Doe")
                .email("john.doe@gmail.com")
                .headline("Software Architect")
                .profileCompletenessScore(80)
                .build();

        Mockito.when(userService.getProfile(userId, userId)).thenReturn(mockProfile);

        mockMvc.perform(get("/api/users/" + userId + "/profile")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@gmail.com"))
                .andExpect(jsonPath("$.profileCompletenessScore").value(80));
    }
}
