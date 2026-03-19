package tt.heixiong.awesome.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import tt.heixiong.awesome.domain.Requirement;
import tt.heixiong.awesome.service.RequirementService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = RequirementCtrl.class, properties = {"server.servlet.context-path=", "eureka.client.enabled=false", "spring.cloud.discovery.enabled=false"})
public class RequirementCtrlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequirementService requirementService;

    @Test
    public void createRequirementRejectsBlankTitle() throws Exception {
        mockMvc.perform(post("/requirements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"description\":\"缺少标题\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors[0]").value("title: must not be blank"));
    }

    @Test
    public void getRequirementReturnsNotFoundWhenMissing() throws Exception {
        when(requirementService.getRequirement(42L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/requirements/42"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateRequirementStatusReturnsNotFoundWhenMissing() throws Exception {
        when(requirementService.updateRequirementStatus(7L, "DONE")).thenReturn(Optional.empty());

        mockMvc.perform(put("/requirements/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":7,\"status\":\"DONE\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteRequirementReturnsNotFoundWhenMissing() throws Exception {
        doThrow(new org.springframework.dao.EmptyResultDataAccessException(1)).when(requirementService).deleteRequirement(anyLong());

        mockMvc.perform(delete("/requirements/11"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Requirement not found"));
    }

    @Test
    public void getRequirementReturnsPayloadWhenPresent() throws Exception {
        Requirement requirement = new Requirement();
        requirement.setId(5L);
        requirement.setTitle("关键回归");
        requirement.setStatus("TODO");
        when(requirementService.getRequirement(eq(5L))).thenReturn(Optional.of(requirement));

        mockMvc.perform(get("/requirements/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.title").value("关键回归"));
    }
}
