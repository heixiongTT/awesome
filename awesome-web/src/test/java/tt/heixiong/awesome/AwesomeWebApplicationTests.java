package tt.heixiong.awesome;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AwesomeWebApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void requirementCrudFlowWorks() throws Exception {
        mockMvc.perform(post("/requirements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Trace-Id", "trace-create")
                        .content("{\"title\":\"补全JPA脚手架\",\"description\":\"接入Spring Data JPA\",\"priority\":\"HIGH\",\"creator\":\"codex\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.traceId").value("trace-create"))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.status").value("TODO"));

        mockMvc.perform(get("/requirements").param("creator", "codex"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].title").value("补全JPA脚手架"));

        mockMvc.perform(put("/requirements/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));

        mockMvc.perform(put("/requirements/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"status\":\"DONE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.status").value("DONE"));
    }

    @Test
    public void statusUpdateRejectsSkippingWorkflowSteps() throws Exception {
        mockMvc.perform(post("/requirements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"补全JPA脚手架\",\"description\":\"接入Spring Data JPA\",\"priority\":\"HIGH\",\"creator\":\"codex\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/requirements/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"status\":\"DONE\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INVALID_STATUS_TRANSITION"))
                .andExpect(jsonPath("$.message").value(
                        "Status must follow TODO -> IN_PROGRESS -> DONE, cannot change from TODO to DONE"));
    }

    @Test
    public void validationErrorsUseUnifiedEnvelope() throws Exception {
        mockMvc.perform(post("/requirements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"creator\":\"codex\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data.errors[0]").value("title: must not be blank"))
                .andExpect(jsonPath("$.data.path").value("/requirements"));
    }

    @Test
    public void missingResourceUsesUnifiedEnvelope() throws Exception {
        mockMvc.perform(get("/requirements/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Requirement not found"))
                .andExpect(jsonPath("$.data.path").value("/requirements/999"));
    }

    @Test
    public void deleteMissingResourceUsesUnifiedEnvelope() throws Exception {
        mockMvc.perform(delete("/requirements/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }
}
