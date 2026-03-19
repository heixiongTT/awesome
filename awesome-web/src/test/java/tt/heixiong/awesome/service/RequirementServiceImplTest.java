package tt.heixiong.awesome.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import tt.heixiong.awesome.domain.Requirement;
import tt.heixiong.awesome.repository.RequirementRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequirementServiceImplTest {

    @Mock
    private RequirementRepository requirementRepository;

    @InjectMocks
    private RequirementServiceImpl requirementService;

    @Test
    public void createRequirementDefaultsStatusWhenMissing() {
        Requirement requirement = new Requirement();
        requirement.setTitle("补齐测试");

        Requirement saved = new Requirement();
        saved.setId(1L);
        saved.setTitle("补齐测试");
        saved.setStatus("TODO");

        when(requirementRepository.save(any(Requirement.class))).thenReturn(saved);

        Requirement result = requirementService.createRequirement(requirement);

        ArgumentCaptor<Requirement> captor = ArgumentCaptor.forClass(Requirement.class);
        verify(requirementRepository).save(captor.capture());
        assertEquals("TODO", captor.getValue().getStatus());
        assertSame(saved, result);
    }

    @Test
    public void listRequirementsUsesCombinedFilterWhenStatusAndCreatorProvided() {
        Requirement requirement = new Requirement();
        requirement.setId(2L);
        when(requirementRepository.findByStatusAndCreator("DONE", "codex"))
                .thenReturn(Collections.singletonList(requirement));

        assertEquals(1, requirementService.listRequirements("DONE", "codex").size());
        verify(requirementRepository).findByStatusAndCreator("DONE", "codex");
        verify(requirementRepository, never()).findByStatus(any(String.class));
        verify(requirementRepository, never()).findByCreator(any(String.class));
    }

    @Test
    public void listRequirementsFallsBackToNewestFirstWhenNoFilterProvided() {
        when(requirementRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(Arrays.asList(new Requirement(), new Requirement()));

        assertEquals(2, requirementService.listRequirements(null, " ").size());
        verify(requirementRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    public void updateRequirementStatusReturnsEmptyWhenRequirementMissing() {
        when(requirementRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Requirement> result = requirementService.updateRequirementStatus(99L, "DONE");

        assertFalse(result.isPresent());
        verify(requirementRepository).findById(99L);
        verify(requirementRepository, never()).save(any(Requirement.class));
    }

    @Test
    public void deleteRequirementDelegatesToRepository() {
        requirementService.deleteRequirement(3L);

        verify(requirementRepository).deleteById(eq(3L));
    }
}
