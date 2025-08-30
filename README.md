# 🚜 Oystr Web Scraper - Projeto de Análise de Máquinas Agrícolas

## 📋 Descrição do Projeto

Este projeto foi desenvolvido como parte do processo seletivo da Oystr para a vaga de Desenvolvedor Java. A aplicação realiza web scraping em três sites de anúncios de máquinas agrícolas, extraindo informações específicas e gerando um arquivo JSON consolidado com os dados coletados.

## ✨ Funcionalidades
- Web Scraping Avançado: Coleta dados de múltiplos sites de anúncios
- Extração de Dados: Obtém informações como modelo, tipo de contrato, marca, ano, horas de trabalho, cidade, preço e imagem
- Geração de JSON: Exporta os dados coletados em formato JSON organizado
- Configuração Stealth: Navegador headless configurado para evitar detecção
- Arquitetura Modular: Design expansível para adicionar novos sites facilmente

## 🛠️ Tecnologias Utilizadas
- Java 17+
- Spring Boot 3.0
- Spring Context (Injeção de Dependência)
- Selenium WebDriver 4.0
- JSoup
- Lombok
- JUnit 5 + Mockito (Testes)
- Maven (Gerenciamento de dependências)
- WebDriverManager (Gerenciamento automático de drivers)

## 📦 Estrutura do Projeto
```
src/
├── main/
│   ├── java/com/br/oystr/
│   │   ├── config/          # Configurações do WebDriver
│   │   ├── model/           # Entidades (Machine)
│   │   └── service/         # Interfaces e serviços principais
|   |       └── impl/        # Implementações específicas por site
│   └── resources/           # Arquivos de configuração
├── test/                    # Testes unitários
└── output/                  # Pasta gerada com resultados JSON
```
## ⚙️ Configuração e Instalação
### Pré-requisitos ###
- Java 17 ou superior
- Maven 3.8 ou superior
- Conexão com internet para download de dependências

```
# Clone o repositório
git clone <url-do-repositorio>

# Navegue até o diretório
cd oystr-web-scraper

# Compile o projeto
mvn clean compile

# Execute a aplicação
mvn spring-boot:run
```

## 🚀 Como Usar
A aplicação é executada automaticamente ao ser iniciada. Ela acessará os 6 URLs pré-definidos (2 de cada site) e gerará um arquivo JSON na pasta output/ com o timestamp da execução.

### Execução Personalizada ### 
Para modificar os URLs alvo, edite o array urls na classe OystrApplication:
```
String[] urls = new String[]{
    "https://www.agrofy.com.br/trator-john-deere-7230j-205340.html",
    "https://www.agrofy.com.br/trator-case-ih-puma-215-207085.html",
    // Adicione outros URLs aqui
};
```

## 📊 Sites Suportados
Atualmente a aplicação suporta scraping dos seguintes sites:

1. **Agrofy** (agrofy.com.br)

2. **Mercado Máquinas** (mercadomaquinas.com.br)

3. **Tratores e Colheitadeiras** (tratoresecolheitadeiras.com.br)

## 🔧 Configuração do WebDriver
O projeto utiliza duas configurações de WebDriver:

1. **Modo Headless** (Produção): Chrome invisível para scraping

2. **Modo Visual** (Desenvolvimento): Chrome visível para debugging

Para alternar entre os modos, injete o bean desejado:
```
// Para modo headless (padrão)
@Autowired
private WebDriver webDriver;

// Para modo visual
@Autowired 
@Qualifier("webDriverVisual")
private WebDriver webDriverVisual;
```
## 🧪 Testes
Execute os testes unitários com:
```
mvn test
```
Estrutura de Testes
- **Testes de Unidade**: Validam a lógica de extração de dados
- **Testes com Mocks**: Simulam o comportamento do WebDriver
- **Cobertura**: Inclui testes para todos os bots implementados

# 📝 Exemplo de Saída
O arquivo JSON gerado terá a seguinte estrutura:
```
[
  {
    "model": "John Deere 7230",
    "contractType": "Venda",
    "brand": "John Deere",
    "year": 2019,
    "city": "São Paulo/SP",
    "price": "R$ 185.000,00",
    "imageUrl": "https://example.com/image.jpg",
    "workingHours": "2.500 horas",
    "url": "https://www.agrofy.com.br/trator-john-deere-7230j-205340.html"
  }
]
```
## 🎯 Requisitos Atendidos
- Desenvolvido em Java com princípios de OOP

- Build com Maven (pom.xml incluído)

- Extração de dados de 6 páginas (2 de cada site)

- Coleta de todos os 8 campos solicitados

- Injeção de Dependência com Spring

- Testes unitários com JUnit e Mockito

## 🔮 Próximas Melhorias
- Suporte a mais sites de anúncios

- Cache de resultados para evitar requisições repetidas

- Interface web para configuração e execução

- Exportação para outros formatos (CSV, XML)

- Agendamento de execuções automáticas

## 📄 Licença
Este projeto foi desenvolvido para o processo seletivo da Oystr.

👨‍💻 Desenvolvedor
Desenvolvido por mim, Diego Avila como parte do desafio técnico da Oystr.

Nota: Este projeto é destinado exclusivamente para fins educacionais e de avaliação técnica. Recomenda-se verificar as políticas de uso de cada website antes de realizar scraping em ambiente produtivo.
