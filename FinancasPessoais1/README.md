# App 01 – Gestão de Finanças Pessoais

Aplicativo Android desenvolvido em **Kotlin com Jetpack Compose** para controle dinâmico de gastos mensais com teto orçamentário configurável.

---

## Interface

<img width="273" height="606" alt="image" src="https://github.com/user-attachments/assets/f9721569-a01b-4755-bb3d-dac99f14695c" />

---

## Funcionalidades

- Cadastro de despesas com nome e valor
- Cálculo automático do total consumido em tempo real
- Exibição do saldo restante com atualização dinâmica
- Alertas visuais ao se aproximar ou ultrapassar o teto orçamentário
- Validação de inputs para campos vazios e formatos incorretos

---

## Tecnologias Utilizadas

| Tecnologia | Uso |
|---|---|
| Kotlin | Linguagem principal |
| Jetpack Compose | Construção da interface declarativa |
| Listas mutáveis (`mutableStateListOf`) | Gerenciamento dinâmico das despesas |
| State (`remember` / `mutableStateOf`) | Controle de estado da UI |

---

## Arquitetura

O aplicativo utiliza **Jetpack Compose** com gerenciamento de estado local via `remember` e `mutableStateOf`, mantendo a UI reativa às alterações da lista de despesas.

---

## Como Executar

1. Abra a pasta `FinancasPessoais1` no **Android Studio**.
2. Aguarde a sincronização do Gradle.
3. Execute em um emulador ou dispositivo com **API 24+**.
