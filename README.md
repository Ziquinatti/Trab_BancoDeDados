# Trab_BancoDeDados
Trabalho final da disciplina de Projeto e Gerência de Banco de Dados

Aplicação desenvolvida no Android Studio, para rodar em smartphones.

# Requisitos
- Android Studio 2021.3.1
- Git

# Como Usar
- Abrir Android Studio
- File => Settings => Version Control => GitHub => (Adicionar sua conta do GitHub)

Após logar na conta do GitHub:
- File => New => Project from Version Control => GitHub
- Selecionar o repositório que deseja clonar e o diretório que deseja utilizar

Após clonar o repositório:
- Criar o emulador de android (link abaixo)
- Alterar a variável "HOST" nos arquivos "FormCadastro.java" e "FormLogin.java" no seguinte formato:

      String HOST = "http://(seu ip)/(pasta dos arquivos php)";
      
OBS: Apesar do servidor ser local, não pode colocar "localhost" ou "127.0.0.1", deve ser o ip do PC (pode ver no cmd com o comando "ipconfig")
      
OBS_2: A pasta dos arquivos fica (geralmente) neste caminho - C:\Apache24\htdocs\\(pasta dos arquivos php)

# Adicionais
Como criar um emulador de android: https://www.youtube.com/watch?v=HPWzk-lwPVg

- Versões utilizadas: Pixel 4 / Android 9.0 (Pie) - API 28
