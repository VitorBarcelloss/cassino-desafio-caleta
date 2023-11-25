package com.vitor.cassinoapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vitor.cassinoapi.model.request.BetWinRequest;
import com.vitor.cassinoapi.model.request.RollbackRequest;
import com.vitor.cassinoapi.model.response.BalanceResponse;
import com.vitor.cassinoapi.model.response.BetWinResponse;
import com.vitor.cassinoapi.service.TransactionService;

@RestController
@RequestMapping("")
public class TransactionController {
    @Autowired
    TransactionService transactionService;

    @GetMapping("/balance/{playerId}")
    public ResponseEntity<BalanceResponse> balance(@PathVariable Long playerId){
        BalanceResponse balanceResponse = transactionService.balance(playerId);
        return new ResponseEntity<>(balanceResponse, HttpStatus.OK);
    }

    @PostMapping("/bet")
    public ResponseEntity<BetWinResponse> bet(@RequestBody BetWinRequest betWinRequest){
        BetWinResponse betWinResponse = transactionService.bet(betWinRequest);
        return new ResponseEntity<>(betWinResponse, HttpStatus.OK);
    }

    @PostMapping("/win")
    public ResponseEntity<BetWinResponse> win(@RequestBody BetWinRequest betWinRequest){
        BetWinResponse betWinResponse = transactionService.win(betWinRequest);
        return new ResponseEntity<>(betWinResponse, HttpStatus.OK);
    }

    @PostMapping("/rollback")
    public ResponseEntity<?> rollback(@RequestBody RollbackRequest rollbackRequest){
        Object rollbackResponse = transactionService.rollback(rollbackRequest);
        return new ResponseEntity<>(rollbackResponse, HttpStatus.OK);
    }
}
