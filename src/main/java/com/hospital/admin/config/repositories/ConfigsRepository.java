package com.hospital.admin.config.repositories;

import com.hospital.admin.config.entities.Configs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigsRepository extends JpaRepository<Configs, String> { //기본키 string
}
