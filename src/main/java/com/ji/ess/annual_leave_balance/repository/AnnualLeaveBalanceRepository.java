package com.ji.ess.annual_leave_balance.repository;

import com.ji.ess.annual_leave_balance.entity.AnnualLeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnnualLeaveBalanceRepository extends JpaRepository<AnnualLeaveBalance, Long> {
    Optional<AnnualLeaveBalance> findByUserIdAndYear(Long userId, int year);
    List<AnnualLeaveBalance> findByUserId(Long userId);
}
