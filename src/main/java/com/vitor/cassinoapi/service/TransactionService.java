package com.vitor.cassinoapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vitor.cassinoapi.error.exception.ResourceBadRequestException;
import com.vitor.cassinoapi.error.exception.ResourceNotFoundException;
import com.vitor.cassinoapi.model.entity.Player;
import com.vitor.cassinoapi.model.entity.Transaction;
import com.vitor.cassinoapi.model.request.BetWinRequest;
import com.vitor.cassinoapi.model.request.RollbackRequest;
import com.vitor.cassinoapi.model.response.BalanceResponse;
import com.vitor.cassinoapi.model.response.BetWinResponse;
import com.vitor.cassinoapi.model.response.RollbackInvalidResponse;
import com.vitor.cassinoapi.model.response.RollbackOkResponse;
import com.vitor.cassinoapi.repository.PlayerRepository;
import com.vitor.cassinoapi.repository.TransactionRepository;

@Service
public class TransactionService {
    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    TransactionRepository transactionRepository;

    private static final String typeBet = "bet";
    private static final String typeWin = "win";

    public BalanceResponse balance(Long playerId){
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found!"));
        return new BalanceResponse(playerId, player.getBalance());        
    }

    public BetWinResponse bet(BetWinRequest betWinRequest){
        Player player = playerRepository.findById(betWinRequest.player())
                .orElseThrow(() -> new ResourceNotFoundException("Player not found!"));
        
        Double value = betWinRequest.value();
        player.withdrawal(value);
        playerRepository.save(player);

        Transaction transaction = new Transaction(player, value, false, typeBet);
        transaction = transactionRepository.save(transaction);

        return new BetWinResponse(player.getPlayerId(), player.getBalance(), transaction.getTransactionId());
    }

    public BetWinResponse win(BetWinRequest betWinRequest){
        Player player = playerRepository.findById(betWinRequest.player())
                .orElseThrow(() -> new ResourceNotFoundException("Player not found!"));
        
        Double value = betWinRequest.value();
        player.deposit(value);
        playerRepository.save(player);

        Transaction transaction = new Transaction(player, value, false, typeWin);
        transaction = transactionRepository.save(transaction);

        return new BetWinResponse(player.getPlayerId(), player.getBalance(), transaction.getTransactionId());
    }

    public Object rollback(RollbackRequest rollbackRequest){
        Player player = playerRepository.findById(rollbackRequest.player())
                .orElseThrow(() -> new ResourceNotFoundException("Player not found!"));

        Transaction transaction = transactionRepository.findById(rollbackRequest.txn())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found!"));
        
        Object object = rollbackValidation(transaction.getType(), transaction.getCanceled(), player.getBalance(), rollbackRequest.value(), transaction.getValue());
        if(object != null){
            return object;
        }
        
        player.deposit(rollbackRequest.value());
        playerRepository.save(player);

        transaction.setCanceled(true);
        transactionRepository.save(transaction);

        return new RollbackOkResponse("OK", player.getBalance());
    }

    public Object rollbackValidation(String type, Boolean canceled, Double balance, Double requestedValue, Double transactionValue){
        if(type.equals("win")){

            return new RollbackInvalidResponse("Invalid");
        } else if(canceled == true){

            return new RollbackOkResponse("OK", balance);
        } else if(!requestedValue.equals(transactionValue)){

            throw new ResourceBadRequestException("The requested value is different than the transaction value!");
        }

        return null;
    }
}
