# ProxyAPI-Service

![License](https://img.shields.io/badge/License-MIT-blue.svg)

## Оглавление

- [ProxyAPI-Service](#proxyapi-service)
  - [Оглавление](#оглавление)
  - [Обзор](#обзор)
  - [Функциональность](#функциональность)
    - [Особенности моделей o1 и o1-mini](#особенности-моделей-o1-и-o1-mini)
  - [Структура Проекта](#структура-проекта)
  - [Установка](#установка)
  - [Конфигурация](#конфигурация)
  - [Развёртывание](#развёртывание)
  - [Использование](#использование)
    - [GET /v1/models](#get-v1models)
    - [POST /v1/chat/completions](#post-v1chatcompletions)
    - [POST /v1/images/generations](#post-v1imagesgenerations)
  - [Логирование](#логирование)
  - [Роадмап](#роадмап)
    - [Доступные методы](#доступные-методы)
      - [OpenAI](#openai)
      - [Google](#google)
      - [Anthropic](#anthropic)
  - [Вклад](#вклад)
  - [Лицензия](#лицензия)
  - [Контакты](#контакты)

## Обзор

**ProxyAPI-Service** — это приложение на базе Spring Boot, предназначенное для взаимодействия с [ProxyAPI](https://proxyapi.ru/) OpenAI. Проект фокусируется на реализации необходимых роутов для работы с моделями OpenAI, обеспечивая удобное и безопасное управление запросами к API. Для работы приложения необходим API ключ от [ProxyAPI](https://proxyapi.ru/).

## Функциональность

- **Поддержка нескольких моделей:** Легко переключайтесь между различными моделями OpenAI, такими как `o1`, `o1-mini`, `gpt-4` и другими.
- **Расширенные настройки:** Управляйте параметрами, специфичными для моделей, такими как `maxCompletionTokens` и `temperature`, без необходимости предоставлять их пользователям.
- **Комплексное логирование:** Ведётся подробное логирование всех взаимодействий с ProxyAPI, что облегчает мониторинг и отладку.
- **Интеграционные тесты:** Набор интеграционных тестов обеспечивает надёжность и корректность работы с различными моделями и сценариями.
- **Документация Swagger:** Интерактивная документация API, сгенерированная с помощью Swagger, для удобного изучения и тестирования эндпоинтов.

### Особенности моделей o1 и o1-mini

Модели `o1` и `o1-mini` требуют особой обработки параметров запроса, при иных запрос вернёт ошибку. Для этих моделей автоматически устанавливаются следующие параметры:

- **`temperature`** устанавливается на значение `1.0`, вне зависимости от значения в запросе.
- **`maxCompletionTokens`** используется вместо стандартного `maxTokens` для контроля количества генерируемых токенов. При отправке запроса к [ProxyAPI](https://proxyapi.ru/) `maxTokens` автоматически заменяется на `maxCompletionTokens`.

Для обеспечения этих особенностей был добавлен дополнительный DTO (`ChatCompletionRequestDTO`), который позволяет изолировать внутренние параметры от внешнего API, предоставляя пользователю только необходимые поля (`model`, `messages`, `maxTokens` и `temperature`).

## Структура Проекта

```
└── 📁ProxyAPI-Service
    └── 📁logs
        └── proxyapi.log
    └── 📁src
        └── 📁main
            └── 📁java
                └── 📁com
                    └── 📁example
                        └── 📁proxyapi
                            └── 📁controller
                                └── GlobalExceptionHandler.java
                                └── OpenAiController.java
                            └── 📁dto
                                └── ErrorResponse.java
                                └── 📁openai
                                    └── ChatCompletionRequestDTO.java
                                    └── ChatCompletionRequestInputDTO.java
                                    └── ChatCompletionResponseDTO.java
                                    └── ChoiceDTO.java
                                    └── MessageDTO.java
                                    └── UsageDTO.java
                            └── 📁exception
                                └── ProxyApiException.java
                            └── ProxyApiApplication.java
                            └── 📁service
                                └── OpenAiService.java
                            └── 📁utils
                                └── ProxyApiHttpClient.java
            └── 📁resources
                └── application.properties
                └── logback-spring.xml
    └── .gitignore
    └── pom.xml
    └── README.md
```

## Установка

1. **Клонирование Репозитория:**

   ```bash
   git clone https://github.com/Daniil-Tiunchyk/ProxyAPI-Service.git
   cd ProxyAPI-Service
   ```

2. **Сборка Проекта:**

   Убедитесь, что у вас установлен Maven. Затем выполните команду:

   ```bash
   mvn clean install
   ```

## Конфигурация

1. **Настройка `application.properties`:**

   Файл находится по пути `src/main/resources/application.properties`.

   ```properties
   # Конфигурация ProxyAPI OpenAI
   proxyapi.key=YOUR_PROXY_API_KEY
   ```

   **Важно:** Для работы проекта необходимо получить API ключ от [ProxyAPI](https://proxyapi.ru/) и указать его в данном файле.

2. **Тестовая Конфигурация:**

   Для запуска интеграционных тестов настройте `application-test.properties` в `src/test/resources/`:

   ```properties
   # src/test/resources/application-test.properties
   proxyapi.key=YOUR_PROXY_API_KEY_FOR_TESTS
   ```

## Развёртывание

1. **Запуск Приложения:**

   ```bash
   mvn spring-boot:run
   ```

2. **Доступ к Swagger UI:**

   После запуска приложения перейдите по адресу:

   ```
   http://localhost:8080/swagger-ui/index.html
   ```

   Здесь доступна интерактивная документация API для изучения и тестирования эндпоинтов.

## Использование

### GET /v1/models

Получение списка доступных моделей OpenAI.

**Запрос:**

```http
GET /openai/v1/models HTTP/1.1
Host: localhost:8080
Authorization: Bearer ВАШ_PROXY_API_KEY
```

**Ответ:**

```json
{
  "object": "list",
  "data": [
    {
      "id": "gpt-4",
      "object": "model",
      ...
    },
    ...
  ]
}
```

### POST /v1/chat/completions

Создание чат-комплишена с использованием выбранной модели.

**Запрос:**

```http
POST /openai/v1/chat/completions HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer YOUR_PROXY_API_KEY

{
  "model": "gpt-3.5-turbo",
  "messages": [
    {
      "role": "user",
      "content": "Say this is a test!"
    }
  ],
  "maxTokens": 100,
  "temperature": 0.7
}
```

**Ответ:**

```json
{
  "id": "chatcmpl-123",
  "object": "chat.completion",
  "created": 1616161616,
  "model": "gpt-3.5-turbo",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": "This is a test!"
      },
      "finish_reason": "stop"
    }
  ],
  "usage": {
    "prompt_tokens": 9,
    "completion_tokens": 6,
    "total_tokens": 15
  }
}
```

### POST /v1/images/generations

Генерация изображений на основе текстового prompt.

**Запрос:**

```http
POST /openai/v1/images/generations HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer YOUR_PROXY_API_KEY

{
  "model": "dall-e-3",
  "prompt": "белый сиамский кот",
  "n": 1,
  "size": "1024x1024",
  "quality": "hd"
}
```

**Ответ:**

```json
{
  "data": [
    {
      "url": "https://example.com/generated-image.png",
      "base64": "iVBORw0KGgoAAAANSUhEUgAA..."
    }
  ]
}
```

**Примечания:**

- **`model`:** Укажите модель для генерации, например, `dall-e-3` или `dall-e-2`.
- **`prompt`:** Текстовый запрос для генерации изображения.
- **`n`:** Количество изображений для генерации (до 10 для DALL·E 2).
- **`size`:** Размер изображения, например, `1024x1024`, `1024x1792`, `1792x1024` и т.д..
- **`quality`:** Опциональный параметр, например, `"hd"` для повышения детализации.

## Логирование

Все взаимодействия с ProxyAPI логируются в файле `proxyapi.log`, находящемся в директории `logs`. Конфигурация логирования управляется файлом `logback-spring.xml` в `src/main/resources/`. Логи содержат подробную информацию о запросах и ответах, что облегчает мониторинг и отладку.

## Роадмап

### Доступные методы

#### OpenAI

- [x] `/v1/models`
- [x] `/v1/chat/completions`
- [x] `/v1/images/generations`
- [ ] `/v1/images/edits`
- [ ] `/v1/images/variations`
- [ ] `/v1/embeddings`
- [ ] `/v1/files` (доступно в подписке ProxyAPI Pro)
- [ ] `/v1/assistants` (доступно в подписке ProxyAPI Pro)
- [ ] `/v1/threads/*` (доступно в подписке ProxyAPI Pro)
- [ ] `/v1/audio/*`

#### Google

- [ ] `/v1/models/{model}:generateContent`
- [ ] `/v1/models/{model}:streamGenerateContent`
- [ ] `/v1beta/models/{model}:generateContent`
- [ ] `/v1beta/models/{model}:streamGenerateContent`
- [ ] `/v1/models/{model}:countTokens`
- [ ] `/v1beta/models/{model}:countTokens`

#### Anthropic

- [ ] `/v1/messages`

## Вклад

Вклады приветствуются! Пожалуйста, следуйте следующим шагам:

1. **Форк репозитория**
2. **Создайте новую ветку:**

   ```bash
   git checkout -b feature/YourFeature
   ```

3. **Закоммитьте ваши изменения:**

   ```bash
   git commit -m "Add your commit message"
   ```

4. **Запушьте в ветку:**

   ```bash
   git push origin feature/YourFeature
   ```

5. **Откройте Pull Request**

## Лицензия

Этот проект лицензирован под [MIT License](LICENSE).

## Контакты

Для любых вопросов или предложений, пожалуйста, откройте [issue](https://github.com/Daniil-Tiunchyk/ProxyAPI-Service/issues) или свяжитесь по электронной почте [fcad.td@gmail.com](mailto:fcad.td@gmail.com).
