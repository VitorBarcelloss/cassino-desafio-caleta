package com.vitor.cassinoapi.model.entity;

import com.vitor.cassinoapi.error.exception.ResourceBadRequestException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "player")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id")
    private Long playerId;    
    private Double balance;

    public Player(Double balance){
        this.balance = balance;
    }

    public Double withdrawal(Double value){
        if(this.balance < value){
           throw new ResourceBadRequestException("Invalid withdrawal. Your balance is lower than the requested value!"); 
        } else {
            return this.balance -= value;
        }
    }

    public Double deposit(Double value){
        return this.balance += value;
    }
}
