package com.ji.ess.attendance.dto;

import com.ji.ess.attendance.entity.Attendance;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

// 근태 응답 DTO
@NoArgsConstructor
@Getter
public class AttendanceResponseDto {
    private Long id;
    private String username;         // 사원 이름
    private String companyName;      // 회사 이름
    private LocalDate workDate;      // 근무 날짜
    private LocalTime checkIn;       // 출근 시간
    private LocalTime checkOut;      // 퇴근 시간
    private int workHours;           // 근무 시간(시간 단위)
    private int workMinutes;         // 근무 시간(분 단위)
    private Integer shortfallMinutes; // 유연근무 부족분(분)
    private String status;           // 정상 / 지각 / 조퇴 / 결근
    private String note;             // 비고

    //Attendance → DTO 변환
    public AttendanceResponseDto(Attendance attendance) {
        this.id = attendance.getId();
        this.username = attendance.getUser().getUsername();
        this.companyName = attendance.getCompany().getCompanyName();
        this.workDate = attendance.getWorkDate();
        this.checkIn = attendance.getCheckIn();
        this.checkOut = attendance.getCheckOut();
        this.workHours = attendance.getWorkHours();
        this.workMinutes = attendance.getWorkMinutes();
        this.shortfallMinutes = attendance.getShortfallMinutes();
        this.status = attendance.getStatus().toString();
        this.note = attendance.getNote();
    }
}
