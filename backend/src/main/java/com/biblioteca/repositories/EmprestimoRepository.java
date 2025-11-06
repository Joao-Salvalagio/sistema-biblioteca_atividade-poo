package com.biblioteca.repositories;

import com.biblioteca.models.EmprestimoModel;
import com.biblioteca.models.EmprestimoModel.StatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<EmprestimoModel, Long> {
    List<EmprestimoModel> findByUsuarioId(Long usuarioId);
    List<EmprestimoModel> findByUsuarioIdAndStatus(Long usuarioId, StatusEmprestimo status);
    List<EmprestimoModel> findByStatus(StatusEmprestimo status);
}