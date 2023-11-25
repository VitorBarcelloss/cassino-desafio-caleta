package com.vitor.cassinoapi.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ErrorMessage {
    private String titulo;
    private Integer status;
    private String mensagem;
}