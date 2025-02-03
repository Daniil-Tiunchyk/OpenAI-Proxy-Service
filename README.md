# ProxyAPI-Service

![License](https://img.shields.io/badge/License-MIT-blue.svg)

## Оглавление

- [ProxyAPI-Service](#proxyapi-service)
  - [Оглавление](#оглавление)
  - [Обзор](#обзор)
  - [Функциональность](#функциональность)
    - [Особенности моделей o1 и o1-mini](#особенности-моделей-o1-и-o1-mini)
    - [Текст в речь (TTS)](#текст-в-речь-tts)
    - [Транскрипция и Перевод Аудио](#транскрипция-и-перевод-аудио)
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
    - [Доступные методы](#доступные-методы)
      - [OpenAI](#openai)
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
- **Текст в речь (TTS):** Поддержка моделей `tts-1` и `tts-1-hd` для преобразования текста в аудио, с возможностью выбора голоса и формата вывода.
- **Транскрипция и Перевод Аудио:** Возможность транскрибировать аудио файлы и переводить с использованием модели Whisper.
- **Embeddings:** Поддержка моделей для получения векторных представлений текста (например, `text-embedding-3-small`, `text-embedding-3-large`, `text-embedding-ada-002`).

### Особенности моделей o1 и o1-mini

Модели `o1` и `o1-mini` требуют особой обработки параметров запроса, при иных запрос вернёт ошибку. Для этих моделей автоматически устанавливаются следующие параметры:

- **`temperature`** устанавливается на значение `1.0`, вне зависимости от значения в запросе.
- **`maxCompletionTokens`** используется вместо стандартного `maxTokens` для контроля количества генерируемых токенов. При отправке запроса к [ProxyAPI](https://proxyapi.ru/) `maxTokens` автоматически заменяется на `maxCompletionTokens`.

Для обеспечения этих особенностей был добавлен дополнительный DTO (`ChatCompletionRequestDTO`), который позволяет изолировать внутренние параметры от внешнего API, предоставляя пользователю только необходимые поля (`model`, `messages`, `maxTokens` и `temperature`).

### Текст в речь (TTS)

Теперь **ProxyAPI-Service** поддерживает преобразование текста в аудио с использованием моделей `tts-1` и `tts-1-hd`. Это позволяет создавать озвученные версии текстов различных форматов.

**Основные возможности:**

- **Выбор модели:** Используйте `tts-1` для минимальной задержки или `tts-1-hd` для высокого качества звука.
- **Выбор голоса:** Доступны различные голоса, такие как `alloy`, `echo`, `fable`, `onyx`, `nova` и `shimmer`.
- **Форматы вывода:** Поддерживаются форматы `mp3`, `opus`, `aac` и `flac`.
- **Поддержка нескольких языков:** Модели поддерживают множество языков, включая английский, русский, французский и другие.

### Транскрипция и Перевод Аудио

**ProxyAPI-Service** предоставляет возможности транскрипции аудио файлов и их перевода с использованием модели Whisper.

**Основные возможности:**

- **Транскрипция Аудио:** Преобразование аудио файлов в текст с использованием модели Whisper.
- **Перевод Аудио:** Перевод транскрибированного текста.
- **Поддержка различных форматов аудио:** Поддерживаются форматы `flac`, `m4a`, `mp3`, `mp4`, `mpeg`, `mpga`, `oga`, `ogg`, `wav` и `webm`.

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
                                    └── AudioRequestDTO.java
                                    └── AudioResponseDTO.java
                                    └── AudioSpeechRequestDTO.java
                                    └── ChatCompletionRequestDTO.java
                                    └── ChatCompletionRequestInputDTO.java
                                    └── ChatCompletionResponseDTO.java
                                    └── EmbeddingsRequestDTO.java
                                    └── EmbeddingsResponseDTO.java
                                    └── ImageDataDTO.java
                                    └── ImageGenerationRequestDTO.java
                                    └── ImageGenerationResponseDTO.java
                                    └── MessageDTO.java
                            └── 📁exception
                                └── ProxyApiException.java
                            └── ProxyApiApplication.java
                            └── 📁service
                                └── OpenAiService.java
                            └── 📁utils
                                └── FilePart.java
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
      "url": "https://example.com/generated-image1.png",
      "base64": "iVBORw0KGgoAAAANSUhEUgAA..."
    },
    {
      "url": "https://example.com/generated-image2.png",
      "base64": "iVBORw0KGgoAAAANSUhEUgAA..."
    }
  ]
}
```

**Примечания:**

- **`model`:** Укажите модель для генерации, например, `dall-e-3` или `dall-e-2`.
- **`prompt`:** Текстовый запрос для генерации изображения.
- **`n`:** Количество изображений для генерации (до 10 для DALL·E 2).
- **`size`:** Размер изображения, например, `256x256`, `512x512`, `1024x1024` и т.д.
- **`quality`:** Опциональный параметр, например, `"hd"` для повышения детализации.

### POST /v1/audio/speech

Преобразование текста в аудио файл с использованием моделей `tts-1` или `tts-1-hd`.

**Запрос:**

```http
POST /openai/v1/audio/speech HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer YOUR_PROXY_API_KEY

{
  "model": "tts-1",
  "voice": "alloy",
  "input": "Today is a wonderful day to build something people love!",
  "format": "mp3"
}
```

**Параметры запроса:**

- **`model`:** Название модели, например, `tts-1` или `tts-1-hd`.
- **`voice`:** Голос для генерации аудио. Доступные варианты: `alloy`, `echo`, `fable`, `onyx`, `nova`, `shimmer`.
- **`input`:** Текст для преобразования в аудио.
- **`format`:** Формат выходного аудио. Доступные варианты: `mp3`, `opus`, `aac`, `flac`. По умолчанию `mp3`.

**Ответ:**

Аудио файл в указанном формате. Пример использования `curl` для сохранения файла:

```bash
curl -X POST "http://localhost:8080/openai/v1/audio/speech" \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer YOUR_PROXY_API_KEY" \
     -d '{
           "model": "tts-1",
           "voice": "alloy",
           "input": "Today is a wonderful day to build something people love!",
           "format": "mp3"
         }' --output speech.mp3
```

**Примечания:**

- **`model`:** Выбор между `tts-1` (низкая задержка) и `tts-1-hd` (высокое качество звука).
- **`voice`:** Экспериментируйте с различными голосами для достижения желаемого тона.
- **`format`:** Выберите формат, подходящий для вашего применения:
  - **`mp3`:** По умолчанию, широко поддерживается.
  - **`opus`:** Для потоковой передачи и низкой задержки.
  - **`aac`:** Для цифрового аудио сжатия, подходит для YouTube, Android, iOS.
  - **`flac`:** Для без потерь сжатия, рекомендуется для аудиофилов и архивирования.

### POST /v1/audio/transcriptions

Транскрипция аудио файла с использованием модели Whisper.

**Запрос:**

```http
POST /openai/v1/audio/transcriptions HTTP/1.1
Host: localhost:8080
Content-Type: multipart/form-data
Authorization: Bearer YOUR_PROXY_API_KEY

--boundary
Content-Disposition: form-data; name="file"; filename="speech.mp3"
Content-Type: audio/mpeg

<binary data>
--boundary
Content-Disposition: form-data; name="model"

whisper-1
--boundary
Content-Disposition: form-data; name="response_format"

json
--boundary
Content-Disposition: form-data; name="prompt"

Используй правильную пунктуацию и заглавные буквы.
--boundary--
```

**Параметры запроса:**

- **`file`:** Аудио файл для транскрипции. Поддерживаются форматы: `flac`, `m4a`, `mp3`, `mp4`, `mpeg`, `mpga`, `oga`, `ogg`, `wav`, `webm`.
- **`model`:** Название модели, например, `whisper-1`.
- **`response_format`:** Формат ответа. Возможные значения: `json` (по умолчанию), `text`.
- **`prompt`:** Дополнительный prompt для улучшения качества транскрипции.

**Ответ:**

```json
{
  "text": "Привет, меня зовут Вольфганг и я из Германии. Куда вы сегодня направляетесь?"
}
```

### POST /v1/audio/translations

Перевод аудио файла с использованием модели Whisper.

**Запрос:**

```http
POST /openai/v1/audio/translations HTTP/1.1
Host: localhost:8080
Content-Type: multipart/form-data
Authorization: Bearer YOUR_PROXY_API_KEY

--boundary
Content-Disposition: form-data; name="file"; filename="speech.mp3"
Content-Type: audio/mpeg

<binary data>
--boundary
Content-Disposition: form-data; name="model"

whisper-1
--boundary
Content-Disposition: form-data; name="response_format"

json
--boundary
Content-Disposition: form-data; name="prompt"

Используй правильную пунктуацию и заглавные буквы.
--boundary--
```

**Параметры запроса:**

- **`file`:** Аудио файл для перевода. Поддерживаются форматы: `flac`, `m4a`, `mp3`, `mp4`, `mpeg`, `mpga`, `oga`, `ogg`, `wav`, `webm`.
- **`model`:** Название модели, например, `whisper-1`.
- **`response_format`:** Формат ответа. Возможные значения: `json` (по умолчанию), `text`.
- **`prompt`:** Дополнительный prompt для улучшения качества перевода.

**Ответ:**

```json
{
  "text": "Hello, my name is Wolfgang and I am from Germany. Where are you heading today?"
}
```

**Примечания:**


- **`model`:** Выбор модели для транскрипции и перевода.
- **`response_format`:** Выберите формат ответа, подходящий для вашего применения.
- **`prompt`:** Используйте для уточнения и улучшения качества транскрипции или перевода.

### POST /v1/embeddings

Получение векторного представления текста (embedding) с помощью одной из моделей:  

- `text-embedding-3-small`  
- `text-embedding-3-large`  
- `text-embedding-ada-002`  

**Запрос:**

```http
POST /openai/v1/embeddings HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer YOUR_PROXY_API_KEY

{
  "model": "text-embedding-3-small",
  "input": "Пример текста для получения эмбеддинга"
}
```

**Параметры запроса:**

- **`model`:** Название модели, например, `text-embedding-3-small`, `text-embedding-3-large` или `text-embedding-ada-002`.
- **`input`:** Текст, для которого нужно получить векторное представление (embedding).

**Ответ:**

```json
{
  "object": "list",
  "model": "text-embedding-3-small",
  "data": [
    {
      "object": "embedding",
      "index": 0,
      "embedding": [
        -0.006929283495992422,
        -0.005336422007530928,
        ...
        -0.024047505110502243
      ]
    }
  ],
  "usage": {
    "prompt_tokens": 5,
    "total_tokens": 5
  }
}
```

**Примечания:**

- **Векторное представление (embedding):** массив чисел с плавающей точкой, отражающих семантическую близость слов. Чем меньше косинусное расстояние между embedding-ами двух текстов, тем они более «похожи» по смыслу.
- **Ограничения:** Максимальный размер входа (в токенах) варьируется в зависимости от модели. Убедитесь, что вы не превышаете лимитов, накладываемых OpenAI/ProxyAPI.

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
- [x] `/v1/embeddings`
- [ ] `/v1/files` (доступно в подписке ProxyAPI Pro)
- [ ] `/v1/assistants` (доступно в подписке ProxyAPI Pro)
- [ ] `/v1/threads/*` (доступно в подписке ProxyAPI Pro)
- [x] `/v1/audio/speech`
- [x] `/v1/audio/transcriptions`
- [x] `/v1/audio/translations`

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
