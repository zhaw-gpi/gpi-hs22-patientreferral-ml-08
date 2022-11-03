package ch.zhaw.gpi.referralprocessapplication.kis_mock;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalCaseRepository extends JpaRepository<MedicalCase, Long> {
    
}
