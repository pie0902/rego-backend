package com.ji.ess.companyRule.entity;


import com.ji.ess.company.entity.Company;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

// 근무 규칙 엔티티
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "company_rule")
public class CompanyRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkType workType; // FIXED or FLEXIBLE

    private LocalTime standardCheckIn;      // 고정 근무 출근 기준시간
    private LocalTime standardCheckOut;     // 고정 근무 퇴근 기준시간
    private BigDecimal requiredHours;       // 유연근무 시 필수 근무시간
    private Integer lateTolerance;          // 지각 허용분
    private Integer earlyLeaveTolerance;    // 조퇴 허용분
    private Boolean weekendWorkAllowed;     // 주말 근무 허용 여부

    @OneToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    private CompanyRule(Company company,
                        WorkType workType,
                        LocalTime standardCheckIn,
                        LocalTime standardCheckOut,
                        BigDecimal requiredHours,
                        Integer lateTolerance,
                        Integer earlyLeaveTolerance,
                        Boolean weekendWorkAllowed) {
        this.company = company;
        this.workType = workType;
        this.standardCheckIn = standardCheckIn;
        this.standardCheckOut = standardCheckOut;
        this.requiredHours = requiredHours;
        this.lateTolerance = lateTolerance;
        this.earlyLeaveTolerance = earlyLeaveTolerance;
        this.weekendWorkAllowed = weekendWorkAllowed;
    }

    // 필수값으로 규칙 생성
    @Builder(builderMethodName = "builder")
    public static CompanyRule create(Company company,
                                     WorkType workType,
                                     LocalTime standardCheckIn,
                                     LocalTime standardCheckOut,
                                     BigDecimal requiredHours,
                                     Integer lateTolerance,
                                     Integer earlyLeaveTolerance,
                                     Boolean weekendWorkAllowed) {
        return new CompanyRule(company, workType, standardCheckIn, standardCheckOut, requiredHours,
                lateTolerance, earlyLeaveTolerance, weekendWorkAllowed);
    }

    // 규칙 수정
    public void update(WorkType workType,
                       LocalTime standardCheckIn,
                       LocalTime standardCheckOut,
                       BigDecimal requiredHours,
                       Integer lateTolerance,
                       Integer earlyLeaveTolerance,
                       Boolean weekendWorkAllowed) {
        this.workType = workType;
        this.standardCheckIn = standardCheckIn;
        this.standardCheckOut = standardCheckOut;
        this.requiredHours = requiredHours;
        this.lateTolerance = lateTolerance;
        this.earlyLeaveTolerance = earlyLeaveTolerance;
        this.weekendWorkAllowed = weekendWorkAllowed;
    }
}
