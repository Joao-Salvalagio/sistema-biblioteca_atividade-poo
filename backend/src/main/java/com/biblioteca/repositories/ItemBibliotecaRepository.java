package com.biblioteca.repositories;

import com.biblioteca.models.ItemBibliotecaModel;
import com.biblioteca.models.ItemBibliotecaModel.TipoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemBibliotecaRepository extends JpaRepository<ItemBibliotecaModel, Long> {
    List<ItemBibliotecaModel> findByTipo(TipoItem tipo);
    List<ItemBibliotecaModel> findByDisponivel(Boolean disponivel);
}