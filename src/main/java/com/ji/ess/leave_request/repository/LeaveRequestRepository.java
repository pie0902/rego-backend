package com.ji.ess.leave_request.repository;

import com.ji.ess.leave_request.entity.LeaveRequest;
import com.ji.ess.leave_request.entity.LeaveStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    @Query("""
            select lr from LeaveRequest lr
            join fetch lr.user
            join fetch lr.company
            where lr.company.id = :companyId
              and lr.status = :status
            """)
    List<LeaveRequest> findByCompanyIdAndStatus(@Param("companyId") Long companyId, @Param("status") LeaveStatus status);

    @EntityGraph(attributePaths = {"user", "company"})
    List<LeaveRequest> findByUserIdOrderByRequestedAtDesc(Long userId);

}
