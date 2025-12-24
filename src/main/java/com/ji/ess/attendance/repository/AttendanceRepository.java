package com.ji.ess.attendance.repository;

import com.ji.ess.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    // 유저아이디랑 근무날짜 조회
    Optional<Attendance> findByUserIdAndWorkDate(Long userId, LocalDate workDate);
    //개인 근태 조회용 리스트
    @EntityGraph(attributePaths = {"user", "company"})
    List<Attendance> findByUserId(Long userId);

    boolean existsByUserIdAndWorkDate(Long userId, LocalDate workDate);

    // (간소화 버전) 기간 조회 메서드는 사용하지 않음

}
