package ch.zhaw.gpi.kis;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalCaseRepository extends JpaRepository<MedicalCase, Long> {
    
}
