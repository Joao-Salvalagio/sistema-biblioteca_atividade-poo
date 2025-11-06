package com.biblioteca.services;

import com.biblioteca.models.ItemBibliotecaModel;
import com.biblioteca.models.ItemBibliotecaModel.TipoItem;
import com.biblioteca.repositories.ItemBibliotecaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemBibliotecaService {
    @Autowired
    private ItemBibliotecaRepository itemRepository;

    public ItemBibliotecaModel criarItem(ItemBibliotecaModel item) {
        return itemRepository.save(item);
    }

    public List<ItemBibliotecaModel> listarTodos() {
        return itemRepository.findAll();
    }

    public ItemBibliotecaModel buscarPorId(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item não encontrado!"));
    }

    public List<ItemBibliotecaModel> listarPorTipo(TipoItem tipo) {
        return itemRepository.findByTipo(tipo);
    }

    public List<ItemBibliotecaModel> listarDisponiveis() {
        return itemRepository.findByDisponivel(true);
    }

    public ItemBibliotecaModel atualizarItem(Long id, ItemBibliotecaModel itemAtualizado) {
        ItemBibliotecaModel item = buscarPorId(id);

        item.setTitulo(itemAtualizado.getTitulo());
        item.setAutor(itemAtualizado.getAutor());
        item.setCategoria(itemAtualizado.getCategoria());
        item.setEditora(itemAtualizado.getEditora());
        item.setDataPublicacao(itemAtualizado.getDataPublicacao());

        return itemRepository.save(item);
    }

    public void deletarItem(Long id) {
        ItemBibliotecaModel item = buscarPorId(id);
        itemRepository.delete(item);
    }
}