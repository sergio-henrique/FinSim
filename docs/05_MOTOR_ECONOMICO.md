# 05 — Motor Econômico

## Objetivo

Criar uma economia simulada, simples, coerente e educativa.

## Princípios

1. A simulação deve ser explicável.
2. O MVP deve começar simples.
3. Não usar cotações reais no início.
4. Não criar falsa sensação de previsão do mercado.
5. Toda fórmula deve ser documentada.
6. Toda regra deve ter teste.

## MVP 1 — Renda fixa simples

Produto inicial:

- Tesouro Selic simulado.

Fórmula simplificada:

```text
valor_final = valor_inicial * (1 + taxa_mensal)
```

Exemplo:

```text
taxa_mensal = 0,008
valor_inicial = 1000
valor_final = 1008
```

## Taxa inicial simulada

Para o MVP:

```text
taxa_mensal_selic_simulada = 0,008
```

Isso equivale a 0,8% ao mês na simulação.

## Passagem de mês

Ao avançar mês:

1. aplicar rendimento da renda fixa;
2. depositar renda mensal;
3. gerar contas;
4. recalcular patrimônio;
5. gerar resumo educativo.

## MVP 2 — Inflação

A inflação pode afetar despesas.

Fórmula simplificada:

```text
nova_despesa = despesa_anterior * (1 + inflacao_mensal)
```

## MVP 3 — Renda variável simulada

Cada ativo pode ter:

- ticker fictício ou inspirado;
- setor;
- preço;
- volatilidade;
- tendência;
- chance de dividendos;
- chance de evento negativo;
- chance de evento positivo.

Fórmula inicial possível:

```text
novo_preco = preco_atual * (1 + tendencia + ruido + impacto_evento)
```

Onde:

- tendência representa ciclo econômico;
- ruído representa oscilação normal;
- impacto_evento representa notícias ou crises.

## Crashes simulados

Eventos raros podem causar quedas fortes.

Exemplo:

```text
queda = -0,20
```

O app deve explicar:

"Crises acontecem. Por isso, diversificação e reserva são importantes."

## Dividendos

Empresas simuladas podem pagar dividendos.

Fórmula simples:

```text
dividendo = quantidade_de_acoes * dividendo_por_acao
```

## Cuidados

- Não usar frases como "compre agora".
- Não sugerir ativos reais.
- Não ensinar especulação como caminho principal.
- Sempre reforçar que rentabilidade passada ou simulada não garante futuro.
