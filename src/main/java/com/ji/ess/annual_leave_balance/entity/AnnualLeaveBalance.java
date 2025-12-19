package com.ji.ess.annual_leave_balance.entity;

import com.ji.ess.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// 연차 잔액 엔티티
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "annual_leave_balance")
public class AnnualLeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int year;        // 연도 (예: 2025)

    @Column(nullable = false, precision = 5, scale = 1)
    private BigDecimal granted;     // 부여된 일수(0.5 단위 지원)

    @Column(nullable = false, precision = 5, scale = 1)
    private BigDecimal used;        // 사용된 일수(0.5 단위 지원)

    @Column(nullable = false, precision = 5, scale = 1)
    private BigDecimal remaining;   // 잔여 일수(0.5 단위 지원)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private AnnualLeaveBalance(User user, int year, BigDecimal granted) {
        this.user = user;
        this.year = year;
        this.granted = granted;
        this.used = BigDecimal.ZERO;
        this.remaining = granted;
    }

    // 필수값으로 연차 잔액 생성
    @Builder(builderMethodName = "builder")
    public static AnnualLeaveBalance of(User user, int year, BigDecimal granted) {
        return new AnnualLeaveBalance(user, year, granted);
    }

    // 연차 사용/복원 메서드
    public void useLeave(BigDecimal days) {
        if (days == null || days.signum() <= 0) return;
        this.used = this.used.add(days);
        this.remaining = this.remaining.subtract(days);
    }

    public void rollbackLeave(BigDecimal days) {
        if (days == null || days.signum() <= 0) return;
        this.used = this.used.subtract(days);
        this.remaining = this.remaining.add(days);
    }

    // 자동 부여 증가 처리
    public void grantExtraDays(BigDecimal days) {
        if (days == null || days.signum() <= 0) return;
        this.granted = this.granted.add(days);
        this.remaining = this.remaining.add(days);
    }
}
