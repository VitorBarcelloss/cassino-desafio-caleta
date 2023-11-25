package com.vitor.cassinoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vitor.cassinoapi.model.entity.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    
}
