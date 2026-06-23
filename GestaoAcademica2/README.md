# App 02 – Gestão Acadêmica Integrada

Aplicativo Android desenvolvido em **Kotlin** para organização da rotina estudantil, gerenciamento de eventos e acompanhamento de avaliações acadêmicas.

---

## Interface

> <img width="1080" height="2400" alt="WhatsApp Image 2026-06-23 at 11 21 00 (1)" src="https://github.com/user-attachments/assets/1be66771-f393-4f03-b496-151ae53b4736" />

---

## Funcionalidades

- Listagem dinâmica de compromissos acadêmicos (provas, trabalhos, palestras)
- Cadastro e edição de eventos com campos de data, tipo e descrição
- Sistema de favoritos para destacar compromissos prioritários
- Filtros por categoria para visualização organizada dos eventos

---

## Tecnologias Utilizadas

| Tecnologia | Uso |
|---|---|
| Kotlin | Linguagem principal |
| MVVM | Arquitetura de separação de responsabilidades |
| LiveData / StateFlow | Observabilidade de estados da UI |
| Room | Persistência local dos dados |
| RecyclerView | Listagem dinâmica de compromissos |

---

## Arquitetura

O aplicativo segue a arquitetura **MVVM (Model-View-ViewModel)**:

- **Model:** Entidades Room e DAOs para persistência local
- **ViewModel:** Expõe os dados via `LiveData`/`StateFlow` e processa a lógica de negócio
- **View:** Fragments/Activities observam o ViewModel e atualizam a UI via RecyclerView

---

## Como Executar

1. Abra a pasta `GestaoAcademica2` no **Android Studio**.
2. Aguarde a sincronização do Gradle.
3. Execute em um emulador ou dispositivo com **API 24+**.
