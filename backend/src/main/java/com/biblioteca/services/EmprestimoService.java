package com.biblioteca.services;

import com.biblioteca.models.EmprestimoModel;
import com.biblioteca.models.EmprestimoModel.StatusEmprestimo;
import com.biblioteca.models.ItemBibliotecaModel;
import com.biblioteca.models.UsuarioModel;
import com.biblioteca.repositories.EmprestimoRepository;
import com.biblioteca.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class EmprestimoService {
    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ItemBibliotecaService itemService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public EmprestimoModel realizarEmprestimo(Long usuarioId, Long itemId, Integer diasEmprestimo) {
        UsuarioModel usuario = usuarioService.buscarPorId(usuarioId);
        ItemBibliotecaModel item = itemService.buscarPorId(itemId);

        if (!item.getDisponivel()) {
            throw new RuntimeException("Item não disponível para empréstimo!");
        }

        List<EmprestimoModel> emprestimosAtivos = emprestimoRepository.findByUsuarioIdAndStatus(usuarioId, StatusEmprestimo.ATIVO);

        if (emprestimosAtivos.size() >= usuario.getLimiteEmprestimos()) {
            throw new RuntimeException("Usuário atingiu o limite de empréstimos simultâneos!");
        }

        for (EmprestimoModel emp : emprestimosAtivos) {
            if (emp.getStatus() == StatusEmprestimo.ATRASADO) {
                throw new RuntimeException("Usuário possui itens em atraso!");
            }
        }

        if (usuario.getMultaPendente().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Usuário possui multa pendente de R$ " + usuario.getMultaPendente());
        }

        EmprestimoModel emprestimo = new EmprestimoModel();
        emprestimo.setUsuario(usuario);
        emprestimo.setItem(item);
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataDevolucaoPrevista(LocalDate.now().plusDays(diasEmprestimo));
        emprestimo.setStatus(StatusEmprestimo.ATIVO);

        item.setDisponivel(false);
        itemService.atualizarItem(itemId, item);

        return emprestimoRepository.save(emprestimo);
    }

    @Transactional
    public EmprestimoModel realizarDevolucao(Long emprestimoId) {
        EmprestimoModel emprestimo = buscarPorId(emprestimoId);

        if (emprestimo.getStatus() == StatusEmprestimo.DEVOLVIDO) {
            throw new RuntimeException("Empréstimo já foi devolvido!");
        }

        LocalDate hoje = LocalDate.now();
        emprestimo.setDataDevolucaoReal(hoje);

        if (hoje.isAfter(emprestimo.getDataDevolucaoPrevista())) {
            long diasAtraso = ChronoUnit.DAYS.between(emprestimo.getDataDevolucaoPrevista(), hoje);
            BigDecimal multaDiaria = new BigDecimal("2.00");
            BigDecimal multaTotal = multaDiaria.multiply(new BigDecimal(diasAtraso));

            emprestimo.setMultaAplicada(multaTotal);

            UsuarioModel usuario = emprestimo.getUsuario();
            usuario.setMultaPendente(usuario.getMultaPendente().add(multaTotal));
            usuarioRepository.save(usuario);
        }

        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);

        ItemBibliotecaModel item = emprestimo.getItem();
        item.setDisponivel(true);
        itemService.atualizarItem(item.getId(), item);

        return emprestimoRepository.save(emprestimo);
    }

    @Transactional
    public UsuarioModel pagarMulta(Long usuarioId, BigDecimal valorPagamento) {
        UsuarioModel usuario = usuarioService.buscarPorId(usuarioId);

        if (valorPagamento == null || valorPagamento.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor de pagamento deve ser maior que zero!");
        }

        if (valorPagamento.compareTo(usuario.getMultaPendente()) > 0) {
            throw new RuntimeException("Valor maior que a multa pendente!");
        }

        usuario.setMultaPendente(usuario.getMultaPendente().subtract(valorPagamento));
        return usuarioRepository.save(usuario);
    }

    public void verificarAtrasos() {
        List<EmprestimoModel> emprestimosAtivos = emprestimoRepository.findByStatus(StatusEmprestimo.ATIVO);

        LocalDate hoje = LocalDate.now();

        for (EmprestimoModel emprestimo : emprestimosAtivos) {
            if (hoje.isAfter(emprestimo.getDataDevolucaoPrevista())) {
                emprestimo.setStatus(StatusEmprestimo.ATRASADO);
                emprestimoRepository.save(emprestimo);
            }
        }
    }

    public List<EmprestimoModel> listarTodos() {
        return emprestimoRepository.findAll();
    }

    public EmprestimoModel buscarPorId(Long id) {
        return emprestimoRepository.findById(id).orElseThrow(() -> new RuntimeException("Empréstimo não encontrado!"));
    }

    public List<EmprestimoModel> listarPorUsuario(Long usuarioId) {
        return emprestimoRepository.findByUsuarioId(usuarioId);
    }

    public List<EmprestimoModel> listarAtivos() {
        return emprestimoRepository.findByStatus(StatusEmprestimo.ATIVO);
    }

    public List<EmprestimoModel> listarAtrasados() {
        return emprestimoRepository.findByStatus(StatusEmprestimo.ATRASADO);
    }
}