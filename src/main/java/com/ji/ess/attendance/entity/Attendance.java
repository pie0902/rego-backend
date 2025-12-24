package com.ji.ess.attendance.entity;

import com.ji.ess.company.entity.Company;
import com.ji.ess.companyRule.entity.CompanyRule;
import com.ji.ess.companyRule.entity.WorkType;
import com.ji.ess.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

// 근태 엔티티
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate workDate;      // 근무 날짜
    private LocalTime checkIn;       // 출근 시간
    private LocalTime checkOut;      // 퇴근 시간
    private int workHours;           // 근무 시간(시간 단위)
    private int workMinutes;         // 근무 시간(분 단위)
    private Integer shortfallMinutes; // 유연근무 부족분(분)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;
    // 정상 / 지각 / 조퇴 / 결근
    private String note;             // 비고

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    public static Attendance seed(User user,
                                  Company company,
                                  CompanyRule rule,
                                  LocalDate workDate,
                                  LocalTime checkInTime,
                                  LocalTime checkOutTime) {
        Objects.requireNonNull(user, "user");
        Objects.requireNonNull(company, "company");
        Objects.requireNonNull(workDate, "workDate");
        Objects.requireNonNull(checkInTime, "checkInTime");
        Objects.requireNonNull(checkOutTime, "checkOutTime");

        if (!checkOutTime.isAfter(checkInTime)) {
            throw new IllegalArgumentException("checkOutTime must be after checkInTime");
        }

        Attendance attendance = new Attendance();
        attendance.user = user;
        attendance.company = company;
        attendance.workDate = workDate;
        attendance.checkIn = checkInTime;
        attendance.checkOut = checkOutTime;
        attendance.status = determineStatusOnCheckIn(checkInTime, rule);
        attendance.note = "seeded";

        attendance.workHours = attendance.calculateWorkHours();
        attendance.shortfallMinutes = null;

        if (rule != null && rule.getWorkType() != null) {
            if (rule.getWorkType() == WorkType.FIXED) {
                attendance.applyFixedRuleOnSeed(rule);
            } else if (rule.getWorkType() == WorkType.FLEXIBLE) {
                attendance.applyFlexibleRule(rule);
            }
        }

        return attendance;
    }


    // 출근 처리
    public static Attendance checkIn(User user, Company company, CompanyRule rule) {
        LocalTime now = LocalTime.now();
        LocalDate today = LocalDate.now();

        Attendance attendance = new Attendance();
        attendance.user = user;
        attendance.company = company;
        attendance.workDate = today;
        attendance.checkIn = now;

        // 근무 규칙이 없을 경우 NORMAL 처리
        if (rule == null || rule.getWorkType() == null) {
            attendance.status = AttendanceStatus.NORMAL;
            attendance.note = "checked in (no rule)";
            return attendance;
        }

        // FLEXIBLE 근무제: 지각 판단 없음
        if (rule.getWorkType() == WorkType.FLEXIBLE) {
            attendance.status = AttendanceStatus.NORMAL;
            attendance.note = "checked in (flexible)";
            return attendance;
        }

        // FIXED 근무제: 기준시간 + 지각 허용분 계산
        LocalTime lateLimit = rule.getStandardCheckIn()
                .plusMinutes(rule.getLateTolerance() != null ? rule.getLateTolerance() : 0);

        attendance.status = now.isAfter(lateLimit)
                ? AttendanceStatus.LATE
                : AttendanceStatus.NORMAL;

        attendance.note = "checked in";
        return attendance;
    }

    private static AttendanceStatus determineStatusOnCheckIn(LocalTime checkInTime, CompanyRule rule) {
        if (rule == null || rule.getWorkType() == null) {
            return AttendanceStatus.NORMAL;
        }
        if (rule.getWorkType() == WorkType.FLEXIBLE) {
            return AttendanceStatus.NORMAL;
        }

        LocalTime standardCheckIn = rule.getStandardCheckIn() != null ? rule.getStandardCheckIn() : LocalTime.of(9, 0);
        int lateTolerance = rule.getLateTolerance() != null ? rule.getLateTolerance() : 0;
        LocalTime lateLimit = standardCheckIn.plusMinutes(lateTolerance);

        return checkInTime.isAfter(lateLimit) ? AttendanceStatus.LATE : AttendanceStatus.NORMAL;
    }
    // 퇴근 처리
    public Attendance checkOut(CompanyRule rule) {
        if (this.checkOut != null) {
            throw new IllegalStateException("이미 퇴근 처리된 근태 기록입니다.");
        }

        this.checkOut = LocalTime.now();
        this.workHours = calculateWorkHours();
        this.shortfallMinutes = null;

        if (rule != null) {
            if (rule.getWorkType() == WorkType.FIXED) {
                handleFixedRule(rule);
            } else if (rule.getWorkType() == WorkType.FLEXIBLE) {
                applyFlexibleRule(rule);
            }
        }

        if (this.note == null) {
            this.note = "checked out";
        }
        return this;
    }

    // 일한 시간 계산
    private int calculateWorkHours() {
        if (checkIn != null && checkOut != null) {
            Duration duration = Duration.between(checkIn, checkOut);
            this.workMinutes = (int) duration.toMinutes();
            return (int) duration.toHours();
        }
        this.workMinutes = 0;
        return 0;
    }

    // 고정 근무 규칙 반영
    private void handleFixedRule(CompanyRule rule) {
        LocalTime earlyLeaveLimit = rule.getStandardCheckOut()
                .minusMinutes(rule.getEarlyLeaveTolerance() != null ? rule.getEarlyLeaveTolerance() : 0);

        if (this.status != AttendanceStatus.LATE && this.checkOut.isBefore(earlyLeaveLimit)) {
            this.status = AttendanceStatus.EARLY_LEAVE;
        }
        this.shortfallMinutes = null;
        this.note = "checked out";
    }

    private void applyFixedRuleOnSeed(CompanyRule rule) {
        LocalTime standardCheckOut = rule.getStandardCheckOut() != null ? rule.getStandardCheckOut() : LocalTime.of(18, 0);
        int earlyLeaveTolerance = rule.getEarlyLeaveTolerance() != null ? rule.getEarlyLeaveTolerance() : 0;
        LocalTime earlyLeaveLimit = standardCheckOut.minusMinutes(earlyLeaveTolerance);

        if (this.status != AttendanceStatus.LATE && this.checkOut.isBefore(earlyLeaveLimit)) {
            this.status = AttendanceStatus.EARLY_LEAVE;
        }
        this.shortfallMinutes = null;
    }

    // 유연 근무 규칙 반영
    private void applyFlexibleRule(CompanyRule rule) {
        BigDecimal requiredHours = rule.getRequiredHours();
        if (requiredHours == null) {
            this.shortfallMinutes = null;
            this.note = "checked out (flexible)";
            return;
        }

        int requiredMinutes = toMinutes(requiredHours);
        if (this.workMinutes < requiredMinutes) {
            this.shortfallMinutes = requiredMinutes - this.workMinutes;
            this.status = AttendanceStatus.SHORT_WORK;
            this.note = "checked out (short by " + this.shortfallMinutes + " minutes)";
        } else {
            this.shortfallMinutes = null;
            this.status = AttendanceStatus.NORMAL;
            this.note = "checked out (flexible)";
        }
    }

    // 시간 값을 분 단위로 변환
    private int toMinutes(BigDecimal hours) {
        return hours.multiply(BigDecimal.valueOf(60))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
    }
}
