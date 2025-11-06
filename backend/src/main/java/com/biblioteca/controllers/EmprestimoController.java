package com.biblioteca.controllers;

import com.biblioteca.dto.EmprestimoRequestDTO;
import com.biblioteca.dto.PagamentoMultaDTO;
import com.biblioteca.models.EmprestimoModel;
import com.biblioteca.models.UsuarioModel;
import com.biblioteca.services.EmprestimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emprestimos")
@CrossOrigin(origins = "*")
public class EmprestimoController {
    @Autowired
    private EmprestimoService emprestimoService;

    @PostMapping
    public ResponseEntity<EmprestimoModel> realizarEmprestimo(@RequestBody EmprestimoRequestDTO dados) {
        EmprestimoModel emprestimo = emprestimoService.realizarEmprestimo(dados.getUsuarioId(), dados.getItemId(), dados.getDiasEmprestimo());
        return ResponseEntity.status(HttpStatus.CREATED).body(emprestimo);
    }

    @PutMapping("/devolver/{id}")
    public ResponseEntity<EmprestimoModel> realizarDevolucao(@PathVariable Long id) {
        return ResponseEntity.ok(emprestimoService.realizarDevolucao(id));
    }

    @PostMapping("/pagar-multa")
    public ResponseEntity<UsuarioModel> pagarMulta(@RequestBody PagamentoMultaDTO dados) {
        return ResponseEntity.ok(emprestimoService.pagarMulta(dados.getUsuarioId(), dados.getValorPagamento()));
    }

    @PostMapping("/verificar-atrasos")
    public ResponseEntity<String> verificarAtrasos() {
        emprestimoService.verificarAtrasos();
        return ResponseEntity.ok("Atrasos verificados e atualizados!");
    }

    @GetMapping
    public ResponseEntity<List<EmprestimoModel>> listarTodos() {
        return ResponseEntity.ok(emprestimoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmprestimoModel> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(emprestimoService.buscarPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<EmprestimoModel>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(emprestimoService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<EmprestimoModel>> listarAtivos() {
        return ResponseEntity.ok(emprestimoService.listarAtivos());
    }

    @GetMapping("/atrasados")
    public ResponseEntity<List<EmprestimoModel>> listarAtrasados() {
        return ResponseEntity.ok(emprestimoService.listarAtrasados());
    }
}