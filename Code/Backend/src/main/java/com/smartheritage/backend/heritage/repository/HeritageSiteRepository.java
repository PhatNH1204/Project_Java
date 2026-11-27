package com.smartheritage.backend.heritage.repository;

import com.smartheritage.backend.heritage.entity.HeritageSite;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeritageSiteRepository extends JpaRepository<HeritageSite, Long> {

    List<HeritageSite> findByActiveTrueOrderByNameAsc();
}
