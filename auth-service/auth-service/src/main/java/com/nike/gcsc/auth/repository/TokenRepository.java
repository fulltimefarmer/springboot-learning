package com.nike.gcsc.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nike.gcsc.auth.entity.Token;

import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query(value = "SELECT * FROM gcsc_token token WHERE token.token = ?1", nativeQuery = true)
    Token findByToken(String token);

	@Query(value = "SELECT * FROM gcsc_token token WHERE token.create_time >= ?1", nativeQuery = true)
    List<Token> findByCreateTime(long seconds);

}
