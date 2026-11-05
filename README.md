# Objectives-And-Key-Results - контроль качества данных по целям и ключевым результатам

Сервис проверяет качество данных по целям и КР, хранит результаты прогонов
в PostgreSQL и отдаёт их через REST API.

Решение написано без ИИ-ассистента.

## Стек

- Java 21, Spring Boot 3.5
- PostgreSQL 15
- Liquibase
- Maven
- Docker Compose

## Переменные окружения

Креды лежат в `.env` (шаблон: `.env.example`):

| Переменная | Назначение | Пример |
|---|---|---|
| `POSTGRES_DB` | имя БД | `app_db` |
| `POSTGRES_USER` | пользователь БД | `user` |
| `POSTGRES_PASSWORD` | пароль БД | `change_me` |
| `POSTGRES_PORT` | порт на хосте | `5555` |
| `APP_PORT` | порт приложения | `8080` |

Для Maven-плагина Liquibase креды - в `liquibase.properties` (шаблон - `liquibase.properties.example`).

## Запуск

```bash
docker compose up
```

Работает на: `http://localhost:APP_PORT`. Надо только свой порт подставить, например, 8080.

При старте Spring Boot запускает Liquibase: создаёт схему и загружает стартовые данные и делает накат, откат и повторный накат.

## API

| Метод | Эндпоинт | Что делает |
|---|---|---|
| GET | `/api/runs` | список прогонов: статус, дата снимка, с чем сравнивается, число проблем |
| GET | `/api/kkd/summary?run_id=&unit_id=` | метрики подразделения (с учётом дочерних), разбивка по важности, дельта с прошлым прогоном |
| GET | `/api/kkd/issues?run_id=&unit_id=&rule=&severity=&limit=&offset=` | проблемы прогона с фильтрами и пагинацией |

Ошибки - единый формат: `{"error": {"code": "...", "message": "..."}}`.
Неизвестный прогон/подразделение = 404, некорректные параметры = 422.

Примеры:

```bash
curl "http://localhost:8080/api/runs"
curl "http://localhost:8080/api/kkd/summary?run_id=RUN-002&unit_id=DEP-1"
curl "http://localhost:8080/api/kkd/issues?run_id=RUN-002&severity=critical"
```

## Тесты

```bash
mvn test
```

## Миграции: накат → откат → повторный накат

```bash
# 1. Накат
mvn liquibase:update

# 2. Откат всех changeset'ов
mvn liquibase:rollback -Dliquibase.rollbackCount=12

# 3. Повторный накат
mvn liquibase:update
```

Параметры подключения для Maven-плагина - в `liquibase.properties` (шаблон: `liquibase.properties.example`).

## Схема БД

- `units` - подразделения с иерархией (`parent_id`);
- `goals` - цели (`unit_id`, `owner`, `parent_goal_id`, `status`, `due_date`);
- `key_results` - КР (`goal_id`, `target_value`, `fact_value`, `weight`, `fact_updated_at`);
- `rules` - справочник правил (`code`, `title`, `severity`);
- `runs` - прогоны (`snapshot_date`, `status`, `compared_to_run_id`, `issues_count`);
- `issues` - найденные проблемы (`run_id`, `rule_code`, `object_type`, `object_id`, `severity`).

## Архитектура

- **Логика правил** - `domain/rules/checkers/` (4 чекера: `GOAL_NO_OWNER`, `GOAL_NO_KR`,
  `KR_WEIGHTS_SUM`, `KR_STALE_PROGRESS`). Каждый реализует `RuleChecker` и возвращает
  `List<FoundIssue>`, не зная про БД и HTTP. `RuleEngine` прогоняет все чекеры.
  
- **Прогон** - `domain/service/RunService`: берёт активные цели/КР, прогоняет правила,
  сохраняет `runs` и `issues`, `compared_to_run_id` и `issues_count`.
  
- **Метрики** - `domain/service/SummaryService`: по поддереву подразделений
  (`UnitRepository.findSubtreeOfUnitIds` делает рекурсивный запрос) считает метрики
  (`critical_issues_count`, `major_issues_count`, `minor_issues_count`, `issues_total`),
  разбивку по важности и дельту.
  
- **Дельта** - `domain/service/DeltaService`: сравнивает два прогона, используя пару
  `(rule_code, object_id)` - получаем: `new` / `persisting` / `resolved`. У первого прогона - `null`.
  
- **API** - `web/Controller` + `web/ExceptionController`.
