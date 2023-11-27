package com.vitor.cassinoapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vitor.cassinoapi.model.entity.Player;
import com.vitor.cassinoapi.model.entity.Transaction;
import com.vitor.cassinoapi.model.request.BetWinRequest;
import com.vitor.cassinoapi.model.request.RollbackRequest;
import com.vitor.cassinoapi.model.response.BalanceResponse;
import com.vitor.cassinoapi.model.response.BetWinResponse;
import com.vitor.cassinoapi.repository.PlayerRepository;
import com.vitor.cassinoapi.repository.TransactionRepository;
import com.vitor.cassinoapi.service.TransactionService;

public class TransactionServiceTest {
     @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testBalance(){
        Player player = new Player(1L, 100.0);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        BalanceResponse balanceResponse = transactionService.balance(1L);

        Assertions.assertNotNull(balanceResponse);
        Assertions.assertEquals(1L, balanceResponse.player());
        Assertions.assertEquals(100.0, balanceResponse.balance());
    }

    @Test
    public void testBet() {
        Double betValue = 250.00;
        Player player = new Player(1L, 500.00);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        Transaction transaction = new Transaction(); // Crie uma instância válida de Transaction
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        BetWinRequest betWinRequest = new BetWinRequest(player.getPlayerId(), betValue);

        BetWinResponse betWinResponse = transactionService.bet(betWinRequest);

        Assertions.assertNotNull(betWinResponse);
        Assertions.assertEquals(250.00, betWinResponse.balance());
        Assertions.assertEquals(player.getPlayerId(), betWinResponse.player());
    }   

    @Test
    public void testWin(){
        Double winValue = 300.00;
        Player player = new Player(1L, 0.0);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        Transaction transaction = new Transaction();
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        BetWinRequest betWinRequest = new BetWinRequest(player.getPlayerId(), winValue);

        BetWinResponse betWinResponse = transactionService.win(betWinRequest);

        Assertions.assertNotNull(betWinResponse);
        Assertions.assertEquals(300.00, betWinResponse.balance());
        Assertions.assertEquals(player.getPlayerId(), betWinResponse.player());
        Assertions.assertEquals(transaction.getTransactionId(), betWinResponse.txn());
    }

    @Test
    public void testRollback(){
         Player player = new Player(1L, 0.0);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        Transaction transaction = new Transaction(1L, player, 250.00, false, "bet");
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        Object object = transactionService.rollback(new RollbackRequest(1L, 1L, 250.00));

        Assertions.assertNotNull(object);
    }
}
