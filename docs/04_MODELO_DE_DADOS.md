# 04 — Modelo de Dados Inicial

## Entidades do MVP 1

### UserProfile

Campos sugeridos:

- id
- name
- ageRange
- monthlyIncome
- currentMonth
- createdAt

### Account

Campos:

- id
- profileId
- balance
- emergencyReserveBalance
- updatedAt

### Transaction

Campos:

- id
- accountId
- type
- amount
- description
- month
- createdAt

Tipos:

- INCOME
- BILL_PAYMENT
- RESERVE_TRANSFER
- INVESTMENT_APPLICATION
- INVESTMENT_REDEMPTION
- EVENT

### Bill

Campos:

- id
- profileId
- name
- amount
- month
- isPaid
- category
- dueMonth

Categorias:

- HOUSING
- FOOD
- TRANSPORT
- EDUCATION
- HEALTH
- LEISURE
- OTHER

### FixedIncomeInvestment

Campos:

- id
- profileId
- productType
- investedAmount
- currentAmount
- monthlyRate
- startMonth
- maturityMonth
- liquidityType
- createdAt

Tipos:

- TESOURO_SELIC_SIMULADO
- CDB_SIMULADO_FUTURO

### MonthlySnapshot

Campos:

- id
- profileId
- month
- accountBalance
- reserveBalance
- fixedIncomeBalance
- totalWealth
- billsPaidAmount
- billsPendingAmount
- financialHealthScore

## Observação

Valores monetários devem ser tratados com cuidado.

Em Kotlin, considerar:

- Long em centavos; ou
- BigDecimal nas regras de domínio.

Evitar Double para dinheiro em regras críticas.
