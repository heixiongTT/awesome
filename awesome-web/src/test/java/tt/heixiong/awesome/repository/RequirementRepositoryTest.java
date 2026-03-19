package tt.heixiong.awesome.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import tt.heixiong.awesome.domain.Requirement;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:integration-test.properties")
public class RequirementRepositoryTest {

    @Autowired
    private RequirementRepository requirementRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void findByStatusAndCreatorReturnsOnlyMatchingRequirements() {
        persistRequirement("需求A", "TODO", "alice");
        persistRequirement("需求B", "DONE", "alice");
        persistRequirement("需求C", "DONE", "bob");

        List<Requirement> requirements = requirementRepository.findByStatusAndCreator("DONE", "alice");

        assertEquals(1, requirements.size());
        assertEquals("需求B", requirements.get(0).getTitle());
    }

    @Test
    public void findAllByOrderByCreatedAtDescReturnsNewestFirst() {
        Requirement first = persistRequirement("最早", "TODO", "alice");
        sleepBriefly();
        Requirement second = persistRequirement("最新", "TODO", "alice");

        List<Requirement> requirements = requirementRepository.findAllByOrderByCreatedAtDesc();

        assertEquals(2, requirements.size());
        assertEquals(second.getId(), requirements.get(0).getId());
        assertEquals(first.getId(), requirements.get(1).getId());
    }

    private void sleepBriefly() {
        try {
            Thread.sleep(5L);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        }
    }

    private Requirement persistRequirement(String title, String status, String creator) {
        Requirement requirement = new Requirement();
        requirement.setTitle(title);
        requirement.setDescription(title + " 描述");
        requirement.setPriority("HIGH");
        requirement.setStatus(status);
        requirement.setCreator(creator);
        entityManager.persist(requirement);
        entityManager.flush();
        entityManager.clear();
        return requirement;
    }
}
