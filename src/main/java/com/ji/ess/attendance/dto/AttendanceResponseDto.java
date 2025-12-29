package com.ji.ess.attendance.dto;

import com.ji.ess.attendance.entity.Attendance;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

// 근태 응답 DTO
@NoArgsConstructor
@Getter
@Schema(description = "근태 응답")
public class AttendanceResponseDto {
    @Schema(description = "근태 ID", example = "1")
    private Long id;
    @Schema(description = "사원 이름", example = "홍길동")
    private String username;         // 사원 이름
    @Schema(description = "회사 이름", example = "ThunderDev")
    private String companyName;      // 회사 이름
    @Schema(description = "근무 날짜", type = "string", format = "date", example = "2025-01-01")
    private LocalDate workDate;      // 근무 날짜
    @Schema(description = "출근 시간", type = "string", format = "time", example = "09:00:00")
    private LocalTime checkIn;       // 출근 시간
    @Schema(description = "퇴근 시간", type = "string", format = "time", example = "18:00:00")
    private LocalTime checkOut;      // 퇴근 시간
    @Schema(description = "근무 시간(시간 단위)", example = "8")
    private int workHours;           // 근무 시간(시간 단위)
    @Schema(description = "근무 시간(분 단위)", example = "0")
    private int workMinutes;         // 근무 시간(분 단위)
    @Schema(description = "유연근무 부족분(분)", example = "0")
    private Integer shortfallMinutes; // 유연근무 부족분(분)
    @Schema(description = "상태", example = "정상")
    private String status;           // 정상 / 지각 / 조퇴 / 결근
    @Schema(description = "비고", example = "정상 출근")
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
