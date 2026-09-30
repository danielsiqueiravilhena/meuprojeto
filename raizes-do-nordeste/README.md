ALUNO: DANIEL SIQUEIRA VILHENA
RU: 5015472
Rede Raízes do Nordeste


Configuração do ambiente

Configure o PostgreSQL primeiro para iniciar
Na máquina onde o PostgreSQL está instalado:
Pressione Win + R
Digite:
services.msc
Procure por algo parecido com:
postgresql-x64-16
ou postgresql-x64-17, dependendo da versão.
Clique duas vezes nele.
Em Tipo de inicialização, selecione:
Automático
Clique em Aplicar.
Clique em Iniciar se ele estiver parado.
Clique em OK.

Antes de executar o projeto, configure as variáveis de ambiente no Eclipse.
Acesse:
Run Configurations
\- Environment
\- New

Crie as seguintes variáveis:
Name:
JWT\_SECRET
Value:
raizes-do-nordeste-chave-secreta-2026

Name:
DB\_PASSWORD
Value:
SUA\_SENHA\_DO\_POSTGRES
Substitua pelo valor da senha configurada no PostgreSQL/pgAdmin.


Usuários para teste

Após iniciar a aplicação, utilize os usuários abaixo para autenticação:
Administrador
Email:
admin@raizes.com
Senha:
123456
Com permissão de admin.

Operador
Email:
operador@raizes.com
Senha:
123456
Com permissão de operador.

Esse Projeto foi desenvolvido para o Projeto Multidisciplinar, com foco na digitalização da Rede Raízes do Nordeste.
Ele integra pedidos, produtos, estoque por unidade, pagamento simulado, fidelização, autenticação, segurança, LGPD e auditoria.


O Objetivo

Disponibilizar uma API REST para apoiar a operação da Rede Raízes do Nordeste, permitindo o gerenciamento de produtos, unidades, estoque, pedidos, pagamentos simulados e programa de fidelidade.
A aplicação contempla autenticação por JWT, controle de acesso por perfil, validação de dados, controle de estoque por unidade, múltiplos canais de pedido, registro de auditoria e consentimento para utilização do programa de fidelidade.


As Tecnologias

Java 21;
Spring Boot 4.1.1;
Hibernate;
JWT (JSON Web Token);
BCrypt;
PostgreSQL;
Flyway;
Bean Validation;
Swagger / OpenAPI;
Maven;
Postman para testes da API.


Os Pré-requisitos

Antes de executar o projeto, instale:
JDK 21;
PostgreSQL;
Eclipse IDE ou outra IDE compatível com projetos Maven;
Postman ou Insomnia para execução dos testes manuais da API.


O Banco de dados

Crie um banco PostgreSQL chamado:
raizes\_nordeste

A aplicação utiliza PostgreSQL para persistência dos dados.
O controle e versionamento das alterações do banco de dados são realizados pelo Flyway, por meio dos arquivos de migração localizados em:
src/main/resources/db/migration
As migrações são executadas automaticamente durante a inicialização da aplicação.
O projeto utiliza:
spring.jpa.hibernate.ddl-auto=validate
Dessa forma, o Hibernate apenas valida a estrutura das entidades em relação ao banco, enquanto o Flyway é responsável pelas alterações versionadas do esquema.


A Configuração

A configuração principal está localizada em:
src/main/resources/application.properties
A aplicação utiliza variáveis de ambiente para informações sensíveis.
Exemplo:
spring.application.name=raizes-do-nordeste
spring.datasource.url=jdbc:postgresql://localhost:5432/raizes\_nordeste
spring.datasource.username=postgres
spring.datasource.password=${DB\_PASSWORD}
server.port=8081
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format\_sql=true
spring.flyway.enabled=true
spring.flyway.url=jdbc:postgresql://localhost:5432/raizes\_nordeste
spring.flyway.user=postgres
spring.flyway.password=${DB\_PASSWORD}
spring.flyway.locations=classpath:db/migration
As variáveis DB\_PASSWORD e JWT\_SECRET devem ser configuradas no ambiente de execução como citado no começo do readme.


As Migrações do banco

As migrações do banco como citadas acima ficam em:
src/main/resources/db/migration
Entre as alterações versionadas estão:
criação/estrutura inicial utilizada pelo projeto;
associação do estoque às unidades;
associação dos pedidos às unidades;
inclusão do consentimento LGPD na tabela de fidelidade.
O Flyway mantém o controle da versão do esquema do banco de dados.


Executando no Eclipse

Importe o projeto como projeto Maven.
Aguarde o download das dependências.
Confirme que o PostgreSQL está em execução.
Crie o banco raizes\_nordeste.
Configure a variável DB\_PASSWORD.
Configure a variável JWT\_SECRET.


Execute a classe:

RaizesDoNordesteApplication.java
A aplicação será disponibilizada em:
http://localhost:8081


Swagger / OpenAPI

Após iniciar a aplicação, acesse:
http://localhost:8081/swagger-ui/index.html
A documentação permite visualizar os endpoints disponíveis e realizar testes diretamente pela interface do Swagger.
A API utiliza autenticação Bearer Token para as rotas protegidas.


A Autenticação

Para acessar endpoints protegidos:
Faça login usando um dos usuários acima.
POST /auth/login
Copie o token JWT retornado.
No Postman utilize:
Authorization
Bearer Token
Cole o token recebido nas rotas protegidas.
Exemplo:
{
"email": "admin@raizes.com",
"senha": "123456
}


Perfis

O projeto possui controle de acesso por perfil.
ADMIN: tem acesso administrativo, incluindo recursos de usuários, estoque e auditoria conforme as permissões configuradas.
OPERADOR: tem acesso aos recursos operacionais permitidos pela configuração de segurança.


Os Principais endpoints:

POST /auth/login - autenticação. 
GET /produtos - produtos. 
GET /unidades - unidades. 
GET /estoque - estoque. 
POST /pedidos - criação de pedido. 
GET /pedidos - consulta, com filtro canalPedido. 
PUT /pedidos/{id}/status - atualização de status. 
POST /pagamentos/{pedidoId}?resultado=APROVADO|RECUSADO - pagamento mock. 
GET /fidelidade/cliente/{clienteId} - saldo. 
PUT /fidelidade/cliente/{clienteId}/consentimento - consentimento LGPD. 
POST /fidelidade/cliente/{clienteId}/pontos - pontos. 
POST /fidelidade/cliente/{clienteId}/resgate - resgate. 
GET /fidelidade/cliente/{clienteId}/historico - histórico. 


O campo canalPedido utiliza os seguintes valores:

APP
TOTEM
BALCAO
PICKUP
WEB

Também é possível filtrar pedidos por canal:
GET /pedidos?canalPedido=TOTEM
O pedido possui status controlado por regras de transição.
Foi implementado um mecanismo de pagamento mock para representar a integração do sistema com um gateway de pagamento externo. O resultado do processamento pode ser informado como APROVADO ou RECUSADO, permitindo simular diferentes respostas.  O endpoint utilizado para o processamento é: POST /pagamentos/{pedidoId}?resultado=APROVADO ou: POST /pagamentos/{pedidoId}?resultado=RECUSADO.
Quando o pagamento é aprovado, o registro do pagamento é persistido no banco de dados, o pedido recebe o status PAGAMENTO\_APROVADO e são gerados pontos de fidelidade com base no valor do pedido. 
Quando o pagamento é recusado, o registro da tentativa de pagamento é persistido e o pedido recebe o status CANCELADO. Nesse cenário, não são concedidos pontos de fidelidade. O pagamento é um mock acadêmico, utilizado para representar a integração com um gateway externo.
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


A Auditoria

O acesso à auditoria é restrito ao perfil administrativo.
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


A Estrutura de dados principal

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


O Fluxo crítico

O principal fluxo implementado é:
Pedido
Validação dos dados
Validação da unidade
Validação do produto
Verificação do estoque da unidade
Pedido criado
AGUARDANDO\_PAGAMENTO
Pagamento MOCK

Se APROVADO:
PAGAMENTO\_APROVADO
Geração de pontos

Se RECUSADO:
CANCELADO

Após a aprovação do pagamento, o pedido pode avançar pelas etapas operacionais:
PAGAMENTO\_APROVADO
EM\_PREPARACAO
PRONTO
ENTREGUE


O Status de pedido

Os principais status utilizados são:
AGUARDANDO\_PAGAMENTO
PAGAMENTO\_APROVADO
EM\_PREPARACAO
PRONTO
ENTREGUE
ou
CANCELADO

As transições são controladas pela regra de negócio da aplicação.
Um pedido entregue não pode voltar para estados anteriores.
Pedidos cancelados não podem voltar para o fluxo normal de preparação.


As Regras de estoque

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


A Fidelização e LGPD

O programa de fidelidade mantém o saldo de pontos por cliente e registra as movimentações realizadas.
Os pontos são gerados após um pagamento MOCK aprovado, conforme a regra implementada no serviço de pagamento.
O sistema mantém histórico das movimentações de pontos.
O programa também possui o campo:
consentimentoLgpd
O resgate de pontos depende do consentimento LGPD e da existência de saldo suficiente.
A solução busca aplicar o princípio de minimização de dados e evitar exposição desnecessária de informações pessoais.


O Pagamento MOCK

O pagamento simulado representa uma integração com um serviço externo de pagamentos.
O endpoint permite informar dois resultados:
APROVADO
RECUSADO


Pagamento aprovado
Quando o resultado é APROVADO:
o pagamento é persistido;
o pedido recebe o status PAGAMENTO\_APROVADO;
são gerados pontos de fidelidade;
a movimentação de pontos é registrada no histórico.

Pagamento recusado
Quando o resultado é RECUSADO:
a tentativa de pagamento é registrada;
o pedido recebe o status CANCELADO;
não são gerados pontos de fidelidade.
O pagamento é somente uma simulação acadêmica e não processa valores financeiros reais.


A Segurança

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


O Tratamento de erros

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
"error": "BAD\_REQUEST",
"message": "A quantidade de pontos deve ser maior que zero."
}


Os Testes realizados

Foram considerados cenários positivos e negativos para validar os principais requisitos da aplicação.

Cenário	                  Resultado esperado
Login válido                       	200
Requisição sem token	              401
Usuário sem permissão	              403
Campo obrigatório ausente  	        400
canalPedido inválido	              400
Produto inexistente	                404
Unidade inexistente	                404
Estoque insuficiente	              409
Pedido válido	                      201
Pagamento MOCK aprovado	  Pedido PAGAMENTO\_APROVADO
Pagamento MOCK recusado	  Pedido CANCELADO
Consulta de fidelidade  	Saldo persistido
Consentimento LGPD	      Consentimento persistido
Resgate de pontos	        Saldo atualizado
Pontos inválidos	                  400
Filtro por canal	        Pedidos filtrados
Consulta de auditoria	    Registro persistido


O Postman

A coleção de testes da API está disponibilizada no projeto em formato JSON, no diretório:
postman/Raízes do Nordeste - Projeto Multidisciplinar.postman\_collection.json

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
Obter JWT
Criar pedido
Executar pagamento MOCK
Verificar status do pedido
Consultar fidelidade
Consultar histórico


As Promoções e campanhas

Como evolução do sistema, está prevista a possibilidade de implementação da campanha:
Raízes da Semana
Regra proposta:
desconto de 10%;
segunda a sexta-feira;
das 11h às 14h;
somente para produtos participantes;
sem cumulatividade com outras promoções.
Essa regra é uma proposta de evolução e não representa uma funcionalidade automatizada do MVP atual.


Observação

Este projeto possui finalidade acadêmica.
