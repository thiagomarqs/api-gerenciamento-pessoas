# language: pt
Funcionalidade: Gerenciar pessoas

  Cenario: Criar pessoa com sucesso
    Quando é requisitada a criação de uma pessoa via API com o payload "create-person-success.json"
    Então a pessoa é criada com sucesso

  Cenario: Desativar pessoa e todos seus endereços com sucesso
    Dado que uma pessoa foi criada via API com o payload "create-person-success.json"
    Quando é requisitada a desativação de uma pessoa com o payload "deactivate-person-success.json"
    Então a pessoa e seus endereços são desativados

  Cenario: Não criar pessoa se falhar na validação da integração de endereços
    Dado que a integração de endereços responde com o payload "invalid-address-response.json"
    Quando é requisitada a criação de uma pessoa via API com o payload "create-person-fails-address-integration-validation.json"
    Então a pessoa não é criada