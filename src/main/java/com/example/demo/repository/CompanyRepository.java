package com.example.demo.repository;

import com.example.demo.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query(value="select * from company where symbol_code=?1",nativeQuery=true)
    Company getCompanyBySymbol_Code(String Symbol_Code);
}


