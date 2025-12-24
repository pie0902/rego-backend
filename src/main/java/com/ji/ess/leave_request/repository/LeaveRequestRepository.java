package com.ji.ess.leave_request.repository;

import com.ji.ess.leave_request.entity.LeaveRequest;
import com.ji.ess.leave_request.entity.LeaveStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    @EntityGraph(attributePaths = {"user", "company"})
    List<LeaveRequest> findByCompanyIdAndStatus(Long companyId, LeaveStatus status);

    @EntityGraph(attributePaths = {"user", "company"})
    List<LeaveRequest> findByUserIdOrderByRequestedAtDesc(Long userId);

}
