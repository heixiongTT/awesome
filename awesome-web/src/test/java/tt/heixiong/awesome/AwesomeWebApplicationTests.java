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
                        .content("{\"title\":\"补全JPA脚手架\",\"description\":\"接入Spring Data JPA\",\"priority\":\"HIGH\",\"creator\":\"codex\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.status").value("TODO"));

        mockMvc.perform(get("/requirements").param("creator", "codex"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("补全JPA脚手架"));

        mockMvc.perform(put("/requirements/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"status\":\"DONE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    public void swaggerDocsEndpointExposesRequirementApi() throws Exception {
        mockMvc.perform(get("/v2/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/requirements'].post.summary").value("创建需求"))
                .andExpect(jsonPath("$.paths['/requirements/status'].put.summary").value("更新需求状态"));
    }

}
