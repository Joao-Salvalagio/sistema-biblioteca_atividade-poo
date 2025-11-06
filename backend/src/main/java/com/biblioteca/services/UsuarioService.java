package com.biblioteca.services;

import com.biblioteca.models.UsuarioModel;
import com.biblioteca.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public UsuarioModel criarUsuario(UsuarioModel usuario) {
        Optional<UsuarioModel> usuarioExistente = usuarioRepository.findByDocumento(usuario.getDocumento());
        if (usuarioExistente.isPresent()) {
            throw new RuntimeException("Documento já cadastrado!");
        }

        return usuarioRepository.save(usuario);
    }

    public List<UsuarioModel> listarTodos() {
        return usuarioRepository.findAll();
    }

    public UsuarioModel buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
    }

    public UsuarioModel buscarPorDocumento(String documento) {
        return usuarioRepository.findByDocumento(documento).orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
    }

    public UsuarioModel atualizarUsuario(Long id, UsuarioModel usuarioAtualizado) {
        UsuarioModel usuario = buscarPorId(id);

        usuario.setNome(usuarioAtualizado.getNome());
        usuario.setTelefone(usuarioAtualizado.getTelefone());
        usuario.setEmail(usuarioAtualizado.getEmail());
        usuario.setLimiteEmprestimos(usuarioAtualizado.getLimiteEmprestimos());

        return usuarioRepository.save(usuario);
    }

    public void deletarUsuario(Long id) {
        UsuarioModel usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }
}