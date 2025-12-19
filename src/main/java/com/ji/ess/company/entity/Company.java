package com.ji.ess.company.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 회사 엔티티
@Entity
@Table(name="companies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String companyName;
    @Column(nullable = false)
    private String businessNumber;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private String ceoName;
    @Column(nullable = false)
    private String email;

    private Company(String companyName,
                   String businessNumber,
                   String address,
                   String ceoName,
                   String email) {
        this.companyName = companyName;
        this.businessNumber = businessNumber;
        this.address = address;
        this.ceoName = ceoName;
        this.email = email;
    }

    @Builder(builderMethodName = "builder")
    public static Company of(String companyName,
                             String businessNumber,
                             String address,
                             String ceoName,
                             String email) {
        return new Company(companyName, businessNumber, address, ceoName, email);
    }
}
