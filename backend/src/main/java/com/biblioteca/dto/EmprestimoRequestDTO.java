package com.biblioteca.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmprestimoRequestDTO {
    private Long usuarioId;
    private Long itemId;
    private Integer diasEmprestimo;
}