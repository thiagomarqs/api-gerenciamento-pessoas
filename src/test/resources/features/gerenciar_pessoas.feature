# language: pt
Funcionalidade: Gerenciar pessoas

  Cenario: Criar pessoa com sucesso
    Quando uma pessoa é criada via API com payload "create-person-success.json"
    Então a pessoa é criada com sucesso

  Cenario: Desativar pessoa com sucesso
    Dado que uma pessoa é criada via API com payload "create-person-success.json"
    Quando é requisitada a desativação de uma pessoa com payload "deactivate-person-success.json"
    Então a pessoa e seus endereços são desativados