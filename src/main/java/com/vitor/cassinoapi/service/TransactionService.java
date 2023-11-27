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
        Player player = findPlayer(playerId);

        return new BalanceResponse(playerId, player.getBalance());        
    }

    public BetWinResponse bet(BetWinRequest betWinRequest){
        Player player = findPlayer(betWinRequest.player());
        Double value = betWinRequest.value();

        player = withdrawal(player, value);
        Transaction transaction = createTransaction(player, value, typeBet);

        return createBetWinResponse(player, transaction.getTransactionId());
    }

    public BetWinResponse win(BetWinRequest betWinRequest){
        Player player = findPlayer(betWinRequest.player());
        Double value = betWinRequest.value();

        player = deposit(player, value);
        Transaction transaction = createTransaction(player, value, typeWin);
        
        return createBetWinResponse(player, transaction.getTransactionId());
    }

    public Object rollback(RollbackRequest rollbackRequest){
        Player player = findPlayer(rollbackRequest.player());
        Transaction transaction = findTransaction(rollbackRequest.txn());
        
        Object object = rollbackValidation(transaction.getType(), transaction.getCanceled(), player.getBalance(), rollbackRequest.value(), transaction.getValue());
        if(object != null){
            return object;
        }
        
        player = deposit(player, rollbackRequest.value());
        transaction = cancelTransaction(transaction);

        return new RollbackOkResponse("OK", player.getBalance());
    }

    public Object rollbackValidation(String type, Boolean canceled, Double balance, Double requestedValue, Double transactionValue){
        if(type.equals(typeWin)){

            return new RollbackInvalidResponse("Invalid");
        } else if(canceled == true){

            return new RollbackOkResponse("OK", balance);
        } else if(!requestedValue.equals(transactionValue)){

            throw new ResourceBadRequestException("The requested value is different than the transaction value!");
        }

        return null;
    }

    public Player findPlayer(Long id){
       Player player = this.playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found!"));

        return player;
    }

    public Player deposit(Player player, Double value){
        player.deposit(value);
        this.playerRepository.save(player);

        return player;
    }

    public Player withdrawal(Player player, Double value){
        player.withdrawal(value);
        this.playerRepository.save(player);

        return player;
    }

    public Transaction createTransaction(Player player, Double value, String type){
        Transaction transaction = new Transaction(player, value, false, type);
        transaction = this.transactionRepository.save(transaction);

        return transaction;
    }

    public Transaction cancelTransaction(Transaction transaction){
        transaction.setCanceled(true);
        this.transactionRepository.save(transaction);

        return transaction;
    }

     public Transaction findTransaction(long id){
        Transaction transaction = this.transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found!"));

        return transaction;
    }

    public BetWinResponse createBetWinResponse(Player player, Long txn){
        return new BetWinResponse(player.getPlayerId(), player.getBalance(), txn);
    }
}
