package com.ji.ess.leave_request.entity;

import com.ji.ess.company.entity.Company;
import com.ji.ess.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 휴가 신청 엔티티
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "leave_request")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // 승인자 (CEO or MANAGER)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    // 기간
    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
    private LocalDate endDate;

    // 타입: 조퇴 / 반차 / 연차 / 병가
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType type;

    // 상태: 대기 / 승인 / 거절
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus status;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;

    private LeaveRequest(User user,
                         Company company,
                         LocalDate startDate,
                         LocalDate endDate,
                         LeaveType type,
                         String reason,
                         LocalDateTime requestedAt) {
        this.user = user;
        this.company = company;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.reason = reason;
        this.status = LeaveStatus.PENDING;
        this.requestedAt = requestedAt != null ? requestedAt : LocalDateTime.now();
    }

    @Builder(builderMethodName = "builder")
    // 필수값으로 휴가 신청 생성
    public static LeaveRequest create(User user,
                                      Company company,
                                      LocalDate startDate,
                                      LocalDate endDate,
                                      LeaveType type,
                                      String reason,
                                      LocalDateTime requestedAt) {
        return new LeaveRequest(user, company, startDate, endDate, type, reason, requestedAt);
    }

    // 승인/거절 처리
    // 승인 처리
    public void approve(User approver) {
        this.status = LeaveStatus.APPROVED;
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
    }

    // 거절 처리
    public void reject(User approver) {
        this.status = LeaveStatus.REJECTED;
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
    }
}
