# api-gblavanderia

**api-gblavanderia** é uma aplicação backend desenvolvida em Java utilizando o framework Spring Boot. Esta API foi projetada para processar dados da empresa GB Lavanderia a partir de arquivos CSV, executando um Algoritmo Genético para otimização do sequência de produção de ordens de serviço em máquinas paralelas.

## Índice

- [api-gblavanderia](#api-gblavanderia)
  - [Índice](#índice)
  - [Pré-requisitos](#pré-requisitos)
  - [Instalação](#instalação)
  - [Configuração](#configuração)
  - [Execução](#execução)
  - [Uso](#uso)
    - [**Endpoint disponível:**](#endpoint-disponível)
  - [Tecnologias utilizadas](#tecnologias-utilizadas)
  - [Contribuição](#contribuição)
  - [Contato](#contato)

## Pré-requisitos

Antes de começar, certifique-se de ter as seguintes ferramentas instaladas em sua máquina:

- [Java 17](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
- [Maven 3.8.6](https://maven.apache.org/download.cgi)

## Instalação

1. **Clone o repositório:**

   ```bash
   git clone https://github.com/matheusjagi/api-gblavanderia.git
   ```

2. **Navegue até o diretório do projeto:**

   ```bash
   cd api-gblavanderia
   ```

## Configuração

Os dados utilizados pela API são carregados a partir de arquivos CSV localizados no diretório:

```
/src/main/java/br/com/ifes/apigblavanderia/repository
```

Caso deseje modificar os dados, basta editar ou substituir os arquivos CSV nessa pasta.

## Execução

Para executar a aplicação, certifique-se que o Maven esteja instalado em sua máquina e execute o seguinte comando:

```bash
mvn clean install spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

## Uso

A API possui documentação disponibilizada através do Swagger, disponível em:

`
http://localhost:8080/swagger-ui/index.html
`

### **Endpoint disponível:**

- **AlgoritmoResource**
  - `GET /api/gblavanderia/{tamanhoInicialPopulacao}/{quantidadeEvolucoes}`: Executa o algoritmo utilizando os dados carregados dos arquivos CSV teste dentro do projeto.

Para testar, você pode acessar diretamente pelo navegador ou utilizar o [Postman](https://www.postman.com/) como a seguinte requisição exemplo:

```bash
GET http://localhost:8080/api/gblavanderia/100/50
```

Caso deseje utilizar um cURL via terminal, segue exemplo:

```bash
curl --location 'http://localhost:8080/api/gblavanderia/100/50'
```

## Tecnologias utilizadas

- **Java 17**: Linguagem de programação principal.
- **Spring Boot 3**: Framework para construção da aplicação.
- **Maven 3.8.6**: Gerenciador de dependências e build.
- **CSV**: Arquivos para armazenamento dos dados.

## Contribuição

Contribuições são bem-vindas! Se você deseja contribuir com este projeto, siga os passos abaixo:

1. **Fork o repositório.**
2. **Crie uma branch para sua feature:**

   ```bash
   git checkout -b feature/minha-nova-feature
   ```

3. **Commit suas alterações:**

   ```bash
   git commit -m 'Adiciona nova feature'
   ```

4. **Faça o push para a branch:**

   ```bash
   git push origin feature/minha-nova-feature
   ```

5. **Abra um Pull Request.**

## Contato

Para dúvidas ou sugestões, entre em contato:

- **Nome:** Matheus Jagi
- **Email:** [jagi.matheus@gmail.com](mailto:seuemail@example.com)
- **GitHub:** [https://github.com/matheusjagi](https://github.com/matheusjagi)

---
