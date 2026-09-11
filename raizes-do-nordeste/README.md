ALUNO: DANIEL SIQUEIRA VILHENA

RU: 5015472

\# Rede Raízes do Nordeste — API REST



Projeto desenvolvido para o \*\*Projeto Multidisciplinar\*\*, com foco na digitalização da Rede Raízes do Nordeste, integrando pedidos, produtos, estoque por unidade, pagamento simulado, fidelização, autenticação, segurança, LGPD e auditoria.



\## 1. Objetivo



Disponibilizar uma API REST para apoiar a operação da Rede Raízes do Nordeste, permitindo o gerenciamento de produtos, unidades, estoque, pedidos, pagamentos simulados e programa de fidelidade.



A aplicação contempla autenticação por JWT, controle de acesso por perfil, validação de dados, controle de estoque por unidade, múltiplos canais de pedido, registro de auditoria e consentimento para utilização do programa de fidelidade.



\## 2. Tecnologias



\- Java 21

\- Spring Boot 4.1.1

\- Spring Web MVC

\- Spring Data JPA

\- Hibernate

\- Spring Security

\- JWT (JSON Web Token)

\- BCrypt

\- PostgreSQL

\- Flyway

\- Bean Validation

\- Swagger / OpenAPI

\- Maven

\- JUnit / Spring Boot Test

\- Mockito

\- Postman para testes da API



\## 3. Arquitetura



O projeto utiliza uma organização em camadas:



```text

src/main/java/br/com/raizesdonordeste

├── api

│   ├── controller

│   ├── dto

│   └── exception

├── application

│   └── service

├── domain

│   ├── entity

│   ├── enums

│   └── repository

└── infrastructure

\&#x20;   └── security

Principais responsabilidades

API: exposição dos endpoints REST, DTOs e tratamento de exceções.

Application: implementação das regras de negócio e serviços da aplicação.

Domain: entidades, enums e repositórios.

Infrastructure: autenticação JWT, filtros de segurança, auditoria e configurações de infraestrutura.

4\\. Pré-requisitos



Antes de executar o projeto, instale:



JDK 21

PostgreSQL

Maven ou utilize o Maven Wrapper incluído no projeto

Eclipse IDE ou outra IDE compatível com projetos Maven

Postman ou Insomnia para execução dos testes manuais da API

5\\. Banco de dados



Crie um banco PostgreSQL chamado:



raizes\\\_nordeste



A aplicação utiliza PostgreSQL para persistência dos dados.



O controle e versionamento das alterações do banco de dados são realizados pelo Flyway, por meio dos arquivos de migração localizados em:



src/main/resources/db/migration



As migrações são executadas automaticamente durante a inicialização da aplicação.



O projeto utiliza:



spring.jpa.hibernate.ddl-auto=validate



Dessa forma, o Hibernate apenas valida a estrutura das entidades em relação ao banco, enquanto o Flyway é responsável pelas alterações versionadas do esquema.



6\\. Configuração



A configuração principal está localizada em:



src/main/resources/application.properties



A aplicação utiliza variáveis de ambiente para informações sensíveis.



Exemplo:



spring.application.name=raizes-do-nordeste



spring.datasource.url=jdbc:postgresql://localhost:5432/raizes\\\_nordeste

spring.datasource.username=postgres

spring.datasource.password=${DB\\\_PASSWORD}



server.port=8081



spring.jpa.hibernate.ddl-auto=validate

spring.jpa.show-sql=true

spring.jpa.properties.hibernate.format\\\_sql=true



spring.flyway.enabled=true

spring.flyway.url=jdbc:postgresql://localhost:5432/raizes\\\_nordeste

spring.flyway.user=postgres

spring.flyway.password=${DB\\\_PASSWORD}

spring.flyway.locations=classpath:db/migration



Não publique senhas, chaves JWT, tokens ou outras credenciais no repositório.
As variáveis DB\_PASSWORD e JWT\_SECRET devem ser configuradas no ambiente de execução.



Recomenda-se utilizar um arquivo .env.example ou configuração equivalente contendo apenas os nomes das variáveis necessárias, sem valores reais de credenciais.



7\\. Migrações do banco



As migrações do banco ficam em:



src/main/resources/db/migration



Entre as alterações versionadas estão:



criação/estrutura inicial utilizada pelo projeto;

associação do estoque às unidades;

associação dos pedidos às unidades;

inclusão do consentimento LGPD na tabela de fidelidade.



O Flyway mantém o controle da versão do esquema do banco de dados.



8\\. Executando no Eclipse

Importe o projeto como projeto Maven.

Aguarde o download das dependências.

Confirme que o PostgreSQL está em execução.

Crie o banco raizes\\\_nordeste.

Configure a variável DB\\\_PASSWORD.

Execute a classe:

RaizesDoNordesteApplication.java

A aplicação será disponibilizada em:

http://localhost:8081

9\\. Swagger / OpenAPI



Após iniciar a aplicação, acesse:



http://localhost:8081/swagger-ui/index.html



A documentação permite visualizar os endpoints disponíveis e realizar testes diretamente pela interface do Swagger.



A API utiliza autenticação Bearer Token para as rotas protegidas.



10\\. Autenticação



O login é realizado por:



POST /auth/login



Exemplo:



{

\&#x20; "email": "admin@raizes.com",

\&#x20; "senha": "SUA\\\_SENHA"

}



Após o login, o token JWT retornado deve ser utilizado como:



Authorization: Bearer SEU\\\_TOKEN



nas rotas protegidas.



Perfis



O projeto possui controle de acesso por perfil.



ADMIN: acesso administrativo, incluindo recursos de usuários, estoque e auditoria conforme as permissões configuradas.

OPERADOR: acesso aos recursos operacionais permitidos pela configuração de segurança.

11\\. Principais endpoints

Autenticação

POST /auth/login

Usuários

GET /usuarios

Produtos

GET  /produtos

GET  /produtos/{id}

POST /produtos

Unidades

GET  /unidades

GET  /unidades/{id}

POST /unidades

Estoque

GET  /estoque

GET  /estoque/{id}

POST /estoque

PUT  /estoque/{id}



O estoque é associado simultaneamente ao produto e à unidade.



Essa associação permite controlar quantidades de produtos de forma independente para cada unidade da rede.



Pedidos

GET  /pedidos

GET  /pedidos/{id}

POST /pedidos

PUT  /pedidos/{id}/status



A criação de pedidos exige:



cliente;

unidade;

canal do pedido;

forma de pagamento;

pelo menos um item.



O campo canalPedido utiliza os seguintes valores:



APP

TOTEM

BALCAO

PICKUP

WEB



Também é possível filtrar pedidos por canal:



GET /pedidos?canalPedido=TOTEM



O pedido possui status controlado por regras de transição.

O endpoint de atualização de status utiliza o formato:



PUT /pedidos/{id}/status?status=STATUS



Exemplo:



PUT /pedidos/24/status?status=ENTREGUE



As transições permitidas são:



AGUARDANDO\_PAGAMENTO → PAGAMENTO\_APROVADO

PAGAMENTO\_APROVADO → EM\_PREPARACAO

EM\_PREPARACAO → PRONTO

PRONTO → ENTREGUE

qualquer estado operacional válido → CANCELADO, exceto ENTREGUE e CANCELADO



Transições inválidas retornam HTTP 409 Conflict.



Pagamentos

POST /pagamentos/{pedidoId}?resultado=APROVADO

POST /pagamentos/{pedidoId}?resultado=RECUSADO



O pagamento é um mock acadêmico, utilizado para representar a integração com um gateway externo.



Nenhuma transação financeira real é realizada pela aplicação.



Fidelidade

GET    /fidelidade/cliente/{clienteId}

PUT    /fidelidade/cliente/{clienteId}/consentimento

POST   /fidelidade/cliente/{clienteId}/pontos

POST   /fidelidade/cliente/{clienteId}/resgate

GET    /fidelidade/cliente/{clienteId}/historico

POST   /fidelidade/cliente/{clienteId}/historico

DELETE /fidelidade/historico/{id}



O programa de fidelidade permite:



consultar saldo;

registrar geração de pontos;

consultar histórico;

realizar resgate;

registrar consentimento LGPD;

controlar operações relacionadas à fidelidade.



O resgate de pontos exige consentimento LGPD e saldo suficiente.



Auditoria

GET /auditoria



O acesso à auditoria é restrito ao perfil administrativo.



A auditoria registra informações como usuário, método HTTP, rota, status da operação e data/hora.



12\\. Fluxo crítico



O principal fluxo implementado é:



Pedido

\&#x20; ↓

Validação dos dados

\&#x20; ↓

Validação da unidade

\&#x20; ↓

Validação do produto

\&#x20; ↓

Verificação do estoque da unidade

\&#x20; ↓

Pedido criado

\&#x20; ↓

AGUARDANDO\\\_PAGAMENTO

\&#x20; ↓

Pagamento MOCK

\&#x20; ├── APROVADO

\&#x20; │      ↓

\&#x20; │  PAGAMENTO\\\_APROVADO

\&#x20; │      ↓

\&#x20; │  Geração de pontos

\&#x20; │

\&#x20; └── RECUSADO

\&#x20;        ↓

\&#x20;     CANCELADO



Após a aprovação do pagamento, o pedido pode avançar pelas etapas operacionais:



PAGAMENTO\\\_APROVADO

\&#x20;       ↓

EM\\\_PREPARACAO

\&#x20;       ↓

PRONTO

\&#x20;       ↓

ENTREGUE

13\\. Status de pedido



Os principais status utilizados são:



AGUARDANDO\\\_PAGAMENTO

PAGAMENTO\\\_APROVADO

EM\\\_PREPARACAO

PRONTO

ENTREGUE

CANCELADO



As transições são controladas pela regra de negócio da aplicação.
A idempotência completa das operações críticas é considerada uma evolução para ambiente produtivo.



Um pedido entregue não pode voltar para estados anteriores.



Pedidos cancelados não podem voltar para o fluxo normal de preparação.



14\\. Regras de estoque



O estoque é controlado por combinação de:



Unidade + Produto



Antes da criação do pedido, a aplicação verifica:



se a unidade existe e está ativa;

se o produto existe;

se existe estoque cadastrado para aquela unidade e produto;

se a quantidade disponível é suficiente.



Quando o estoque é insuficiente, a API retorna:



HTTP 409 Conflict



Essa regra evita a criação de pedidos com quantidade superior ao estoque disponível.



15\\. Fidelização e LGPD



O programa de fidelidade mantém o saldo de pontos por cliente e registra as movimentações realizadas.



Os pontos são gerados após um pagamento MOCK aprovado, conforme a regra implementada no serviço de pagamento.



O sistema mantém histórico das movimentações de pontos.



O programa também possui o campo:



consentimentoLgpd



O resgate de pontos depende do consentimento LGPD e da existência de saldo suficiente.



A solução busca aplicar o princípio de minimização de dados e evitar exposição desnecessária de informações pessoais.



16\\. Pagamento MOCK



O pagamento simulado representa uma integração com um serviço externo de pagamentos.



O endpoint permite informar dois resultados:



APROVADO

RECUSADO

Pagamento aprovado



Quando o resultado é APROVADO:



o pagamento é persistido;

o pedido recebe o status PAGAMENTO\\\_APROVADO;

são gerados pontos de fidelidade;

a movimentação de pontos é registrada no histórico.

Pagamento recusado



Quando o resultado é RECUSADO:



a tentativa de pagamento é registrada;

o pedido recebe o status CANCELADO;

não são gerados pontos de fidelidade.



O pagamento é somente uma simulação acadêmica e não processa valores financeiros reais.



17\\. Segurança



Foram implementadas medidas de segurança para proteção da API, incluindo:



autenticação com JWT;

autorização por perfil;

senhas protegidas com BCrypt;

autenticação stateless;

proteção dos endpoints conforme as permissões configuradas;

respostas HTTP específicas para falhas de autenticação e autorização;

senha de usuário não exposta nas respostas;

registro de auditoria;

utilização de variável de ambiente para a senha do banco.



Os principais códigos de segurança utilizados são:



401 Unauthorized



quando o usuário não está autenticado ou apresenta token inválido.



403 Forbidden



quando o usuário está autenticado, mas não possui permissão para acessar determinado recurso.



18\\. Tratamento de erros



A API utiliza códigos HTTP coerentes com os problemas encontrados.



Principais exemplos:



200 OK



Operação realizada com sucesso.



201 Created



Recurso criado com sucesso.



204 No Content



Exclusão realizada com sucesso quando aplicável.



400 Bad Request



Dados inválidos, campos obrigatórios ausentes ou parâmetros incorretos.



401 Unauthorized



Token ausente ou inválido.



403 Forbidden



Usuário autenticado sem permissão.



404 Not Found



Recurso inexistente.



409 Conflict



Conflito de regra de negócio, como estoque insuficiente.



Exemplo:



{

\&#x20; "error": "BAD\\\_REQUEST",

\&#x20; "message": "A quantidade de pontos deve ser maior que zero."

}

19\\. Testes realizados



Foram considerados cenários positivos e negativos para validar os principais requisitos da aplicação.



Cenário	Resultado esperado

Login válido	200

Requisição sem token	401

Usuário sem permissão	403

Campo obrigatório ausente	400

canalPedido inválido	400

Produto inexistente	404

Unidade inexistente	404

Estoque insuficiente	409

Pedido válido	201

Pagamento MOCK aprovado	Pedido PAGAMENTO\\\_APROVADO

Pagamento MOCK recusado	Pedido CANCELADO

Consulta de fidelidade	Saldo persistido

Consentimento LGPD	Consentimento persistido

Resgate de pontos	Saldo atualizado

Pontos inválidos	400

Filtro por canal	Pedidos filtrados

Consulta de auditoria	Registro persistido

20\\. Postman / Insomnia



A coleção de testes da API está disponibilizada no projeto em formato JSON, no diretório:



postman/Raizes-do-Nordeste.postman\_collection.json



A coleção disponibilizada no projeto contempla:



autenticação;

utilização do token JWT;

operações de pedidos;

estoque;

pagamentos MOCK;

fidelidade;

cenários positivos;

cenários negativos;

respostas de erro.



Um fluxo recomendado para demonstração é:



Login

\&#x20; ↓

Obter JWT

\&#x20; ↓

Criar pedido

\&#x20; ↓

Executar pagamento MOCK

\&#x20; ↓

Verificar status do pedido

\&#x20; ↓

Consultar fidelidade

\&#x20; ↓

Consultar histórico

21\\. Auditoria



A aplicação possui mecanismo de auditoria para registrar operações realizadas na API.



Entre as informações registradas estão:



usuário;

método HTTP;

rota;

status HTTP;

data e hora.



O endpoint administrativo é:



GET /auditoria



Esses registros auxiliam na rastreabilidade das operações realizadas pelo sistema.



22\\. Estrutura de dados principal



As principais entidades implementadas são:



Usuario

Unidade

Produto

Estoque

Pedido

ItemPedido

Pagamento

Fidelidade

HistoricoFidelidade

Auditoria



As entidades representam as principais necessidades do domínio da Rede Raízes do Nordeste.



23\\. Banco e migrações



As alterações estruturais do banco são controladas pelo Flyway.



Os arquivos de migração ficam em:



src/main/resources/db/migration



O projeto utiliza versionamento das alterações para reduzir dependência de alterações automáticas do Hibernate.



24\\. Documentação acadêmica



Além do código-fonte, a entrega deve conter a documentação do Projeto Multidisciplinar, contemplando:



introdução e objetivos;

análise e requisitos;

diagramas;

arquitetura;

modelagem de dados;

API e endpoints;

segurança e LGPD;

fidelização;

pagamento MOCK;

estoque e multicanal;

fluxo crítico;

plano de testes;

documentação técnica;

conclusão;

referências.

25\\. Repositório



O código-fonte deve ser disponibilizado em um repositório público contendo:



código-fonte;

README.md;

.env.example;

arquivos de migração;

documentação;

coleção Postman/Insomnia;

demais arquivos necessários para execução.



26\\. Observações para produção



Este projeto possui finalidade acadêmica.



Para uma implantação em ambiente produtivo, recomenda-se complementar a solução com:



gerenciamento profissional de segredos;

rotação de chaves JWT;

configuração externa de parâmetros de segurança;

testes automatizados mais abrangentes;

controle de concorrência de estoque;

idempotência completa nas operações críticas;

observabilidade e monitoramento;

política formal de retenção e anonimização de dados;

integração com gateway de pagamento real;

mecanismos adicionais de alta disponibilidade;

implementação automatizada de promoções e campanhas.

27\\. Promoções e campanhas



Como evolução do sistema, está prevista a possibilidade de implementação da campanha:



Raízes da Semana



Regra proposta:



desconto de 10%;

segunda a sexta-feira;

das 11h às 14h;

somente para produtos participantes;

sem cumulatividade com outras promoções.



Essa regra é uma proposta de evolução e não representa uma funcionalidade automatizada do MVP atual.


