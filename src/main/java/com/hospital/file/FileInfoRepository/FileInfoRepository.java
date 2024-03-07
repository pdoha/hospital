package com.hospital.file.FileInfoRepository;

import com.hospital.file.entities.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long>,
        QuerydslPredicateExecutor<FileInfo> {

    //gid로 조회하기
    List<FileInfo> findByGid(String gid);

    //location으로 조회하기
    List<FileInfo> findByGidAndLocation(String gid, String location);
}
