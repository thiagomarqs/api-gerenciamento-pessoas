# language: pt
Funcionalidade: Gerenciar pessoas

  Cenario: Criar pessoa com sucesso
    Quando é requisitada a criação de uma pessoa
    Então a pessoa é criada com sucesso

  Cenario: Desativar pessoa com sucesso
    Dado que uma pessoa está com cadastro ativo
    Quando é requisitada a desativação de uma pessoa
    Então a pessoa e seus endereços são desativados