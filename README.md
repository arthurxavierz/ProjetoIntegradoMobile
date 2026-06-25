# Projeto Integrado de Desenvolvimento Mobile

Repositório unificado desenvolvido para a disciplina de **Programação de Dispositivos Móveis**, contendo três aplicativos Android desenvolvidos individualmente em **Kotlin** no Android Studio.

---

## Integrantes


Pedro Lopes | App 01 – Gestão de Finanças Pessoais 
Tiago Castro | App 02 – Gestão Acadêmica Integrada 
Arthur Xavier | App 03 – Ciclo de Estudos e Revisões 

---

## Estrutura do Repositório

```
ProjetoIntegradoMobile/
├── FinancasPessoais1/       # App 01 – Gestão de Finanças Pessoais
├── GestaoAcademica2/        # App 02 – Gestão Acadêmica Integrada
└── CicloEstudos3/           # App 03 – Ciclo de Estudos e Revisões
```

---

## Visão Geral dos Aplicativos

### App 01 – Gestão de Finanças Pessoais
Aplicativo de controle dinâmico de gastos mensais com teto orçamentário configurável. Permite o cadastro de despesas, cálculo automático do total consumido e exibição em tempo real do saldo restante com alertas visuais.

**Tecnologias:** Jetpack Compose · Listas mutáveis · Validação de inputs

---

### App 02 – Gestão Acadêmica Integrada
Aplicativo para organização da rotina estudantil com gerenciamento de eventos e avaliações. Conta com listagem dinâmica de compromissos, telas de cadastro/edição, sistema de favoritos e filtros por categoria.

**Tecnologias:** MVVM · LiveData/StateFlow · Room · RecyclerView

---

### App 03 – Ciclo de Estudos e Revisões
Diário de estudos com agendamento de revisões e disparos de notificações locais. Permite o cadastro de disciplinas e tópicos, histórico de conteúdos lidos e um cronograma de revisões com alertas automáticos no dispositivo.

**Tecnologias:** MVVM · DatePicker · TimePicker · WorkManager · Room

---

## Protótipos no Figma

> *(Inserir links dos protótipos do Figma de cada aplicativo aqui)*

---

## Como Executar os Projetos

1. Clone o repositório:
   ```bash
   git clone https://github.com/arthurxavierz/ProjetoIntegradoMobile.git
   ```
2. Abra o **Android Studio** e selecione **"Open"**.
3. Navegue até a pasta do aplicativo desejado (`FinancasPessoais1`, `GestaoAcademica2` ou `CicloEstudos3`) e abra-a como projeto.
4. Aguarde a sincronização do Gradle.
5. Execute em um emulador ou dispositivo físico com **API 24+**.

> Cada aplicativo é um projeto independente. Abra-os separadamente no Android Studio.

Desenvolvido por Arthur Xavier, Pedro Lopes e Tiago Castro.
