# 03 — Arquitetura

## Objetivo

Criar uma arquitetura simples, modular e testável.

## Camadas

```text
presentation/
application/
domain/
data/
simulation/
```

## presentation

Contém:

- telas Compose;
- componentes visuais;
- ViewModels;
- estado de UI;
- navegação.

Não deve conter regra financeira complexa.

## application

Contém casos de uso:

- receber renda mensal;
- pagar conta;
- investir em renda fixa;
- avançar mês;
- calcular resumo financeiro.

## domain

Contém regras puras:

- conta corrente;
- transação;
- despesa;
- reserva;
- investimento;
- carteira;
- mês financeiro.

Deve ser testável sem Android.

## data

Contém persistência:

- Room;
- DAOs;
- entidades de banco;
- repositórios;
- migrations.

## simulation

Contém motores de simulação:

- renda fixa;
- renda variável futura;
- inflação;
- eventos econômicos;
- mercado simulado.

## Regra importante

As fórmulas e regras financeiras devem ficar fora da UI.

## Fluxo ideal

```text
Tela → ViewModel → UseCase → Domain/Simulation → Repository → Banco
```

## Pacotes Kotlin sugeridos

```text
com.finsim.app
  presentation
    home
    account
    fixedincome
    summary
    common

  application
    usecase

  domain
    model
    service
    rule

  data
    local
    repository

  simulation
    fixedincome
    market
    events
    economy
```
