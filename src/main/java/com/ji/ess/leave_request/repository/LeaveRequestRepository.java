package com.ji.ess.leave_request.repository;

import com.ji.ess.leave_request.entity.LeaveRequest;
import com.ji.ess.leave_request.entity.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByCompanyIdAndStatus(Long companyId, LeaveStatus status);
    List<LeaveRequest> findByUserIdOrderByRequestedAtDesc(Long userId);

}
