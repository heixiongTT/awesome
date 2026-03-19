package tt.heixiong.awesome.service;

import tt.heixiong.awesome.domain.Requirement;

import java.util.List;
import java.util.Optional;

public interface RequirementService {

    Requirement createRequirement(Requirement requirement);

    List<Requirement> listRequirements(String status, String creator);

    Optional<Requirement> getRequirement(Long id);

    Optional<Requirement> updateRequirementStatus(Long id, String status);

    void deleteRequirement(Long id);
}
