# App 03 – Ciclo de Estudos e Revisões

Aplicativo Android desenvolvido em **Kotlin** para gerenciamento de um diário de estudos com agendamento de revisões e disparos de notificações locais no dispositivo.

---

## Interface

<img width="540" height="1200" alt="WhatsApp Image 2026-06-23 at 11 20 59" src="https://github.com/user-attachments/assets/6a7f7b25-9155-46a5-97a3-8e9c8226d06e" />
<img width="1080" height="2400" alt="WhatsApp Image 2026-06-23 at 11 20 59 (1)" src="https://github.com/user-attachments/assets/aa6ea8a6-40e5-4108-9c5c-82d5b4bd316a" />
<img width="1080" height="2400" alt="WhatsApp Image 2026-06-23 at 11 21 00" src="https://github.com/user-attachments/assets/f759c345-fd93-4503-b1bf-71503ab8fe71" />

---

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
