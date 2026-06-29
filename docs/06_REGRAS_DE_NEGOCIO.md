# 06 — Regras de Negócio

## Conta corrente

### RN-001 — Saldo não pode ser inconsistente

O saldo deve refletir todas as transações registradas.

### RN-002 — Pagamento exige saldo

O usuário só pode pagar uma conta se houver saldo suficiente.

### RN-003 — Conta não pode ser paga duas vezes

Uma conta paga deve ficar marcada como paga.

### RN-004 — Entrada mensal acontece uma vez por mês

Ao avançar mês, a renda mensal é depositada uma única vez.

## Reserva de emergência

### RN-005 — Transferência para reserva exige saldo

O usuário só pode transferir para reserva se houver saldo livre.

### RN-006 — Reserva é separada do saldo disponível

A reserva deve aparecer separadamente para ensinar organização.

## Renda fixa

### RN-007 — Aplicação exige saldo

O usuário só pode aplicar se tiver saldo suficiente.

### RN-008 — Rendimento ocorre na passagem de mês

A renda fixa do MVP rende apenas quando o mês avança.

### RN-009 — Produto deve mostrar que é simulado

Todo produto financeiro deve indicar que faz parte de uma simulação educativa.

## Passagem de mês

### RN-010 — Avançar mês deve atualizar o sistema em ordem

Ordem sugerida:

1. aplicar rendimento dos investimentos existentes;
2. depositar renda mensal;
3. gerar contas do mês;
4. calcular snapshots;
5. exibir resumo educativo.

## Educação

### RN-011 — Feedback deve ser construtivo

Não usar mensagens humilhantes ou punitivas.

### RN-012 — Não prometer enriquecimento

O app não pode afirmar que determinada estratégia garante riqueza.

### RN-013 — Não recomendar investimento real

O app pode ensinar conceitos, mas não recomendar ativos reais.
