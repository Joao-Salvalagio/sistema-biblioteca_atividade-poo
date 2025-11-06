const API_URL = 'http://localhost:8080/api';

function mostrarMensagem(texto) {
    const mensagem = document.getElementById('mensagem');
    mensagem.textContent = texto;
    mensagem.style.display = 'block';
    setTimeout(() => mensagem.style.display = 'none', 3000);
}

function limparCampos(ids) {
    ids.forEach(id => {
        const elemento = document.getElementById(id);
        if (elemento) elemento.value = '';
    });
}

const tipoItemSelect = document.getElementById('tipoItem');
if (tipoItemSelect) {
    tipoItemSelect.addEventListener('change', function() {
        const tipo = this.value;
        document.getElementById('camposLivro').style.display = tipo === 'LIVRO' ? 'block' : 'none';
        document.getElementById('camposPeriodico').style.display = tipo === 'PERIODICO' ? 'block' : 'none';
    });
}

async function criarUsuario() {
    const nome = document.getElementById('nomeUsuario').value;
    const documento = document.getElementById('documento').value;
    const telefone = document.getElementById('telefone').value;
    const email = document.getElementById('email').value;
    const limiteEmprestimos = document.getElementById('limiteEmprestimos').value;
    
    if (!nome || !documento) {
        mostrarMensagem('Preencha nome e documento!');
        return;
    }
    
    const usuario = { nome, documento, telefone, email, limiteEmprestimos: parseInt(limiteEmprestimos) };

    const response = await fetch(`${API_URL}/usuarios`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(usuario)
    });

    if (response.ok) {
        mostrarMensagem('Usuário criado!');
        limparCampos(['nomeUsuario', 'documento', 'telefone', 'email']);
        listarUsuarios();
    } else {
        const erro = await response.text();
        mostrarMensagem('Erro: ' + erro);
    }
}

async function listarUsuarios() {
    try {
        const response = await fetch(`${API_URL}/usuarios`);
        const usuarios = await response.json();
        
        document.getElementById('listaUsuarios').innerHTML = usuarios.map(u => `
            <div class="item">
                <strong>ID:</strong> ${u.id} | 
                <strong>Nome:</strong> ${u.nome} | 
                <strong>Doc:</strong> ${u.documento} | 
                <strong>Limite:</strong> ${u.limiteEmprestimos} | 
                <strong>Multa Pendente:</strong> R$ ${u.multaPendente.toFixed(2)}
            </div>
        `).join('');
    } catch (error) {
        console.error('Erro ao listar usuários:', error);
    }
}

async function criarItem() {
    const tipo = document.getElementById('tipoItem').value;
    const titulo = document.getElementById('titulo').value;
    
    if (!tipo || !titulo) {
        mostrarMensagem('Preencha tipo e título!');
        return;
    }
    
    const item = { tipo, titulo, disponivel: true };
    
    if (tipo === 'LIVRO') {
        item.autor = document.getElementById('autor').value;
        item.categoria = document.getElementById('categoria').value;
    } else {
        item.editora = document.getElementById('editora').value;
        item.dataPublicacao = document.getElementById('dataPublicacao').value;
    }

    const response = await fetch(`${API_URL}/itens`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(item)
    });

    if (response.ok) {
        mostrarMensagem('Item criado!');
        limparCampos(['titulo', 'autor', 'categoria', 'editora', 'dataPublicacao']);
        document.getElementById('tipoItem').value = '';
        document.getElementById('camposLivro').style.display = 'none';
        document.getElementById('camposPeriodico').style.display = 'none';
        listarItens();
    } else {
        mostrarMensagem('Erro ao criar item');
    }
}

async function listarItens() {
    try {
        const response = await fetch(`${API_URL}/itens`);
        const itens = await response.json();
        exibirItens(itens);
    } catch (error) {
        console.error('Erro ao listar itens:', error);
    }
}

async function listarPorTipo(tipo) {
    const response = await fetch(`${API_URL}/itens/tipo/${tipo}`);
    const itens = await response.json();
    exibirItens(itens);
}

async function listarDisponiveis() {
    const response = await fetch(`${API_URL}/itens/disponiveis`);
    const itens = await response.json();
    exibirItens(itens);
}

function exibirItens(itens) {
    document.getElementById('listaItens').innerHTML = itens.map(i => `
        <div class="item ${i.disponivel ? 'disponivel' : 'indisponivel'}">
            <strong>ID:</strong> ${i.id} | 
            <strong>Tipo:</strong> ${i.tipo} | 
            <strong>Título:</strong> ${i.titulo} | 
            ${i.tipo === 'LIVRO' ? `<strong>Autor:</strong> ${i.autor || '-'}` : `<strong>Editora:</strong> ${i.editora || '-'}`} | 
            <strong>Disponível:</strong> ${i.disponivel ? 'Sim' : 'Não'}
        </div>
    `).join('');
}

async function realizarEmprestimo() {
    const usuarioId = document.getElementById('usuarioIdEmprestimo').value;
    const itemId = document.getElementById('itemIdEmprestimo').value;
    const diasEmprestimo = document.getElementById('diasEmprestimo').value;
    
    if (!usuarioId || !itemId || !diasEmprestimo) {
        mostrarMensagem('Preencha todos os campos!');
        return;
    }
    
    const dados = {
        usuarioId: parseInt(usuarioId),
        itemId: parseInt(itemId),
        diasEmprestimo: parseInt(diasEmprestimo)
    };

    try {
        const response = await fetch(`${API_URL}/emprestimos`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dados)
        });

        if (response.ok) {
            mostrarMensagem('Empréstimo realizado!');
            limparCampos(['usuarioIdEmprestimo', 'itemIdEmprestimo']);
            setTimeout(() => {
                listarEmprestimos();
                listarItens();
            }, 500);
        } else {
            const erro = await response.text();
            mostrarMensagem('Erro: ' + erro);
        }
    } catch (error) {
        console.error('Erro na requisição:', error);
        mostrarMensagem('Erro ao realizar empréstimo');
    }
}

async function listarEmprestimos() {
    try {
        const response = await fetch(`${API_URL}/emprestimos`);
        const emprestimos = await response.json();
        
        if (emprestimos && emprestimos.length > 0) {
            exibirEmprestimos(emprestimos);
        } else {
            document.getElementById('listaEmprestimos').innerHTML = '<div class="item">Nenhum empréstimo encontrado.</div>';
        }
    } catch (error) {
        console.error('Erro ao listar empréstimos:', error);
        document.getElementById('listaEmprestimos').innerHTML = '<div class="item">Erro ao carregar empréstimos.</div>';
    }
}

async function listarAtivos() {
    try {
        const response = await fetch(`${API_URL}/emprestimos/ativos`);
        const emprestimos = await response.json();
        exibirEmprestimos(emprestimos);
    } catch (error) {
        console.error('Erro ao listar ativos:', error);
    }
}

async function listarAtrasados() {
    try {
        const response = await fetch(`${API_URL}/emprestimos/atrasados`);
        const emprestimos = await response.json();
        exibirEmprestimos(emprestimos);
    } catch (error) {
        console.error('Erro ao listar atrasados:', error);
    }
}

function exibirEmprestimos(emprestimos) {
    try {
        const html = emprestimos.map(e => {
            const usuarioNome = e.usuario?.nome || 'Nome não disponível';
            const itemTitulo = e.item?.titulo || 'Título não disponível';
            const status = e.status || 'DESCONHECIDO';
            const dataPrevista = e.dataDevolucaoPrevista || 'N/A';
            const multa = e.multaAplicada || 0;
            
            return `
                <div class="item ${status === 'ATRASADO' ? 'atrasado' : ''}">
                    <strong>ID:</strong> ${e.id} | 
                    <strong>Usuário:</strong> ${usuarioNome} | 
                    <strong>Item:</strong> ${itemTitulo} | 
                    <strong>Status:</strong> ${status} | 
                    <strong>Devolução Prevista:</strong> ${dataPrevista} | 
                    <strong>Multa:</strong> R$ ${multa.toFixed(2)}
                </div>
            `;
        }).join('');
        
        document.getElementById('listaEmprestimos').innerHTML = html;
    } catch (error) {
        console.error('Erro ao exibir empréstimos:', error);
        document.getElementById('listaEmprestimos').innerHTML = '<div class="item">Erro ao exibir empréstimos.</div>';
    }
}

async function devolverItem() {
    const emprestimoId = document.getElementById('emprestimoIdDevolucao').value;
    
    if (!emprestimoId) {
        mostrarMensagem('Informe o ID do empréstimo!');
        return;
    }

    const response = await fetch(`${API_URL}/emprestimos/devolver/${emprestimoId}`, {
        method: 'PUT'
    });

    if (response.ok) {
        mostrarMensagem('Item devolvido!');
        limparCampos(['emprestimoIdDevolucao']);
        listarEmprestimos();
        listarItens();
        listarUsuarios();
    } else {
        const erro = await response.text();
        mostrarMensagem('Erro: ' + erro);
    }
}

async function verificarAtrasos() {
    const response = await fetch(`${API_URL}/emprestimos/verificar-atrasos`, {
        method: 'POST'
    });

    if (response.ok) {
        mostrarMensagem('Atrasos verificados!');
        listarEmprestimos();
    }
}

async function pagarMulta() {
    const usuarioId = document.getElementById('usuarioIdMulta').value;
    const valorPagamento = document.getElementById('valorMulta').value;
    
    if (!usuarioId || !valorPagamento) {
        mostrarMensagem('Preencha todos os campos!');
        return;
    }
    
    const dados = {
        usuarioId: parseInt(usuarioId),
        valorPagamento: parseFloat(valorPagamento)
    };

    const response = await fetch(`${API_URL}/emprestimos/pagar-multa`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(dados)
    });

    if (response.ok) {
        mostrarMensagem('Multa paga!');
        limparCampos(['usuarioIdMulta', 'valorMulta']);
        listarUsuarios();
    } else {
        const erro = await response.text();
        mostrarMensagem('Erro: ' + erro);
    }
}