package tt.heixiong.awesome.service;

import org.springframework.stereotype.Service;
import tt.heixiong.awesome.domain.Requirement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RequirementServiceImpl implements RequirementService {

    private final AtomicLong idGenerator = new AtomicLong(1);

    private final Map<Long, Requirement> requirementStore = new ConcurrentHashMap<Long, Requirement>();

    @Override
    public Requirement createRequirement(Requirement requirement) {
        Long currentId = idGenerator.getAndIncrement();
        Long currentTime = System.currentTimeMillis();
        requirement.setId(currentId);
        requirement.setStatus(requirement.getStatus() == null ? "TODO" : requirement.getStatus());
        requirement.setCreatedAt(currentTime);
        requirement.setUpdatedAt(currentTime);
        requirementStore.put(currentId, requirement);
        return requirement;
    }

    @Override
    public List<Requirement> listRequirements() {
        return new ArrayList<Requirement>(requirementStore.values());
    }

    @Override
    public Requirement getRequirement(Long id) {
        return requirementStore.get(id);
    }

    @Override
    public Requirement updateRequirementStatus(Long id, String status) {
        Requirement requirement = requirementStore.get(id);
        if (requirement == null) {
            return null;
        }
        requirement.setStatus(status);
        requirement.setUpdatedAt(System.currentTimeMillis());
        return requirement;
    }

    @Override
    public void deleteRequirement(Long id) {
        requirementStore.remove(id);
    }
}
