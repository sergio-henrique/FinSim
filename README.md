# FinSim — Escritório de Agentes para Claude Code

Este pacote cria a estrutura inicial de organização do projeto **FinSim**, um app Android educacional de vida financeira e investimentos simulados.

A ideia é manter, no mesmo repositório:

- o código do app;
- a documentação do produto;
- as regras de arquitetura;
- os agentes especializados do Claude Code;
- os comandos personalizados de desenvolvimento.

## Como usar

1. Extraia este ZIP.
2. Abra a pasta `FinSim_Escritorio_Claude` no Claude Code.
3. Leia o arquivo `CLAUDE.md`.
4. Comece pedindo:

```text
Leia o CLAUDE.md e os documentos em /docs. Depois proponha o plano inicial do MVP 1.
```

## Estrutura

```text
FinSim_Escritorio_Claude/
  CLAUDE.md
  README.md
  docs/
  .claude/
    agents/
    commands/
  app/
```

## Observação importante

Este pacote **não contém ainda um projeto Android Studio completo**. Ele contém a estrutura de governança, documentação e agentes para iniciar o desenvolvimento com organização.

Depois, você pode criar o projeto Android dentro desta mesma raiz.
