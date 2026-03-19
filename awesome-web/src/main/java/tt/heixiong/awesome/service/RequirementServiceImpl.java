package tt.heixiong.awesome.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tt.heixiong.awesome.domain.Requirement;
import tt.heixiong.awesome.exception.BusinessException;
import tt.heixiong.awesome.exception.ResourceNotFoundException;
import tt.heixiong.awesome.repository.RequirementRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class RequirementServiceImpl implements RequirementService {

    private static final String DEFAULT_STATUS = "TODO";
    private static final Map<String, String> NEXT_STATUS_MAP = buildNextStatusMap();

    private final RequirementRepository requirementRepository;

    public RequirementServiceImpl(RequirementRepository requirementRepository) {
        this.requirementRepository = requirementRepository;
    }

    @Override
    public Requirement createRequirement(Requirement requirement) {
        if (!StringUtils.hasText(requirement.getStatus())) {
            requirement.setStatus(DEFAULT_STATUS);
        } else {
            requirement.setStatus(normalizeStatus(requirement.getStatus()));
        }
        validateInitialStatus(requirement.getStatus());
        return requirementRepository.save(requirement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Requirement> listRequirements(String status, String creator) {
        if (StringUtils.hasText(status) && StringUtils.hasText(creator)) {
            return requirementRepository.findByStatusAndCreator(normalizeStatus(status), creator);
        }
        if (StringUtils.hasText(status)) {
            return requirementRepository.findByStatus(normalizeStatus(status));
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
        final String normalizedStatus = normalizeStatus(status);
        return requirementRepository.findById(id)
                .map(requirement -> {
                    validateStatusTransition(requirement.getStatus(), normalizedStatus);
                    requirement.setStatus(normalizedStatus);
                    return requirementRepository.save(requirement);
                });
    }

    @Override
    public void deleteRequirement(Long id) {
        try {
            requirementRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new ResourceNotFoundException("Requirement not found");
        }
    }

    private void validateInitialStatus(String status) {
        if (!NEXT_STATUS_MAP.containsKey(status)) {
            throw new BusinessException(
                    "INVALID_STATUS",
                    "Unsupported initial status: " + status,
                    HttpStatus.BAD_REQUEST);
        }
    }

    private void validateStatusTransition(String currentStatus, String targetStatus) {
        String normalizedCurrent = normalizeStatus(currentStatus);
        String expectedNextStatus = NEXT_STATUS_MAP.get(normalizedCurrent);
        if (expectedNextStatus == null) {
            throw new BusinessException(
                    "INVALID_STATUS",
                    "Unsupported current status: " + normalizedCurrent,
                    HttpStatus.BAD_REQUEST);
        }
        if (normalizedCurrent.equals(targetStatus)) {
            return;
        }
        if (!targetStatus.equals(expectedNextStatus)) {
            throw new BusinessException(
                    "INVALID_STATUS_TRANSITION",
                    String.format("Status must follow TODO -> IN_PROGRESS -> DONE, cannot change from %s to %s",
                            normalizedCurrent,
                            targetStatus),
                    HttpStatus.CONFLICT);
        }
    }

    private String normalizeStatus(String status) {
        if (!StringUtils.hasText(status)) {
            throw new BusinessException("INVALID_STATUS", "Status must not be blank", HttpStatus.BAD_REQUEST);
        }
        return status.trim().toUpperCase();
    }

    private static Map<String, String> buildNextStatusMap() {
        List<String> statuses = Arrays.asList("TODO", "IN_PROGRESS", "DONE");
        Map<String, String> nextStatusMap = new HashMap<String, String>();
        for (int index = 0; index < statuses.size(); index++) {
            String nextStatus = index + 1 < statuses.size() ? statuses.get(index + 1) : statuses.get(index);
            nextStatusMap.put(statuses.get(index), nextStatus);
        }
        return nextStatusMap;
    }
}
