# CLAUDE.md

## Projeto

Este projeto se chama **FinSim**.

É um aplicativo Android educacional destinado a crianças e adolescentes, com o objetivo de ensinar vida financeira, orçamento, reserva de emergência, renda fixa, renda variável, risco, liquidez, inflação, juros compostos, dividendos e tomada de decisão.

O aplicativo é uma **simulação educativa**. Ele não deve oferecer recomendação de investimento real.

## Stack inicial sugerida

- Android nativo
- Kotlin
- Jetpack Compose
- Room/SQLite
- Arquitetura em camadas
- Testes unitários para regras de negócio
- Banco local no MVP
- Backend apenas em fase futura

## Princípios do produto

1. Educação antes de rentabilidade.
2. Simulação antes de mercado real.
3. Clareza antes de complexidade.
4. MVP antes de funcionalidades avançadas.
5. Segurança e privacidade antes de coleta de dados.
6. Não usar linguagem de promessa de enriquecimento.
7. O usuário deve aprender com decisões, erros e consequências simuladas.
8. O app deve incentivar disciplina, diversificação, reserva e visão de longo prazo.

## Regras de desenvolvimento

- Antes de codar, propor plano curto.
- Não implementar funcionalidades fora do escopo sem justificar.
- Sempre criar ou atualizar testes para regras de negócio.
- Atualizar documentação quando uma decisão técnica mudar.
- Evitar overengineering.
- Preferir código simples, legível e testável.
- Não apagar arquivos importantes sem confirmação.
- Não fazer commits automáticos sem autorização.
- Não criar integrações externas sem necessidade no MVP.
- Toda simulação financeira deve ser explicável de forma pedagógica.

## Arquitetura desejada

Separar o projeto em camadas:

```text
presentation/
  Telas, componentes Compose, ViewModels e estados de UI.

application/
  Casos de uso e orquestração das ações do usuário.

domain/
  Entidades, objetos de valor e regras puras de negócio.

data/
  Room, SQLite, repositórios, DAOs e persistência.

simulation/
  Motores de simulação: renda fixa, renda variável, eventos e economia.
```

## Módulos do MVP 1

1. Perfil local simples.
2. Conta corrente.
3. Recebimento mensal virtual.
4. Pagamento de contas.
5. Reserva de emergência.
6. Renda fixa simples: Tesouro Selic simulado.
7. Passagem de mês.
8. Estatísticas básicas de patrimônio.
9. Feedback educativo.

## Módulos posteriores

### MVP 2

- CDB simulado.
- Inflação simulada.
- Despesas aleatórias.
- Missões.
- Conquistas.

### MVP 3

- Renda variável simulada.
- Ações fictícias ou inspiradas em setores reais.
- Dividendos.
- Eventos de mercado.
- Crash simulado.
- Carteira.

### MVP 4

- Home broker mais completo.
- Gráficos.
- Ranking local.
- Múltiplos perfis.
- Sincronização em nuvem.

## Cuidados com público jovem

- Não incentivar especulação.
- Não usar linguagem de cassino, aposta ou enriquecimento rápido.
- Explicar risco de forma simples.
- Reforçar que perdas simuladas fazem parte do aprendizado.
- Usar feedback educativo e não punitivo.
- Não coletar dados pessoais desnecessários.
- Não expor menores a interações sociais arriscadas no MVP.
- Não usar notificações manipulativas.

## Definição de pronto

Uma funcionalidade só pode ser considerada pronta quando:

1. A regra de negócio foi implementada.
2. Existem testes relevantes.
3. A documentação foi atualizada.
4. A UI não contradiz o objetivo educacional.
5. Não há linguagem de promessa financeira.
6. O código está legível e organizado.
7. O módulo roda sem erro conhecido.
