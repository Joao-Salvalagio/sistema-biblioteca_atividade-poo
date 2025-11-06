package com.biblioteca.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_itens_biblioteca")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemBibliotecaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoItem tipo;

    @Column(nullable = false)
    private String titulo;

    private String autor;
    private String categoria;

    private String editora;
    private String dataPublicacao;

    @Column(nullable = false)
    private Boolean disponivel = true;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("item")
    private List<EmprestimoModel> emprestimos = new ArrayList<>();

    public enum TipoItem {
        LIVRO,
        PERIODICO
    }
}