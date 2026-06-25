# App 01 – Gestão de Finanças Pessoais

Aplicativo Android desenvolvido em **Kotlin com Jetpack Compose** para controle dinâmico de gastos mensais com teto orçamentário configurável. Os dados são **persistidos localmente** e isolados por usuário.

---

## Funcionalidades

- **Cadastro de despesas** com nome, valor, categoria e data (validação de campos)
- **Cálculo automático** do total consumido em tempo real
- **Saldo restante** exibido dinamicamente (teto − gastos)
- **Alertas visuais**: barra de progresso colorida (azul / âmbar / vermelho) e aviso ao se aproximar ou ultrapassar o teto
- **Edição e exclusão** de despesas
- **Teto orçamentário** configurável pelo usuário
- **Seletor de período** (mês/ano) que filtra os lançamentos exibidos
- **Resumo por categoria** e orçamento por categoria
- **Login/cadastro local** (contas com senha em hash SHA-256); cada usuário vê apenas seus próprios dados, e contas novas começam vazias
- Validação de inputs para campos vazios e formatos incorretos (despesa, teto, login)

---

## Tecnologias Utilizadas

| Tecnologia | Uso |
|---|---|
| Kotlin | Linguagem principal |
| Jetpack Compose | Construção da interface declarativa |
| Material 3 | Componentes e tema |
| ViewModel + StateFlow | Camada de apresentação reativa (MVVM) |
| Room | Persistência local das despesas |
| SharedPreferences | Sessão, contas e preferências por usuário (teto, período) |
| Kotlin Coroutines | Operações assíncronas de banco |
| Fonte DM Sans | Tipografia (em `res/font`) |

---

## Arquitetura

O aplicativo segue **MVVM**:

- **UI (Compose)** — telas `Login`, `Início`, `Despesas`, `Orçamento`, `Perfil` e a gaveta de adicionar/editar; observa o estado via `collectAsState`.
- **`FinanceViewModel`** — expõe `StateFlow` de despesas e configurações, escopados ao usuário logado, e concentra as ações (CRUD, login/cadastro, teto, período).
- **`FinanceRepository`** — intermedia o acesso ao banco.
- **Room** (`FinanceDatabase`, `ExpenseDao`, entidade `Expense`) — persiste as despesas, com coluna `ownerEmail` isolando os dados por usuário.
- **`AccountStore` / `SettingsStore`** (SharedPreferences) — contas locais (senha em SHA-256), sessão e preferências (teto e período) por usuário.

Estrutura de pastas:

```
app/src/main/java/com/example/financaspessoais1/
├── data/            # AccountStore, SettingsStore, db/ (Room), repository/
├── model/           # Expense (entidade), ExpenseCategory
└── ui/              # telas Compose, FinanceViewModel, tema
```

---

## Como Executar

1. Abra a pasta `FinancasPessoais1` no **Android Studio**.
2. Aguarde a sincronização do Gradle (Room é processado via KSP).
3. Execute em um emulador ou dispositivo com **API 24+**.
4. Na primeira vez, crie uma conta em **"Criar conta"**; o app abre vazio e os dados ficam salvos no dispositivo para os próximos acessos.

> Build pela linha de comando: defina o SDK (`local.properties` com `sdk.dir`, ou variável `ANDROID_HOME`) e rode `./gradlew :app:assembleDebug`.
