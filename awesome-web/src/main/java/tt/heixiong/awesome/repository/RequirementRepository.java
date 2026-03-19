package tt.heixiong.awesome.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tt.heixiong.awesome.domain.Requirement;

import java.util.List;

public interface RequirementRepository extends JpaRepository<Requirement, Long> {

    List<Requirement> findAllByOrderByCreatedAtDesc();

    List<Requirement> findByStatus(String status);

    List<Requirement> findByCreator(String creator);

    List<Requirement> findByStatusAndCreator(String status, String creator);
}
