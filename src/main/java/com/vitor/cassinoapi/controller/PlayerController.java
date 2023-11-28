package com.vitor.cassinoapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vitor.cassinoapi.model.response.PlayerResponse;
import com.vitor.cassinoapi.service.PlayerService;

/**
 * Controller responsavel pelo mapeamento referente ao player.
 */

@RestController
@RequestMapping("/player")
public class PlayerController {
    @Autowired
    PlayerService playerService;

    /**
     * Mapeamento responsavel por se comunicar com o metodo 'create' do service 
     * que retorna um  novo player
     * 
     * @return um PlayerResponse, classe com informações do player gerado
     */
    @PostMapping("/create")
    public ResponseEntity<PlayerResponse> create(){
        PlayerResponse player = playerService.create();
        return new ResponseEntity<>(player, HttpStatus.CREATED);
    }
}
