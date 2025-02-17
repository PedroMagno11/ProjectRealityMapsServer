# Project Reality Maps Server

Este projeto fornece uma API REST para gerenciar e servir mapas e tiles do jogo *Project Reality*. Ele permite listar mapas disponíveis e recuperar imagens de tiles individuais por meio de endpoints HTTP.

<img src="assets/background.jpg">


## 🚀 Funcionalidades

- 📌 Listagem de mapas disponíveis
- 🗺️ Recuperação de tiles de mapas específicos


## 🛠️ Tecnologias Utilizadas

- **Java 21**
- **Spring Boot**
- **Swagger**

## 📡 Endpoints da API

### 🔹 Listar Mapas
```http
GET /api/mapas?pagina=0&quantidade_de_mapas=2
```
📥 **Resposta:**
```json
{
  "content": [
    {
      "nome": "adak-beta",
      "tiles": [...],
      "links": [
        {
          "rel": "adak-beta",
          "href": "http://localhost:8080/api/mapas/adak-beta"
        }
      ]
    },
    {
      "nome": "albasrah",
      "tiles": [...],
      "links": [
        {
          "rel": "albasrah",
          "href": "http://localhost:8080/api/mapas/albasrah"
        }
      ]
    },
  "pageable": {...},
    ...
}
```
### 🔹 Obter um Mapa
```http
GET /api/mapas/{mapa}
```
📥 **Exemplo:**
```http
GET /api/mapas/assaultonmestia
```
📥 **Resposta:**
```json
{
  "nome": "assaultonmestia",
  "tiles": [...],
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/mapas/assaultonmestia"
    }
  }
}
```
### 🔹 Obter Tile de um Mapa
```http
GET /api/mapas/{mapa}/{tile}/{x}/{y}
```
📥 **Exemplo:**
```http
GET /api/mapas/assaultonmestia/0/0/0
```
📤 **Resposta:**

<img src="assets/assaultonmestia.jpg">

## 📦 Como Executar

### 🔹 Pré-requisitos
- Java 21+
- Maven 3+
- Ter os mapas baixados e armazenados localmente
- Configurar corretamente o caminho dos mapas no arquivo application.properties

### 🔹 Configuração
No arquivo application.properties, altere a linha:
```
maps.address=C:/caminho/para/seus/mapas
```
Substitua `C:/caminho/para/seus/mapas` pelo diretório onde os mapas estão armazenados.

### 🔹 Download dos Mapas
Você pode baixar os mapas manualmente ou utilizar o script desenvolvido para isso. O repositório do script de download está disponível em:

🔗 [ProjectRealityTilesDownloader](https://github.com/PedroMagno11/ProjectRealityTilesDownloader)

Este script permite baixar um mapa específico ou todos os mapas automaticamente.

### 🔹 Rodando Localmente

1. Clone o repositório:
   ```sh
   git clone https://github.com/PedroMagno11/ProjectRealityMapsServer.git
   cd ProjectRealityMapsServer
   ```
2. Compile e execute o projeto:
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```
3. Acesse a API em `http://localhost:8080/api/mapas`

## 🛠️ Melhorias Futuras
- Implementar cache para otimizar respostas

## 📜 Licença
Este projeto está licenciado sob a **MIT License**.

