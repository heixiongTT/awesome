package tt.heixiong.awesome.service;

import tt.heixiong.awesome.domain.Requirement;

import java.util.List;

public interface RequirementService {

    Requirement createRequirement(Requirement requirement);

    List<Requirement> listRequirements();

    Requirement getRequirement(Long id);

    Requirement updateRequirementStatus(Long id, String status);

    void deleteRequirement(Long id);
}
