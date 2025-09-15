# 📦 BuscaCEP

Aplicação Spring Boot para consulta de CEPs via API simulada com WireMock, com persistência de logs e testes automatizados. Ideal para ambientes de desenvolvimento e validação de comportamento sem depender de serviços externos.

---

## 🚀 Tecnologias utilizadas

- Java 17
- Spring Boot 3
- Spring Data JPA
- Spring Cloud OpenFeign
- PostgreSQL
- WireMock (via Docker e embutido nos testes)
- H2 (para testes automatizados)
- Docker & Docker Compose
- Maven

---

## 📁 Estrutura do projeto

```plaintext
src/
├── controller/       → Endpoints REST
├── service/          → Regras de negócio
├── client/           → Feign Client para API simulada
├── model/            → Entidades e DTOs
├── repository/       → Interface JPA
├── config/           → Configurações (opcional)
└── resources/        → application.properties
```
---

## ⚙️ Como rodar o projeto com Docker
1. Gerar o JAR da aplicação
   bash
   mvn clean install
2. Subir os containers
   bash
   docker-compose up --build
   Isso irá iniciar:

buscacep-app: sua aplicação Spring Boot

buscacep-db: banco PostgreSQL

buscacep-wiremock: mock da API de CEP

---

## 🔍 Exemplos de requisição
Buscar CEP
http
GET /cep/01001000
Resposta:

json
{
"cep": "01001-000",
"logradouro": "Praça da Sé",
"bairro": "Sé",
"localidade": "São Paulo",
"uf": "SP"
}

---

## 🧪 Testes automatizados
Os testes utilizam:

WireMock embutido para simular a API externa

H2 em memória para persistência temporária

MockMvc para testar o controller

Rodar os testes
bash
mvn test

---

## 🐳 Estrutura do WireMock via Docker
plaintext
wiremock/
├── mappings/
│   └── cep-01001000.json
└── __files/
└── 01001000.json
Você pode adicionar novos mocks facilmente criando arquivos .json com o padrão acima.

---

## 🔄 Sobre a API simulada
A aplicação está preparada para consumir uma API externa (como o ViaCEP), mas durante o desenvolvimento e testes, essa API é simulada com WireMock, garantindo previsibilidade e controle total sobre os dados retornados.

✅ Em desenvolvimento local, o WireMock roda via Docker e responde como se fosse a API real.

✅ Em testes automatizados, o WireMock é embutido no contexto de teste e simula os endpoints diretamente.

🔄 Em produção, o client pode ser facilmente reconfigurado para consumir a API real ou qualquer outro serviço externo.

---

## 👨‍💻 Autor: Thiago Sampaio

💼 Desenvolvedor Java | Spring Boot | APIs REST

📍 Mauá, São Paulo 

