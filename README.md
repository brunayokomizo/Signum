# Signum

## Descrição do Projeto
O Signum é um aplicativo nativo Android desenvolvido como projeto acadêmico para o Challenge da FIAP. O sistema tem como objetivo otimizar e digitalizar o processo de assinatura e acompanhamento de contratos e documentos, oferecendo uma experiência móvel segura, intuitiva e acessível.

## Perfis de Usuário e Funcionalidades

O sistema conta com dois perfis de acesso distintos, cada um com fluxos e permissões específicas para atender às necessidades da gestão documental:

### 1. Administrador (ADMIN)
O perfil administrador é responsável pela gestão, preparação e envio dos documentos na plataforma.

Funções principais do Administrador:
* Cadastro e gestão de novos signatários no sistema.
* Seleção e upload de arquivos no formato PDF armazenados no dispositivo.
* Posicionamento interativo dos blocos de assinatura na superfície do documento.
* Revisão final das informações da remessa e envio dos convites de assinatura.
* Acompanhamento do ciclo de vida do contrato por meio de uma linha do tempo detalhada e indicador de status geral (Document Tracking).

### 2. Signatário (Usuário Comum)
O perfil signatário destina-se aos clientes e partes envolvidas que necessitam revisar e assinar os documentos atribuídos a eles.

Funções principais do Signatário:
* Painel principal com divisão clara entre documentos pendentes e documentos já assinados.
* Visualização integrada do arquivo PDF para leitura e verificação prévia dos termos do contrato.
* Painel interativo de captura de assinatura por toque (desenho manual da assinatura digital).
* Confirmação e aplicação direta da assinatura no documento.

## Arquitetura e Tecnologias

A aplicação segue os padrões recomendados pelo Google para desenvolvimento nativo Android com Clean Architecture e arquitetura em camadas:

* Linguagem: Kotlin
* Interface Gráfica: Jetpack Compose com Material Design 3
* Arquitetura: Clean Architecture (Domain, Data, Presentation) e MVVM
* Persistência de Dados: Room Database com Kotlin Symbol Processing (KSP)
* Navegação: Jetpack Navigation Compose
* Injeção de Dependências: Contêiner de dependências manuais
* Suporte Multilíngue (i18n): Suporte completo aos idiomas Português (PT), Inglês (EN) e Espanhol (ES)

## Instruções de Compilação e Execução

1. Requisitos do ambiente: Android Studio Ladybug ou superior, JDK 17+ e Android SDK 35+.
2. Clone o repositório em seu ambiente local.
3. Abra o projeto no Android Studio e aguarde a sincronização do Gradle.
4. Execute o aplicativo selecionando o módulo `app` em um emulador ou dispositivo físico conectado.
