package com.ji.ess.companyRule.repository;

import com.ji.ess.companyRule.entity.CompanyRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRuleRepository extends JpaRepository<CompanyRule, Long> {
    Optional<CompanyRule> findByCompanyId(Long companyId); //
}
