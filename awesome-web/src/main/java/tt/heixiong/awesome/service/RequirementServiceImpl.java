package tt.heixiong.awesome.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tt.heixiong.awesome.domain.Requirement;
import tt.heixiong.awesome.repository.RequirementRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RequirementServiceImpl implements RequirementService {

    private static final String DEFAULT_STATUS = "TODO";

    private final RequirementRepository requirementRepository;

    public RequirementServiceImpl(RequirementRepository requirementRepository) {
        this.requirementRepository = requirementRepository;
    }

    @Override
    public Requirement createRequirement(Requirement requirement) {
        if (!StringUtils.hasText(requirement.getStatus())) {
            requirement.setStatus(DEFAULT_STATUS);
        }
        return requirementRepository.save(requirement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Requirement> listRequirements(String status, String creator) {
        if (StringUtils.hasText(status) && StringUtils.hasText(creator)) {
            return requirementRepository.findByStatusAndCreator(status, creator);
        }
        if (StringUtils.hasText(status)) {
            return requirementRepository.findByStatus(status);
        }
        if (StringUtils.hasText(creator)) {
            return requirementRepository.findByCreator(creator);
        }
        return requirementRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Requirement> getRequirement(Long id) {
        return requirementRepository.findById(id);
    }

    @Override
    public Optional<Requirement> updateRequirementStatus(Long id, String status) {
        return requirementRepository.findById(id)
                .map(requirement -> {
                    requirement.setStatus(status);
                    return requirementRepository.save(requirement);
                });
    }

    @Override
    public void deleteRequirement(Long id) {
        requirementRepository.deleteById(id);
    }
}
