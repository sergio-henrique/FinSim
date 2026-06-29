# ADR-001 — Arquitetura Inicial do FinSim

**Data:** 2026-06-28
**Status:** Aceito
**Decisores:** Sergio Henrique

---

## Decisão 1 — Arquitetura em camadas

### Contexto

Projeto Android iniciado do zero, sem código existente. A ausência de legado permite definir a estrutura antes de qualquer implementação.

### Decisão

Adotar arquitetura em 5 camadas com responsabilidades explícitas:

```
presentation/   — Telas, componentes Compose, ViewModels e estados de UI
application/    — Casos de uso e orquestração das ações do usuário
domain/         — Entidades, objetos de valor e regras puras de negócio
data/           — Room, SQLite, repositórios e DAOs
simulation/     — Motores de simulação: renda fixa, variável, eventos e economia
```

### Motivo

- `domain` e `simulation` sem dependência do Android permitem testes unitários puros com JVM.
- Separação de responsabilidades evita que regras financeiras fiquem acopladas à UI ou ao banco.
- A camada `simulation` isolada documenta e protege as fórmulas financeiras pedagógicas.

### Consequências

- As camadas `domain` e `simulation` não podem importar nenhuma classe do SDK Android.
- ViewModels residem em `presentation` e consomem casos de uso de `application`.
- Mudanças de banco ou de UI não afetam as regras de negócio em `domain`.

---

## Decisão 2 — Representação de valores monetários

### Contexto

Operações financeiras como juros compostos, inflação e rendimentos acumulados exigem precisão numérica. Erros de arredondamento podem gerar resultados educacionais incorretos.

### Decisão

- Entidades de persistência (Room): usar `Long` armazenando o valor em centavos.
- Regras de domínio e simulação: usar `BigDecimal` quando a precisão for necessária.
- Não usar `Double` ou `Float` para valores monetários.

### Motivo

`Double` e `Float` têm erros de representação em ponto flutuante que se acumulam em operações financeiras iterativas. `Long` em centavos é seguro para persistência. `BigDecimal` é preciso para cálculos intermediários.

### Consequências

- A UI deve sempre converter centavos para exibição legível (ex: `1000L` = R$ 10,00).
- Funções de formatação monetária devem ser centralizadas para evitar inconsistências.
- A conversão entre `Long` e `BigDecimal` deve ocorrer nos limites de camada, não espalhada pelo código.

---

## Decisão 3 — Injeção de dependência com Hilt

### Contexto

Repositórios, DAOs e casos de uso precisam ser injetados em ViewModels e em outros casos de uso. A injeção manual em um projeto Android de múltiplas camadas gera boilerplate e dificulta testes.

### Decisão

Usar Hilt (Dagger Hilt para Android) como framework de injeção de dependência.

### Motivo

- Integração nativa com `ViewModel` via `@HiltViewModel`, sem necessidade de factories manuais.
- Menos boilerplate que Dagger puro, com geração de código em tempo de compilação.
- Suporte oficial do Google para projetos Android com Kotlin e Compose.

### Consequências

- `Application` deve ser anotada com `@HiltAndroidApp`.
- `MainActivity` deve ser anotada com `@AndroidEntryPoint`.
- Módulos Hilt devem ser criados em `data/` para expor repositórios e DAOs.
- Testes de integração podem usar `HiltAndroidRule` para injeção nos testes instrumentados.

---

## Decisão 4 — Banco local com Room

### Contexto

O MVP não terá backend. Todos os dados do perfil, conta corrente, histórico e investimentos simulados ficam no dispositivo do usuário.

### Decisão

Usar Room (abstração sobre SQLite) para persistência local no MVP.

### Motivo

- Alinhado ao princípio de privacidade: nenhum dado é enviado a servidores externos no MVP.
- Room oferece tipagem segura via DAOs, migrations controladas e integração com Flow para reatividade.
- Simplicidade adequada ao escopo do MVP.

### Consequências

- Dados não são sincronizados entre dispositivos no MVP. Cada instalação é independente.
- Migrations de banco devem ser planejadas antes de qualquer alteração de schema em produção.
- Backend e sincronização em nuvem estão fora do escopo até o MVP 4.

**Fora de escopo no MVP:** Firebase, Supabase, sincronização remota, exportação de dados.

---

## Decisão 5 — Motor econômico isolado na camada `simulation/`

### Contexto

As fórmulas de juros compostos, rendimento do Tesouro Selic simulado, inflação e outros cálculos financeiros precisam ser testáveis de forma isolada e documentáveis de forma pedagógica.

### Decisão

Criar a camada `simulation/` separada das demais, sem dependência de Android, banco de dados ou UI. O motor recebe valores como parâmetros e retorna resultados calculados. Os casos de uso em `application/` são responsáveis por orquestrar a leitura do banco, a chamada ao motor e a gravação dos resultados.

### Motivo

- Testes unitários puros: as fórmulas podem ser verificadas com entradas e saídas conhecidas, sem mocks de banco ou de Android.
- Documentação pedagógica: cada função do motor pode ser acompanhada de explicação da fórmula utilizada.
- Reusabilidade: o mesmo motor pode ser chamado por diferentes casos de uso sem duplicação.

### Consequências

- O motor não lê nem grava no banco diretamente.
- Toda persistência intermediária é responsabilidade dos casos de uso em `application/`.
- Fórmulas financeiras devem ter cobertura de testes antes de serem consideradas prontas.
- Mudanças nas fórmulas devem ser registradas em ADR ou em comentário de código com justificativa pedagógica.
