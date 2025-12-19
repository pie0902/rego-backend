package com.ji.ess.user.repository;


import com.ji.ess.user.entity.User;
import com.ji.ess.user.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);
    List<User> findByCompanyIdAndActive(Long companyId, UserStatus active);
    // 입사일 기준 범위 조회(입사 1년 미만 사용자 선별용)
    List<User> findByHireDateBetween(LocalDate start, LocalDate end);


}
