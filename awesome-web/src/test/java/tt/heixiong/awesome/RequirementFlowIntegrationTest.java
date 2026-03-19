package tt.heixiong.awesome;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.annotation.DirtiesContext;

import static org.hamcrest.Matchers.hasItem;
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
@TestPropertySource(locations = "classpath:integration-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RequirementFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void requirementCrudFlowWorks() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/requirements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Trace-Id", "trace-create")
                        .content("{\"title\":\"requirement title\",\"description\":\"Spring Data JPA\",\"priority\":\"HIGH\",\"creator\":\"codex\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.traceId").value("trace-create"))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.status").value("TODO"))
                .andReturn();

        Integer requirementId = JsonPath.read(createResult.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(get("/requirements").param("creator", "codex"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data[*].title", hasItem("requirement title")));

        mockMvc.perform(put("/requirements/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":" + requirementId + ",\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));

        mockMvc.perform(put("/requirements/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":" + requirementId + ",\"status\":\"DONE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.status").value("DONE"));
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
