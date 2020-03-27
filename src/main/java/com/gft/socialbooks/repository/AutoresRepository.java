package com.gft.socialbooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gft.socialbooks.domain.Autor;

public interface AutoresRepository extends JpaRepository<Autor, Long> {

}
