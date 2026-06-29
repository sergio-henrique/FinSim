# 10 — Prompts para Claude Code

## Prompt inicial

```text
Leia o CLAUDE.md e todos os documentos em /docs.

Você será o orquestrador técnico deste projeto.

Não implemente código ainda.

Primeiro:
1. revise a visão do produto;
2. proponha a arquitetura inicial;
3. proponha o plano do MVP 1;
4. sugira a estrutura de pacotes Kotlin;
5. liste os primeiros módulos;
6. identifique riscos técnicos e pedagógicos;
7. gere uma lista de tarefas pequenas para começar.
```

## Planejar feature

```text
Use o agente project-orchestrator e o agente domain-architect.

Planeje a feature: [NOME_DA_FEATURE].

Entregue:
1. objetivo;
2. escopo;
3. fora de escopo;
4. regras de negócio;
5. entidades afetadas;
6. telas afetadas;
7. testes;
8. riscos;
9. plano de implementação em passos pequenos.

Não implemente ainda.
```

## Implementar feature

```text
Implemente a feature: [NOME_DA_FEATURE].

Siga:
- CLAUDE.md;
- docs/03_ARQUITETURA.md;
- docs/06_REGRAS_DE_NEGOCIO.md;
- plano já aprovado.

Ao final:
1. liste arquivos alterados;
2. explique o que foi feito;
3. rode testes se possível;
4. indique próximos passos.
```

## Revisar código

```text
Use o agente qa-tester e revise as alterações recentes.

Procure:
1. bugs;
2. regras quebradas;
3. testes faltantes;
4. problemas de arquitetura;
5. riscos pedagógicos;
6. mensagens inadequadas para jovens.

Não altere nada sem propor antes.
```

## Atualizar documentação

```text
Atualize a documentação afetada pela última mudança.

Verifique:
- docs/02_BACKLOG.md
- docs/03_ARQUITETURA.md
- docs/04_MODELO_DE_DADOS.md
- docs/06_REGRAS_DE_NEGOCIO.md
- docs/11_DECISOES_ARQUITETURA.md
```
