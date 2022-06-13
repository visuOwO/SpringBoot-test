package com.example.hld.springbootdemo.dbFiles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface msgDataRepository extends JpaRepository<msg_metadata,Long> {
}
