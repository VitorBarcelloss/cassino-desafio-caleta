package com.vitor.cassinoapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vitor.cassinoapi.model.entity.Player;
import com.vitor.cassinoapi.model.response.PlayerResponse;
import com.vitor.cassinoapi.repository.PlayerRepository;

@Service
public class PlayerService {
    @Autowired
    PlayerRepository playerRepository;

    public PlayerResponse create(){
        Player player = new Player(0.0);
        player = playerRepository.save(player);

        return new PlayerResponse(player.getPlayerId());
    }
}
