# 🎮 Meu Xamêgo - Guia para Rodar o Jogo

Bem-vindo ao **Meu Xamêgo**! Este README explica tudo o que você precisa para baixar, instalar e executar o jogo corretamente, além de listar possíveis problemas e como resolvê-los.

---

## 🚀 Requisitos para Rodar o Jogo

Antes de iniciar, certifique-se de ter instalado:

### ✔️ **Java (JDK 17 ou superior)**
- Necessário para executar o jogo.
- Verifique a versão instalada:
```sh
java -version
```

### ✔️ **Maven**
- Usado para compilar o projeto e gerenciar dependências.
- Verifique se está configurado corretamente no PATH:
```sh
mvn -v
```

### ✔️ **Git (Opcional, mas recomendado)**
- Para baixar o projeto diretamente do repositório.

### ✔️ **IDE Opcional**
- VS Code, IntelliJ ou Eclipse.
- Não é obrigatório, mas facilita o desenvolvimento.

---

## 📥 Como Baixar o Projeto

### 🔽 Clonando com Git
```sh
git clone https://github.com/seu-repositorio/meu-xamego.git
cd meu-xamego
```

### 📦 Ou baixando ZIP
1. Vá até o repositório do projeto.
2. Clique em **Code > Download ZIP**.
3. Extraia os arquivos.

---

## 🛠️ Compilando o Projeto

No diretório raíz do projeto, execute:
```sh
mvn clean install
```
Isso irá:
- Baixar dependências
- Compilar o código
- Gerar arquivos prontos para execução

---

## ▶️ Como Executar o Jogo

Após compilar:
```sh
java -jar target/meu-xamego.jar
```
Se o arquivo `.jar` tiver outro nome, verifique dentro da pasta `target/`.

---

## 🔧 Problemas Comuns e Soluções

### ❌ **Erro: Maven não é reconhecido**
✔️ Solução: adicione o Maven ao PATH.
- No Windows, inclua o caminho do `apache-maven/bin` nas variáveis de ambiente.

### ❌ **Erro: versão do Java incompatível**
✔️ Solução: instale o **JDK 17** ou superior.

### ❌ **Erro ao compilar dependências**
✔️ Solução: rode novamente:
```sh
mvn clean install -U
```
Isso força atualização de dependências.

### ❌ **O jogo abre mas fecha imediatamente**
✔️ Possíveis causas:
- Arquivos do jogo ausentes.
- Configurações incorretas.
- Falta de permissões.

### ❌ **Problemas com sprites / resolução**
✔️ Solução:
- Verifique se as imagens estão na pasta correta.
- Confirme se os arquivos estão em **32x32** e com fundo transparente.

---

## 📂 Estrutura do Projeto

```txt
meu-xamego/
 ├── src/
 │   ├── main/
 │   │   ├── java/        # Código-fonte
 │   │   ├── resources/   # Imagens, sons, configs
 │   └── test/            # Testes automatizados
 ├── target/              # Build gerado pelo Maven
 ├── pom.xml              # Configurações do Maven
 └── README.md            # Este arquivo
```

---

## 🧑‍💻 Equipe do Projeto

- **Leonardo** – Planejamento do Projeto
- **Fellipe** – Frontend
- **Hiago** – Backend
- **Thiago** – Banco de Dados
- **Raíssa** – Manutenção e Suporte

---

## ❤️ Agradecimentos
Obrigado por jogar **Meu Xamêgo**!
Se encontrar qualquer erro ou quiser sugerir melhorias, fique à vontade para abrir uma **issue** no repositório.

Divirta-se! 🎉