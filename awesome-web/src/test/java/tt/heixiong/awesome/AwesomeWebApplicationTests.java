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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.status").value("TODO"));

        mockMvc.perform(get("/requirements").param("creator", "codex"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].title").value("补全JPA脚手架"));

        mockMvc.perform(put("/requirements/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"status\":\"DONE\"}"))
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

    @Test
    public void actuatorEndpointsExposePrometheusAndHealth() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));

        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk());
    }

    @Test
    public void getRequirementReturnsMappedAuditFields() throws Exception {
        mockMvc.perform(post("/requirements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"验证MapStruct映射\",\"description\":\"确认实体字段被转换为DTO\",\"priority\":\"MEDIUM\",\"creator\":\"codex\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());

        mockMvc.perform(get("/requirements/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("验证MapStruct映射"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
    }


    @Test
    public void swaggerDocsEndpointExposesRequirementApi() throws Exception {
        mockMvc.perform(get("/v2/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/requirements'].post.summary").value("创建需求"))
                .andExpect(jsonPath("$.paths['/requirements/status'].put.summary").value("更新需求状态"));
    }

}
