package com.biblioteca.controllers;

import com.biblioteca.models.ItemBibliotecaModel;
import com.biblioteca.models.ItemBibliotecaModel.TipoItem;
import com.biblioteca.services.ItemBibliotecaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itens")
@CrossOrigin(origins = "*")
public class ItemBibliotecaController {

    @Autowired
    private ItemBibliotecaService itemService;

    @PostMapping
    public ResponseEntity<ItemBibliotecaModel> criarItem(@RequestBody ItemBibliotecaModel item) {
        ItemBibliotecaModel novoItem = itemService.criarItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoItem);
    }

    @GetMapping
    public ResponseEntity<List<ItemBibliotecaModel>> listarTodos() {
        return ResponseEntity.ok(itemService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemBibliotecaModel> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.buscarPorId(id));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<ItemBibliotecaModel>> listarPorTipo(@PathVariable TipoItem tipo) {
        return ResponseEntity.ok(itemService.listarPorTipo(tipo));
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<ItemBibliotecaModel>> listarDisponiveis() {
        return ResponseEntity.ok(itemService.listarDisponiveis());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemBibliotecaModel> atualizarItem(@PathVariable Long id, @RequestBody ItemBibliotecaModel item) {
        return ResponseEntity.ok(itemService.atualizarItem(id, item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarItem(@PathVariable Long id) {
        itemService.deletarItem(id);
        return ResponseEntity.noContent().build();
    }
}