# 11 — Decisões de Arquitetura

Este arquivo registra decisões importantes do projeto.

Use o formato ADR simplificado.

## ADR-001 — App Android nativo

Status: Proposto

Decisão:

Usar Android nativo com Kotlin e Jetpack Compose.

Motivo:

- melhor integração com Android;
- boa performance;
- UI moderna;
- forte ecossistema;
- bom suporte a testes.

Consequências:

- foco inicial apenas em Android;
- iOS fica para fase futura.

## ADR-002 — Banco local no MVP

Status: Proposto

Decisão:

Usar Room/SQLite no MVP.

Motivo:

- reduz complexidade;
- não exige backend inicial;
- protege privacidade;
- acelera desenvolvimento.

Consequências:

- sem sincronização entre dispositivos no começo;
- backup e nuvem ficam para fase futura.

## ADR-003 — Simulação, não dados reais

Status: Proposto

Decisão:

O MVP não usará cotações reais nem recomendações reais de investimento.

Motivo:

- público jovem;
- segurança;
- simplicidade;
- foco educacional;
- menor risco jurídico e ético.

Consequências:

- mercado será fictício/simulado;
- o app precisa deixar isso claro na interface.
