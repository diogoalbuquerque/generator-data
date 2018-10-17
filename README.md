# Generator-data

[![License: AGPL v3](https://img.shields.io/badge/License-AGPL%20v3-blue.svg)](https://www.gnu.org/licenses/agpl-3.0)
[![Build Status](https://travis-ci.org/diogoalbuquerque/generator-data.svg?branch=master)](https://travis-ci.org/diogoalbuquerque/generator-data)
[![codecov](https://codecov.io/gh/diogoalbuquerque/generator-data/branch/master/graph/badge.svg)](https://codecov.io/gh/diogoalbuquerque/generator-data)

Ferramenta para geração de uma base de dados de nome e CPF.

## Tasks gradle
* bootJar -> Cria o arquivo jar.
* bootRun -> Executa os arquivos no estado atual.

## Formatação e Linter
Task formatKotlin e lintKotlin do gradle

## Variaveis de ambiente
* **geb.generator.path.input** (opcional)-> Variável para informar o caminho de leitura de arquivos .csv 
contendo o primeiro nome e o sobrenome. São necessários dois arquivos. O firstname.csv para primeiro nome e
o lastname.csv para o sobrenome. <br>  
 **EX: c:\generator\entrada**


## Como usar

### Gerador da base de dados
Para gerar a base de dados basta fazer uma requisição
passando o valor da linguagem (selecionar dentre as disponíveis, **ptbr**, **en**)
e aguardar o processo de adição de dados finalizar.
Efetue a requisição seguindo o exemplo abaixo.

#### Parâmetros:

**localhost** = ip do seu computador

**idiom** = informar uma das linguagens disponíveis


```
POST /generator/create HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Cache-Control: no-cache

{
	"idiom": "ptbr"
}
```

```
curl -X POST \
  http://localhost:8080/generator/create \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
	"idiom": "ptbr"
}'
```

### Limpando a base de dados
Para efetuar a limpeza da base de dados preparando assim para uma nova execução,
basta efetuar a requisição abaixo.

#### Parâmetros:

**localhost** = ip do seu computador

```
DELETE /generator/clean HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Cache-Control: no-cache
```

```
curl -X DELETE \
  http://localhost:8080/generator/clean \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json'
```

### Status da base de dados
Para saber o status do banco de dados e a quantidade de dados em cada tabela,
basta efetuar a requisição abaixo.

#### Parâmetros:

**localhost** = ip do seu computador

```
GET /generator/status HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Cache-Control: no-cache
```

```
curl -X GET \
  http://localhost:8080/generator/status \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json'

```
