package com.vitor.cassinoapi.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "transaction")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;
    @ManyToOne
    @JoinColumn(name ="player_id")
    private Player player;
    private Double value;
    private Boolean canceled;
    private String type;

    public Transaction(Player player, Double value, Boolean canceled, String type){
        this.player = player;
        this.value = value;
        this.canceled = canceled;
        this.type = type;
    }
}
