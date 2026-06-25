# App 03 – Ciclo de Estudos e Revisões

Aplicativo Android desenvolvido em **Kotlin** para gerenciamento de um diário de estudos com agendamento de revisões e disparos de notificações locais no dispositivo.

---

<img width="298" height="649" alt="image" src="https://github.com/user-attachments/assets/b5d817d3-40aa-48f4-bfe6-f1e233332627" />


## Funcionalidades

- Cadastro de disciplinas e tópicos estudados
- Histórico de conteúdos lidos com registro cronológico
- Cronograma de revisões com data e hora configuráveis
- Notificações locais automáticas disparadas no momento agendado da revisão

---

## Tecnologias Utilizadas

| Tecnologia | Uso |
|---|---|
| Kotlin | Linguagem principal |
| MVVM | Arquitetura de separação de responsabilidades |
| Room | Armazenamento persistente de disciplinas e revisões |
| DatePicker / TimePicker | Seleção de data e hora para agendamentos |
| WorkManager | Agendamento e disparo de notificações locais |

---

## Arquitetura

O aplicativo segue a arquitetura **MVVM (Model-View-ViewModel)**:

- **Model:** Entidades Room (Disciplina, Tópico, Revisão) e DAOs
- **ViewModel:** Gerencia o estado das listas e aciona o WorkManager para agendamentos
- **View:** Observa o ViewModel e apresenta as informações com DatePicker/TimePicker para entrada de dados

O **WorkManager** garante que as notificações sejam disparadas mesmo com o app em segundo plano ou o dispositivo reiniciado.

---

## Como Executar

1. Abra a pasta `CicloEstudos3` no **Android Studio**.
2. Aguarde a sincronização do Gradle.
3. Execute em um emulador ou dispositivo com **API 24+**.
4. Certifique-se de conceder permissão de notificações ao app quando solicitado.
