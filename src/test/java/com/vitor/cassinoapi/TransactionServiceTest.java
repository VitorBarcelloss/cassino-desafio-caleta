package com.vitor.cassinoapi;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vitor.cassinoapi.error.exception.ResourceBadRequestException;
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
    @DisplayName("Test balance retrieval functionality with correct player and balance values")
    public void testBalance(){
        Player player = new Player(1L, 100.0);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        BalanceResponse balanceResponse = transactionService.balance(1L);

        Assertions.assertNotNull(balanceResponse);
        Assertions.assertEquals(1L, balanceResponse.player());
        Assertions.assertEquals(100.0, balanceResponse.balance());
    }

    @Test
    @DisplayName("Test bet response functionality with correct player and bet values")
    public void testBetFirstScenario() {
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
    @DisplayName("Test bet response functionality with correct player but wrong bet value")
    public void testBetSecondScenario() {
        Double betValue = 500.00;
        Player mockedPlayer = Mockito.mock(Player.class);
        
        Player player = new Player(1L, 250.00);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        BetWinRequest betWinRequest = new BetWinRequest(player.getPlayerId(), betValue);

        doThrow(new ResourceBadRequestException("Invalid withdrawal. Your balance is lower than the requested value!"))
            .when(mockedPlayer).withdrawal(betValue);

        assertThrows(ResourceBadRequestException.class, () -> {
            transactionService.bet(betWinRequest);
        });
    }

    @Test
    @DisplayName("Test win response funcionality with correct player and deposit values")
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
    @DisplayName("Test rollback response funcionality with correct values for transaction")
    public void testRollbackFirstScenario(){
         Player player = new Player(1L, 0.0);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        Transaction transaction = new Transaction(1L, player, 250.00, false, "bet");
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        Object object = transactionService.rollback(new RollbackRequest(1L, 1L, 250.00));
        RollbackOkResponse  rollbackOkResponse = new RollbackOkResponse("OK", 250.00) ;

        Assertions.assertNotNull(object);
        Assertions.assertEquals(rollbackOkResponse, object);
    }

    @Test
    @DisplayName("Test rollback response with transaction type 'win' instead of 'bet")
    public void testRollbackSecondScenario(){
         Player player = new Player(1L, 0.0);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        Transaction transaction = new Transaction(1L, player, 250.00, false, "win");
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        Object object = transactionService.rollback(new RollbackRequest(1L, 1L, 250.00));
        RollbackInvalidResponse  rollbackInvalidResponse = new RollbackInvalidResponse("Invalid") ;

        Assertions.assertNotNull(object);
        Assertions.assertEquals(rollbackInvalidResponse, object);
    }

    @Test
    @DisplayName("Test rollback response with a canceled transaction")
    public void testRollbackThirdScenario(){
         Player player = new Player(1L, 0.0);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        Transaction transaction = new Transaction(1L, player, 250.00, true, "bet");
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        Object object = transactionService.rollback(new RollbackRequest(1L, 1L, 250.00));
       RollbackOkResponse  rollbackOkResponse = new RollbackOkResponse("OK", 0.0) ;

        Assertions.assertNotNull(object);
        Assertions.assertEquals(rollbackOkResponse, object);
    }

    @Test
    @DisplayName("Test rollback response with a requested value different than the transaction value")
    public void testRollbackFourthScenario(){
        Player player = new Player(1L, 0.0);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        Transaction transaction = new Transaction(1L, player, 250.00, true, "bet");
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        RollbackRequest rollbackRequest = new RollbackRequest(1L, 1L, 300.00);
        when(transactionService.rollback(rollbackRequest)).thenThrow(new ResourceBadRequestException("The requested value is different than the transaction value!"));
        
        assertThrows(ResourceBadRequestException.class, () -> {
        transactionService.rollback(new RollbackRequest(1L, 1L, 300.00));
       });
    }
}
