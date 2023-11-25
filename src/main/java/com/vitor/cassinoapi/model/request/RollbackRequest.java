package com.vitor.cassinoapi.model.request;

public record RollbackRequest(Long player, Long txn, Double value) {
 
}
