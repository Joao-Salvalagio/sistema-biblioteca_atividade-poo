package com.biblioteca.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_usuarios")
@Data //marcacao do lombok para getters e setters
@NoArgsConstructor //marcacao do lombok para construtor vazio
@AllArgsConstructor //marcacao do lombok para construtor com parametros
public class UsuarioModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String documento;

    private String telefone;
    private String email;

    @Column(nullable = false)
    private Integer limiteEmprestimos = 3; //definicao default de 3 itens permitidos

    @Column(nullable = false)
    private BigDecimal multaPendente = BigDecimal.ZERO;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL) //relacao de um para muitos
    @JsonIgnoreProperties("usuario")
    private List<EmprestimoModel> emprestimos = new ArrayList<>();
}