# Spec: Dados Profissionais

## Introduction
Esta feature adiciona a capacidade de registrar informações profissionais de uma pessoa, incluindo nome da empresa, tipo de contrato (CLT ou PJ) e data de início do vínculo empregatício. A feature atende à necessidade de enriquecer o cadastro de pessoas com dados de contexto profissional, mantendo a simplicidade ao permitir apenas um vínculo profissional ativo por pessoa. Dados profissionais são opcionais e podem ser gerenciados através da API existente de pessoas, seguindo os padrões já estabelecidos no códigobase para entidades relacionadas.

## Users & Context
Clientes da API que consomem os endpoints de gerenciamento de pessoas para sistemas de RH, gestão de talentos ou cadastro de colaboradores. Estes usuários precisam registrar e atualizar informações profissionais de pessoas de forma opcional, sem a complexidade de gerenciar múltiplos vínculos simultâneos ou histórico de empregos.

## User Stories

### Story 1: Cadastro de dados profissionais ao criar pessoa
**Como** cliente da API,  
**quero** opcionalmente informar dados profissionais ao criar uma pessoa,  
**para** registrar o contexto profissional desde o início do cadastro.

### Story 2: Edição de dados profissionais
**Como** cliente da API,  
**quero** atualizar os dados profissionais de uma pessoa existente,  
**para** manter as informações atualizadas quando há mudanças no vínculo empregatício.

### Story 3: Remoção de dados profissionais
**Como** cliente da API,  
**quero** remover os dados profissionais de uma pessoa,  
**para** limpar informações quando não são mais aplicáveis ou foram registradas incorretamente.

### Story 4: Consulta de dados profissionais
**Como** cliente da API,  
**quero** visualizar os dados profissionais ao consultar uma pessoa,  
**para** ter acesso completo ao cadastro em uma única requisição.

## Functional Requirements
1. O sistema deve permitir o cadastro opcional de dados profissionais (nome da empresa, tipo de contrato, data de início) durante a criação de uma pessoa.
2. O sistema deve validar que o tipo de contrato seja CLT ou PJ.
3. O sistema deve validar que a data de início do vínculo empregatício não seja futura.
4. O sistema deve permitir a atualização de dados profissionais através do endpoint PATCH /api/people/{id}.
5. O sistema deve garantir que uma pessoa tenha no máximo um vínculo profissional ativo (substituição ao atualizar).
6. O sistema deve permitir a remoção de dados profissionais, definindo-os como null.
7. O sistema deve retornar os dados profissionais automaticamente nas respostas de consulta de pessoa (PersonResponse).
8. O sistema deve persistir os dados profissionais em banco de dados com relacionamento one-to-one com Person.

## Non-Functional Requirements
- **Performance**: A adição de dados profissionais não deve impactar negativamente o tempo de resposta dos endpoints existentes de pessoas.
- **Consistência**: Dados profissionais devem seguir o padrão de entidades JPA já estabelecido no projeto (Builder pattern, Serializable, equals/hashCode baseados em ID).
- **Manutenibilidade**: A implementação deve seguir os padrões existentes de separação de camadas (Controller, DTO, Use Case, Validator, Mapper, Repository).

## Acceptance Criteria

### Story 1: Cadastro de dados profissionais ao criar pessoa
- [ ] É possível criar uma pessoa com dados profissionais informados (empresa, tipo de contrato, data de início).
- [ ] É possível criar uma pessoa sem dados profissionais informados (campo opcional).
- [ ] O sistema rejeita criação quando o tipo de contrato não é CLT ou PJ.
- [ ] O sistema rejeita criação quando a data de início do vínculo é futura.
- [ ] Após criação com dados profissionais, os dados são persistidos corretamente no banco de dados.

### Story 2: Edição de dados profissionais
- [ ] É possível atualizar dados profissionais de uma pessoa através de PATCH /api/people/{id}.
- [ ] Ao atualizar dados profissionais, os dados anteriores são substituídos (mantém-se apenas um vínculo ativo).
- [ ] É possível atualizar parcialmente os dados profissionais (apenas alguns campos).
- [ ] O sistema rejeita atualização quando o tipo de contrato não é CLT ou PJ.
- [ ] O sistema rejeita atualização quando a data de início do vínculo é futura.

### Story 3: Remoção de dados profissionais
- [ ] É possível remover dados profissionais através de PATCH /api/people/{id} enviando campos nulos.
- [ ] Após remoção, a pessoa passa a não ter dados profissionais associados.
- [ ] A remoção de dados profissionais não afeta outros dados da pessoa (nome, endereços, etc.).

### Story 4: Consulta de dados profissionais
- [ ] Ao consultar uma pessoa com dados profissionais (GET /api/people/{id}), os dados profissionais são retornados na resposta.
- [ ] Ao consultar uma pessoa sem dados profissionais, o campo de dados profissionais retorna como null ou vazio.
- [ ] Ao listar pessoas (GET /api/people), cada pessoa inclui seus dados profissionais se existirem.
- [ ] A estrutura de dados profissionais na resposta segue o padrão DTO do projeto.

## Out of Scope
- Histórico de vínculos profissionais (apenas um vínculo ativo é suportado).
- Múltiplos vínculos profissionais simultâneos para a mesma pessoa.
- Validação adicional de dados da empresa (CNPJ, existência legal, etc.).
- Cálculos baseados em dados profissionais (tempo de casa, data prevista de término, etc.).
- Endpoint dedicado exclusivamente para gerenciamento de dados profissionais (usa o endpoint de pessoa existente).
- Busca ou filtragem por dados profissionais.

## Open Questions
Nenhuma. As decisões sobre obrigatoriedade, endpoint de edição, exposição na API e remoção foram resolvidas durante a fase de pesquisa.

## Implementation Notes (Code Review Findings)

### Issues Identified During Implementation Review

**Issue 1: Validador não valida explicitamente valor do enum ContractType**
- **Status:** ✅ Corrigido
- **Descrição:** O `ProfessionalDataBusinessRuleValidator` apenas verificava se `contractType` não era null, mas não validava explicitamente se o valor era CLT ou PJ.
- **Requisito afetado:** FR #2 "O sistema deve validar que o tipo de contrato seja CLT ou PJ"
- **Correção aplicada:** Adicionada validação explícita no método `validateContractType()` para verificar se o valor é CLT ou PJ. Adicionada mensagem de erro `CONTRACT_TYPE_INVALID`.
- **Data da correção:** 2026-06-06

**Issue 2: ProfessionalDataRequest impede updates parciais**
- **Status:** ✅ Corrigido
- **Descrição:** `ProfessionalDataRequest` era um record com campos obrigatórios, impedindo atualizações parciais.
- **Requisito afetado:** Story 2, AC #3 "É possível atualizar parcialmente os dados profissionais"
- **Correção aplicada:** Criado novo DTO `UpdateProfessionalDataRequest` com campos opcionais para updates parciais. `ProfessionalDataRequest` mantido para criação. `EditPersonRequest` atualizado para usar `UpdateProfessionalDataRequest`. `EditPerson` use case atualizado com método `mergeProfessionalData()` para fazer merge de campos.
- **Data da correção:** 2026-06-06

### Deviations from Original Plan

- **Task 10:** A limitação de DTOs records impedindo distinção entre "não enviado" e "enviado como null" foi documentada, mas isso também afeta a capacidade de fazer updates parciais, o que não estava claro no plano original.
