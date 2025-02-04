# OpenAI Proxy Service

![License](https://img.shields.io/badge/License-MIT-blue.svg)

## Оглавление

- [OpenAI Proxy Service](#openai-proxy-service)
  - [Оглавление](#оглавление)
  - [Обзор](#обзор)
  - [Функциональность](#функциональность)
  - [Структура Проекта](#структура-проекта)
  - [Установка](#установка)
  - [Конфигурация](#конфигурация)
  - [Развёртывание](#развёртывание)
  - [Использование](#использование)
    - [GET /v1/models](#get-v1models)
    - [POST /v1/chat/completions](#post-v1chatcompletions)
    - [POST /v1/images/generations](#post-v1imagesgenerations)
    - [POST /v1/audio/speech](#post-v1audiospeech)
    - [POST /v1/audio/transcriptions](#post-v1audiotranscriptions)
    - [POST /v1/audio/translations](#post-v1audiotranslations)
    - [POST /v1/embeddings](#post-v1embeddings)
  - [Логирование](#логирование)
  - [Роадмап](#роадмап)
  - [Вклад](#вклад)
  - [Лицензия](#лицензия)
  - [Контакты](#контакты)

---

## Обзор

**OpenAI Proxy Service** — это приложение на базе Spring Boot, предназначенное для удобного взаимодействия с моделями OpenAI. Основная особенность сервиса — возможность легко переключаться между [ProxyAPI](https://proxyapi.ru/) и официальным ChatGPT API без изменения формата запросов.

> **Важно:** Для работы приложения вам понадобится ключ API:
> - Либо от [ProxyAPI](https://proxyapi.ru/) — удобно, если вы уже используете их сервис или не хотите напрямую интегрироваться с ChatGPT.
> - Либо **официальный OpenAI (ChatGPT) ключ**. Форматы запросов полностью совпадают, так что достаточно заменить URL и ключ.

Приложение обеспечивает готовые эндпоинты для:

- Создания чат-комплишенов
- Генерации изображений
- Преобразования текста в речь
- Транскрипции и перевода аудио
- Получения векторных эмбеддингов

При этом **OpenAI Proxy Service** автоматически обрабатывает некоторые особенности моделей (например, `o1`, `o1-mini` и `o3-mini`) и скрывает низкоуровневые детали работы с API.

## Функциональность

- **Гибкое переключение провайдера**: укажите ключ и базовый URL, чтобы отправлять запросы через ProxyAPI или напрямую в ChatGPT API.
- **Поддержка нескольких моделей**: `o1`, `o1-mini`, `gpt-3.5-turbo`, `gpt-4`, `dall-e-3`, модели TTS и Whisper и т.д.
- **Расширенная конфигурация**: задавайте параметры (`maxTokens`, `temperature` и др.) централизованно.
- **Текст в речь**: преобразуйте текст в аудио с помощью моделей `tts-1` или `tts-1-hd`.
- **Транскрипция и перевод аудио**: модели Whisper для распознавания и перевода речи.
- **Embeddings**: получение векторных представлений текста с разных моделей OpenAI.
- **Подробное логирование**: запись всех запросов и ответов для отладки и мониторинга.
- **Интеграционные тесты**: включены тесты для проверки всех основных сценариев работы.

## Структура Проекта

```
└── 📁OpenAI-Proxy-Service
    └── 📁logs
        └── proxyapi.log
    └── 📁src
        └── 📁main
            └── 📁java
                └── 📁com
                    └── 📁example
                        └── 📁proxyapi
                            └── 📁controller
                            └── 📁dto
                            └── 📁exception
                            └── 📁service
                            └── 📁utils
                            └── ProxyApiApplication.java
            └── 📁resources
                └── application.properties
                └── logback-spring.xml
    └── .gitignore
    └── pom.xml
    └── README.md
```

## Установка

1. **Клонируйте репозиторий**:

   ```bash
   git clone https://github.com/Daniil-Tiunchyk/OpenAI-Proxy-Service.git
   cd OpenAI-Proxy-Service
   ```

2. **Соберите проект**:

   ```bash
   mvn clean install
   ```

## Конфигурация

1. **Файл `application.properties`** (в `src/main/resources/application.properties`):

   ```properties
   # Для работы с ProxyAPI:
   api.key=YOUR_PROXY_API_KEY
   api.base-url=https://api.proxyapi.ru/v1
   
   # Или для работы с официальным ChatGPT API:
   # api.key=YOUR_OPENAI_API_KEY
   # api.base-url=https://api.openai.com/v1
   ```

   - `api.key` — ваш ключ (либо от ProxyAPI, либо официальный OpenAI).
   - `api.base-url` — базовый URL-адрес; по умолчанию указывает на ProxyAPI, но вы можете заменить на `https://api.openai.com/v1`, если хотите использовать официальное API ChatGPT.

2. **Тестовая конфигурация**:

   Аналогично можно настроить `application-test.properties` в `src/test/resources/`, чтобы указать тестовые ключи и URL.

## Развёртывание

1. **Запустите приложение**:

   ```bash
   mvn spring-boot:run
   ```

2. **Swagger UI**:

   После запуска перейдите по адресу:

   ```
   http://localhost:8080/swagger-ui/index.html
   ```

   где вы найдёте интерактивную документацию ко всем эндпоинтам.

## Использование

Ниже приведены основные эндпоинты с примерами запросов и ответов. Все запросы передаются в формате, аналогичном ChatGPT API — то есть при желании вы можете использовать уже знакомые вам инструменты интеграции с OpenAI.

### GET /v1/models

Возвращает список доступных моделей.

### POST /v1/chat/completions

Создаёт чат-комплишен по аналогии с `chatGPT` (`gpt-3.5-turbo`, `gpt-4` и т.д.).

### POST /v1/images/generations

Генерирует изображения из текстового описания (аналогично DALL·E API).

### POST /v1/audio/speech

Преобразует текст в аудио (модели `tts-1` и `tts-1-hd`).

### POST /v1/audio/transcriptions

Транскрибирует (распознаёт) аудио-файлы при помощи Whisper.

### POST /v1/audio/translations

Переводит распознанный текст (также на базе Whisper).

### POST /v1/embeddings

Возвращает векторное представление (embedding) для анализа семантической близости текстов.

> **Примечание:** Более детальные примеры тел запросов и ответов можно найти в Swagger.

## Логирование

Все запросы и ответы логируются в файле `proxyapi.log` в директории `logs`. Подробности конфигурации логов — в `logback-spring.xml`.

## Роадмап

- [x] Поддержка Chat Completions, Images, TTS, Transcription, Embeddings
- [x] Простое переключение между ProxyAPI и официальным ChatGPT API
- [ ] Поддержка дополнительных методов (Files, Assistants, Threads) при наличии соответствующей подписки
- [ ] Расширенные функции мониторинга и аналитики

## Вклад

Хотите внести свой вклад?

1. **Сделайте форк репозитория**
2. **Создайте ветку**:

   ```bash
   git checkout -b feature/YourFeature
   ```

3. **Внесите изменения и сделайте commit**:

   ```bash
   git commit -m "Add your commit message"
   ```

4. **Запушьте ветку**:

   ```bash
   git push origin feature/YourFeature
   ```

5. **Создайте Pull Request**.

## Лицензия

Проект распространяется по [MIT License](LICENSE).

## Контакты

Если у вас есть вопросы, пожалуйста, оставьте [issue](https://github.com/Daniil-Tiunchyk/OpenAI-Proxy-Service/issues) или напишите на [fcad.td@gmail.com](mailto:fcad.td@gmail.com).
