package com.ji.ess.company.repository;

import com.ji.ess.company.dto.CompanyResponseDto;
import com.ji.ess.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    @Modifying
    @Query(value = "TRUNCATE TABLE companies", nativeQuery = true)
    void truncateCompanies();
    @Modifying
    @Query(value = "DELETE FROM companies", nativeQuery = true)
    void deleteAllCompanies();
    //회사 조회
    List<Company> findByCompanyNameContaining(String name);

}
