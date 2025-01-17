# Tech Events API

[![NPM](https://img.shields.io/npm/l/react)](https://github.com/WelingtonFranzoso/franzoso-agregador-de-investimentos/blob/main/LICENSE) 


# Sobre o projeto

Tech Events API é uma aplicação backend que fornece uma API RESTful para realizar operações de CRUD em eventos. A aplicação utiliza DTOs para garantir uma gestão eficiente e segura dos dados. O Flyway é utilizado para assegurar a criação das tabelas de entidade no banco de dados.
Além disso, a aplicação faz upload das imagens diretamente de um bucket S3 da AWS. O banco de dados utilizado é o PostgreSQL, que é executado dentro de um container Docker.



# Funcionalidades
- CRUD Completo: Realiza operações de criação, leitura, atualização e deleção de produtos.
- Gerenciamento de Banco de Dados com Flyway: para gerar a tabela no banco de dados.
- Paginação no retorno das requisições HTTP-GET.
- Persistência de Dados: Utiliza o PostgreSQL como banco de dados relacional para armazenar as informações dos produtos.
- Upload de uma imagem de um bucket S3 da AWS.

# Tecnologias utilizadas
- Java: Linguagem de programação utilizada para o desenvolvimento.
- Spring Boot: Framework para construção da aplicação.
- JPA / Hibernate - ORM (Object-Relational Mapping) para interação com o banco de dados.
- Maven: Gerenciador de dependências e build.
- Flyway: Ppara versionamento e gerenciamento do banco de dados.
- Postgres: Banco de dados relacional.
- PgAdmin: Ferramenta gráfica para gerenciamento do PostgreSQL.
- Docker: Utilizado para containerizar o banco de dados PostgreSQL e PgAdmin.
- AWS S3Client - Para upload e retorno de uma imagem que está em um bucket.

# Endpoints
## Os seguintes endpoints estão disponíveis para interagir com a aplicação:
- POST /api/event: Cria um novo evento com os dados fornecidos.
- POST /api/coupon//event/{eventId}:  Cria um novo cupom para um evento especifico.
## Filtros Personalizados
- GET /api/event: Retorna todos os eventos que ainda estão por vir.
- GET /api/event/{eventId}: Retorna um evento específico identificado pelo id.
- GET /api/event/filter: Retorna evemtos identificado por parametros.

# Como executar o projeto
## Back end
### Pré-requisitos: 
- Java 17 ou superior
- Maven (para construção do projeto)
- Docker

```bash
# clonar repositório
git clone git@github.com:WelingtonFranzoso/tech-events.git

# entrar na pasta do projeto back end
cd tech-events

# executar o projeto
./mvnw spring-boot:run
```

Docker: Configuração do PostgreSQL e PgAdmin
O projeto já está configurado para rodar o PostgreSQL em um container Docker. Para isso, basta garantir que o Docker esteja instalado e em funcionamento. O Docker irá gerenciar o banco de dados de forma transparente.

Acessando o PgAdmin
Para acessar o PgAdmin, você pode navegar até http://localhost:5050 e usar as credenciais padrão:

Email: admin@admin.com
Senha: admin
Lá você poderá gerenciar o banco de dados PostgreSQL utilizado pela aplicação.

# Autor

Welington Franzoso
https://www.linkedin.com/in/welington-franzoso-88a8301b9
