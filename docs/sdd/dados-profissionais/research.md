# Research: Dados Profissionais

## Feature Intent
Adicionar cadastro de dados profissionais a uma pessoa, contendo: nome da empresa, tipo de contrato (CLT ou PJ) e início do vínculo empregatício. Cada pessoa pode ter apenas um vínculo profissional ativo (substituição ao atualizar).

## Codebase Context

### Relevant Entry Points
- `PersonController.java` - REST controller para gerenciamento de pessoas (POST /api/people, PATCH /api/people/{id}, GET /api/people/{id})
- `AddressController.java` - exemplo de controller para gerenciamento de entidades relacionadas (endereços são one-to-many)

### Data Models
- `Person.java` - entidade principal com relacionamento @OneToMany para Address e @OneToOne para mainAddress
- `Address.java` - entidade de endereço com campos de logradouro e relacionamento @ManyToOne com Person
- Pattern: entidades usam Builder pattern, campos simples com getters/setters, e relacionamentos JPA configurados

### Existing Patterns
**Entities**: Localizadas em `domain/entity/`. Usam JPA annotations (@Entity, @Id, @GeneratedValue, @OneToMany, @ManyToOne, @OneToOne). Implementam Serializable, equals/hashCode baseados em ID, e toString. Possuem Builder pattern interno.

**Controllers**: Localizados em `controller/`. Usam @RestController, @RequestMapping, Swagger annotations (@Operation, @ApiResponse, @Parameter), injeção via @Inject, retornam ResponseEntity<?> com HATEOAS (EntityModel/CollectionModel). Validations usam @Valid, @NotNull, @NotBlank, @PastOrPresent.

**DTOs**: Localizados em `controller/dto/{entity}/{request|response}/`. São records com validation annotations do Jakarta Validation.

**Use Cases**: Localizados em `domain/usecase/{entity}/`. São componentes Spring (@Component) que encapsulam lógica de negócio. Recebem entidades ou IDs, realizam validações via BusinessRuleValidator, e persistem via Repository.

**Validators**: Localizados em `domain/validator/usecase/{entity}/`. Implementam lógica de validação de negócio, retornam ValidationResult com lista de erros, e usam exception BusinessRuleException.

**Mappers**: Localizados em `mapper/`. Usam MapStruct (@Mapper, @Component) com @Mapping annotations para ignorar campos específicos. Possuem @AfterMapping para lógica pós-mapeamento.

**Repositories**: Localizados em `domain/repository/`. Estendem JpaRepository<Entidade, ID> e podem ter métodos customizados ou @Query annotations.

**Tests**: Localizados em `test/java/`. Usam JUnit 5, Mockito (@ExtendWith(MockitoExtension.class), @Mock, @InjectMocks), e seguem padrão AAA (Arrange/Act/Assert). Testam comportamento, não implementação.

### Dependencies
- Spring Boot 3.2.5 (Web, Data JPA, Validation, HATEOAS, Actuator)
- MapStruct 1.5.5.Final para mapeamento
- Springdoc OpenAPI 2.5.0 para Swagger
- H2 Database (dev) e MySQL (prod)
- JUnit 5 + Mockito para testes
- Jakarta Inject para injeção de dependências

### Test Patterns
- Unit tests em `src/test/java` seguem estrutura do pacote principal
- Usam @ExtendWith(MockitoExtension.class)
- @Mock para dependências, @InjectMocks para classe em teste
- Builder pattern para criar fixtures de teste
- Asserts via JUnit 5 (assertEquals, assertThrows, assertTrue, etc.)
- verify() do Mockito para verificar interações quando necessário

## Constraints & Risks
- **Enum não utilizado atualmente**: O projeto não possui enums. A introdução de um enum para ContractType (CLT/PJ) será um novo padrão no códigobase.
- **Cardinalidade diferente de Address**: Endereços são one-to-many com lógica complexa (main address). Dados profissionais serão one-to-one.
- **Migração de banco**: Será necessário adicionar tabela/columns no banco de dados para suportar os novos campos.

## Decisões Tomadas
- **Obrigatoriedade**: Dados profissionais são OPCIONAIS ao criar uma pessoa
- **Endpoint de edição**: A edição deve ser feita via PATCH no endpoint geral de pessoa (/api/people/{id})
- **Exposição na API**: Dados profissionais devem ser retornados AUTOMATICAMENTE nas respostas de Person (PersonResponse)
- **Remoção**: Sim, deve ser possível remover dados profissionais (setar como null)

## Files Explored
- `src/main/java/com/github/thiagomarqs/gerenciamentopessoas/domain/entity/Person.java`
- `src/main/java/com/github/thiagomarqs/gerenciamentopessoas/domain/entity/Address.java`
- `src/main/java/com/github/thiagomarqs/gerenciamentopessoas/controller/PersonController.java`
- `src/main/java/com/github/thiagomarqs/gerenciamentopessoas/controller/AddressController.java`
- `src/main/java/com/github/thiagomarqs/gerenciamentopessoas/controller/dto/person/request/CreatePersonRequest.java`
- `src/main/java/com/github/thiagomarqs/gerenciamentopessoas/controller/dto/person/request/EditPersonRequest.java`
- `src/main/java/com/github/thiagomarqs/gerenciamentopessoas/controller/dto/person/response/PersonResponse.java`
- `src/main/java/com/github/thiagomarqs/gerenciamentopessoas/mapper/PersonMapper.java`
- `src/main/java/com/github/thiagomarqs/gerenciamentopessoas/domain/usecase/person/CreatePerson.java`
- `src/main/java/com/github/thiagomarqs/gerenciamentopessoas/domain/usecase/person/EditPerson.java`
- `src/main/java/com/github/thiagomarqs/gerenciamentopessoas/domain/usecase/person/FindPeople.java`
- `src/main/java/com/github/thiagomarqs/gerenciamentopessoas/domain/repository/PersonRepository.java`
- `src/main/java/com/github/thiagomarqs/gerenciamentopessoas/domain/validator/usecase/person/CreatePersonBusinessRuleValidator.java`
- `src/main/java/com/github/thiagomarqs/gerenciamentopessoas/domain/validator/ValidationResult.java`
- `src/main/java/com/github/thiagomarqs/gerenciamentopessoas/domain/exception/BusinessRuleException.java`
- `src/main/java/com/github/thiagomarqs/gerenciamentopessoas/domain/exception/messages/PersonBusinessRuleMessages.java`
- `src/main/java/com/github/thiagomarqs/gerenciamentopessoas/domain/exception/messages/EntityBusinessRuleMessages.java`
- `src/test/java/com/github/thiagomarqs/gerenciamentopessoas/domain/usecase/person/CreatePersonTest.java`
- `build.gradle`
