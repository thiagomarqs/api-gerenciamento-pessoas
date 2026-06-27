# Plano de Migração: Gradle para Maven

## Visão Geral
Este documento descreve o plano detalhado para migrar o projeto de gerenciamento de pessoas de Gradle para Maven. O projeto atualmente utiliza Gradle 8.7 com Java 21 e Spring Boot 3.2.5.

## Análise do Estado Atual

### Configuração Gradle Atual
- **Plugins**: `java`, `org.springframework.boot` (3.2.5), `io.spring.dependency-management` (1.1.4)
- **Java Version**: 21
- **Group**: com.github.thiagomarqs
- **Version**: 1.0.0
- **Repository**: mavenCentral()
- **Gradle Wrapper**: 8.14.4

### Dependências Principais
1. **Spring Boot Starters**:
   - spring-boot-starter-web (3.2.5)
   - spring-boot-starter-data-jpa (3.2.5)
   - spring-boot-starter-validation (3.2.5)
   - spring-boot-starter-hateoas (3.2.5)
   - spring-boot-starter-actuator (3.3.0)
   - spring-boot-starter-test (via BOM)

2. **Outras Dependências**:
   - jakarta.inject:jakarta.inject-api:2.0.1
   - mysql:mysql-connector-java:8.0.33
   - com.h2database:h2:2.2.224
   - com.google.code.gson:gson:2.10.1
   - org.mapstruct:mapstruct:1.5.5.Final
   - org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0
   - org.mockito:mockito-core (via BOM)

3. **Ferramentas de Desenvolvimento**:
   - spring-boot-devtools
   - mapstruct-processor:1.5.5.Final (annotationProcessor)

### Configurações Específicas
- **MapStruct**: Configuração específica do compilador com argumentos:
  - `-Amapstruct.suppressGeneratorTimestamp=true`
  - `-Amapstruct.suppressGeneratorVersionInfoComment=true`
  - `-Amapstruct.verbose=true`
  - `-Amapstruct.disableBuilders=true`
  - `-Amapstruct.defaultComponentModel=jsr330`

- **Testes**: Configuração JUnit Platform habilitada

### Arquivos Atuais Relacionados ao Build
- `build.gradle` - Configuração principal do build
- `settings.gradle` - Configuração do projeto
- `gradlew` / `gradlew.bat` - Scripts do Gradle wrapper
- `gradle/wrapper/` - Distribuição do Gradle wrapper
- `.gradle/` - Cache do Gradle (gerado)
- `build/` - Diretório de build do Gradle (gerado)

## Estratégia de Migração

### Fase 1: Preparação
1. **Backup do Estado Atual**
   - Commitar todas as mudanças pendentes
   - Criar branch específico para migração: `feature/maven-migration`
   - Documentar versão atual funcional

2. **Verificação de Pré-requisitos**
   - Maven 3.9+ instalado no ambiente
   - Java 21 disponível
   - Variáveis de ambiente configuradas

### Fase 2: Criação do pom.xml
1. **Estrutura Básica do POM**
   ```xml
   <project>
       <modelVersion>4.0.0</modelVersion>
       <groupId>com.github.thiagomarqs</groupId>
       <artifactId>gerenciamentopessoas</artifactId>
       <version>1.0.0</version>
       <packaging>jar</packaging>
       <parent>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-parent</artifactId>
           <version>3.2.5</version>
       </parent>
   </project>
   ```

2. **Configuração de Propriedades**
   - `java.version`: 21
   - `mapstruct.version`: 1.5.5.Final
   - `springdoc.version`: 2.5.0
   - `mysql.version`: 8.0.33
   - `h2.version`: 2.2.224
   - `gson.version`: 2.10.1
   - `mockito.version`: 5.23.0

3. **Dependências**
   - Converter todas as dependências do build.gradle para formato Maven
   - Configurar escopos corretos (compile, runtime, test, provided)
   - Mantener versões explícitas onde necessário

4. **Plugins Maven**
   - `maven-compiler-plugin`: Configurar Java 21 e opções do MapStruct
   - `spring-boot-maven-plugin`: Para empacotamento executável
   - `maven-surefire-plugin`: Para execução de testes JUnit 5

### Fase 3: Configuração do MapStruct
1. **Configuração do Compiler Plugin**
   ```xml
   <plugin>
       <groupId>org.apache.maven.plugins</groupId>
       <artifactId>maven-compiler-plugin</artifactId>
       <configuration>
           <source>21</source>
           <target>21</target>
           <compilerArgs>
               <arg>-Amapstruct.suppressGeneratorTimestamp=true</arg>
               <arg>-Amapstruct.suppressGeneratorVersionInfoComment=true</arg>
               <arg>-Amapstruct.verbose=true</arg>
               <arg>-Amapstruct.disableBuilders=true</arg>
               <arg>-Amapstruct.defaultComponentModel=jsr330</arg>
           </compilerArgs>
           <annotationProcessorPaths>
               <path>
                   <groupId>org.mapstruct</groupId>
                   <artifactId>mapstruct-processor</artifactId>
                   <version>${mapstruct.version}</version>
               </path>
           </annotationProcessorPaths>
       </configuration>
   </plugin>
   ```

### Fase 4: Atualização de Arquivos de Configuração
1. **Atualização do Dockerfile**
   - Mudar caminho do JAR: `./build/libs/gerenciamentopessoas-1.0.0.jar` → `./target/gerenciamentopessoas-1.0.0.jar`
   - Considerar renomear para `gerenciamentopessoas-1.0.0.jar` (Maven usa nome diferente)

2. **Atualização do .gitignore**
   - Adicionar `target/` (diretório de build do Maven)
   - Remover referências ao Gradle (`.gradle`, `build/`, `gradle/wrapper/`)
   - Adicionar `.mvn/`, `mvnw`, `mvnw.bat` (Maven wrapper)

3. **Atualização do README.md**
   - Substituir referências a Gradle por Maven
   - Atualizar comandos: `gradle bootRun` → `mvn spring-boot:run`
   - Atualizar comandos de teste: `./gradlew test` → `mvn test`
   - Atualizar seção de tecnologias utilizadas

### Fase 5: Limpeza do Gradle
1. **Remoção de Arquivos Gradle**
   - Remover `build.gradle`
   - Remover `settings.gradle`
   - Remover `gradlew` e `gradlew.bat`
   - Remover diretório `gradle/`
   - Limpar `.gradle/` (se existir localmente)

2. **Limpeza de Build Anterior**
   - Remover diretório `build/`
   - Executar `mvn clean` para garantir build limpo

### Fase 6: Configuração do Maven Wrapper
1. **Instalação do Maven Wrapper**
   - Executar `mvn wrapper:wrapper` para gerar scripts do wrapper
   - Commitar arquivos gerados (`.mvn/`, `mvnw`, `mvnw.bat`)

### Fase 7: Validação e Testes
1. **Build Completo**
   - Executar `mvn clean install`
   - Verificar se build compila sem erros
   - Verificar se todos os testes passam

2. **Validação de Funcionalidades**
   - Testar execução da aplicação: `mvn spring-boot:run`
   - Verificar endpoints da API
   - Validar integração com ViaCEP
   - Testar documentação Swagger

3. **Validação de Cobertura de Testes**
   - Executar `mvn test jacoco:report` (se configurar JaCoCo)
   - Verificar se cobertura mínima de 80% é mantida

### Fase 8: Atualização da Documentação
1. **AGENTS.md**
   - Atualizar comandos de build e teste
   - Atualizar referências a arquivos de configuração

2. **Documentação de CDK**
   - O projeto `cdk_project` já usa Maven, não requer mudanças
   - Verificar se há referências ao build da aplicação principal

### Fase 9: Preparação para Deploy
1. **Docker**
   - Testar build da imagem Docker com novo JAR
   - Validar que container inicia corretamente

2. **AWS CDK**
   - Verificar se referências ao JAR no stack CDK estão corretas
   - Testar deploy local se possível

## Estrutura do novo pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>

    <groupId>com.github.thiagomarqs</groupId>
    <artifactId>gerenciamentopessoas</artifactId>
    <version>1.0.0</version>
    <name>gerenciamentopessoas</name>
    <description>API de Gerenciamento de Pessoas</description>

    <properties>
        <java.version>21</java.version>
        <mapstruct.version>1.5.5.Final</mapstruct.version>
        <springdoc.version>2.5.0</springdoc.version>
        <mysql.version>8.0.33</mysql.version>
        <h2.version>2.2.224</h2.version>
        <gson.version>2.10.1</gson.version>
        <mockito.version>5.23.0</mockito.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-hateoas</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
            <version>3.3.0</version>
        </dependency>

        <!-- Other Dependencies -->
        <dependency>
            <groupId>jakarta.inject</groupId>
            <artifactId>jakarta.inject-api</artifactId>
            <version>2.0.1</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>${h2.version}</version>
        </dependency>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>${gson.version}</version>
        </dependency>
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
            <version>${mapstruct.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>${springdoc.version}</version>
        </dependency>

        <!-- Development Tools -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>

        <!-- Test Dependencies -->
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>${mockito.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                    <compilerArgs>
                        <arg>-Amapstruct.suppressGeneratorTimestamp=true</arg>
                        <arg>-Amapstruct.suppressGeneratorVersionInfoComment=true</arg>
                        <arg>-Amapstruct.verbose=true</arg>
                        <arg>-Amapstruct.disableBuilders=true</arg>
                        <arg>-Amapstruct.defaultComponentModel=jsr330</arg>
                    </compilerArgs>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.mapstruct</groupId>
                            <artifactId>mapstruct-processor</artifactId>
                            <version>${mapstruct.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

## Riscos e Mitigações

### Risco 1: Incompatibilidade de Versões
- **Descrição**: Algumas dependências podem ter versões diferentes quando gerenciadas pelo Spring Boot BOM
- **Mitigação**: Testar build completo e verificar se todas as funcionalidades funcionam corretamente

### Risco 2: Configuração do MapStruct
- **Descrição**: A configuração do annotation processor pode não funcionar como esperado
- **Mitigação**: Testar geração de mappers e validar que os mappers são gerados corretamente

### Risco 3: Docker
- **Descrição**: Caminho do JAR no Dockerfile pode estar incorreto
- **Mitigação**: Testar build da imagem Docker e execução do container

### Risco 4: AWS CDK
- **Descrição**: Stack CDK pode ter referências ao JAR que precisam ser atualizadas
- **Mitigação**: Revisar configuração do CDK e testar deploy se possível

## Cronograma Estimado

1. **Preparação**: 30 minutos
2. **Criação do pom.xml**: 1 hora
3. **Configuração do MapStruct**: 30 minutos
4. **Atualização de arquivos de configuração**: 1 hora
5. **Limpeza do Gradle**: 30 minutos
6. **Configuração do Maven Wrapper**: 15 minutos
7. **Validação e testes**: 2 horas
8. **Atualização da documentação**: 1 hora
9. **Preparação para deploy**: 1 hora

**Total estimado**: ~7.5 horas

## Checklist de Validação

- [ ] Branch de migração criado
- [ ] pom.xml criado e configurado
- [ ] Dependências convertidas corretamente
- [ ] MapStruct configurado corretamente
- [ ] Maven wrapper instalado
- [ ] Arquivos Gradle removidos
- [ ] Dockerfile atualizado
- [ ] .gitignore atualizado
- [ ] README.md atualizado
- [ ] AGENTS.md atualizado
- [ ] Build Maven completo funciona (`mvn clean install`)
- [ ] Testes unitários passam (`mvn test`)
- [ ] Aplicação inicia corretamente (`mvn spring-boot:run`)
- [ ] Endpoints da API funcionam
- [ ] Documentação Swagger acessível
- [ ] Imagem Docker constrói e executa
- [ ] Cobertura de testes mantida (≥80%)
- [ ] CDK ainda funciona corretamente

## Comandos Equivalentes

| Gradle | Maven |
|--------|-------|
| `./gradlew build` | `mvn clean install` |
| `./gradlew test` | `mvn test` |
| `./gradlew bootRun` | `mvn spring-boot:run` |
| `./gradlew clean` | `mvn clean` |
| `./gradlew bootJar` | `mvn package` (com spring-boot-maven-plugin) |
| `./gradlew dependencies` | `mvn dependency:tree` |

## Considerações Finais

- A migração deve ser feita em um branch separado para não afetar o branch `develop`
- Recomenda-se testar exaustivamente antes de mergear
- Considerar configurar CI/CD para usar Maven após a migração
- Documentar qualquer mudança no processo de deploy (Docker, AWS CDK)
- Comunicar mudança na ferramenta de build para a equipe (se aplicável)
