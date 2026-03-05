# Banco Hexa - Core Bancário e Sistema de Transações

Um sistema financeiro transacional construído para consolidar os fundamentos da engenharia de software no backend, incluindo modelagem de dados relacional, arquitetura de APIs RESTful, persistência de dados e testes unitários.

O objetivo deste projeto não é utilizar os frameworks mais pesados do mercado logo de cara, mas sim demonstrar domínio sobre as camadas inferiores da web e do banco de dados antes da abstração.



## Arquitetura do Projeto

Em vez de delegar a infraestrutura para o Spring Boot e o Hibernate/JPA, este projeto foi construído utilizando **SparkJava** e **JDBC puro**. 

Essa decisão técnica foi tomada propositalmente para:
* Controlar explicitamente o ciclo de vida das requisições e respostas HTTP.
* Escrever e otimizar queries SQL (`INSERT`, `SELECT`, `JOINS`) manualmente, garantindo performance e entendimento real do banco de dados.
* Implementar o padrão **Data Access Object (DAO)** e **Data Transfer Object (DTO)** na unha, isolando o domínio da aplicação da camada de rede.

## Funcionalidades e Regras de Negócio

* **Motor de Transferências:** Validação rigorosa de saldo e existência de contas antes de autorizar a movimentação financeira.
* **Ledger (Livro-Razão) Imutável:** Nenhuma transferência é apenas uma atualização de saldo. O banco de dados PostgreSQL mantém um histórico permanente e relacional de toda movimentação (Conta Origem -> Conta Destino).
* **Consumo Dinâmico via SPA:** Front-end totalmente desacoplado consumindo a API via requisições assíncronas, utilizando renderização condicional para simular um painel de administração robusto.

## Stack Tecnológica

**Backend (Motor e API)**
* **Java 17:** Lógica de domínio e programação orientada a objetos.
* **SparkJava:** Micro-framework para roteamento e exposição dos endpoints REST.
* **PostgreSQL & JDBC:** Banco de dados relacional maduro, manipulado através de drivers nativos sem ORM.

**Frontend (Interface Administrativa)**
* **React (TypeScript) & Vite:** Componentização, tipagem estática rigorosa para os contratos da API e alta performance de build.
* **Tailwind CSS v4:** Estilização utility-first construindo um painel Dark Mode moderno e responsivo sem arquivos CSS inflados.

## Dependências Principais

**Backend (Maven - `pom.xml`)**
* `spark-core` (Comunicação HTTP e Roteamento)
* `postgresql` (Driver JDBC para comunicação com o banco)
* `jackson-databind` (Serialização e Desserialização de JSON)
* *Nota: O gerenciamento de dependências e o build são feitos via Maven.*

**Frontend (`package.json`)**
* `react` e `react-dom` (Renderização de UI)
* `@tailwindcss/vite` e `tailwindcss` (Motor de estilização v4)
* *Nota: Inicializado via Vite.*

## Como Executar o Projeto

**1. Banco de Dados (PostgreSQL)**
Crie um banco chamado `SistemaBancario` e execute os scripts de criação de tabela:
```sql
CREATE TABLE contas (
    numero VARCHAR(20) PRIMARY KEY,
    agencia VARCHAR(10) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    nome_cliente VARCHAR(100) NOT NULL,
    cpf_cliente VARCHAR(14) NOT NULL,
    saldo NUMERIC(15, 2) NOT NULL DEFAULT 0.0,
    parametro_especifico NUMERIC(15, 4) NOT NULL
);

CREATE TABLE transacoes (
    id SERIAL PRIMARY KEY,
    conta_origem VARCHAR(20) NOT NULL,
    conta_destino VARCHAR(20) NOT NULL,
    valor DECIMAL(15, 2) NOT NULL,
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_conta_origem FOREIGN KEY (conta_origem) REFERENCES contas(numero),
    CONSTRAINT fk_conta_destino FOREIGN KEY (conta_destino) REFERENCES contas(numero)
);
```
**2. Iniciando a API (Backend)**
* Importe o projeto como uma aplicação **Maven** na sua IDE (IntelliJ, Eclipse ou VS Code).
* Aguarde o Maven fazer o download das dependências listadas no `pom.xml`.
* Execute o método `main` da classe `ApiServer.java`. O servidor iniciará na porta `8080`.

**3. Iniciando o Painel (Frontend)**
* No terminal, navegue até a pasta frontend e rode:

```bash
npm install
npm run dev
```
Acesse http://localhost:5173 no seu navegador.
