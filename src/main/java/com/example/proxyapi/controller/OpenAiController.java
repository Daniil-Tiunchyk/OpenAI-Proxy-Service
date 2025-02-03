package com.example.proxyapi.controller;

import com.example.proxyapi.dto.openai.*;
import com.example.proxyapi.service.OpenAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Контроллер для работы с OpenAI (через ProxyAPI).
 * <p>
 * Имеет эндпоинты:
 * 1. GET /v1/models
 * 2. POST /v1/chat/completions
 * 3. POST /v1/images/generations
 * 4. POST /v1/audio/speech
 * 5. POST /v1/audio/transcriptions
 * 6. POST /v1/audio/translations
 */
@Tag(name = "OpenAI (ProxyAPI)", description = "Эндпоинты для взаимодействия с OpenAI через ProxyAPI")
@RestController
@RequestMapping("/openai/v1")
@Slf4j
@Validated
public class OpenAiController {

    private final OpenAiService openAiService;

    public OpenAiController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    /**
     * Получить список доступных моделей.
     *
     * @return JSON-строка со списком моделей
     */
    @Operation(
            summary = "Получить список моделей",
            description = "Метод позволяет узнать, какие модели доступны в OpenAI (через ProxyAPI)."
    )
    @GetMapping("/models")
    public ResponseEntity<String> listModels() {
        log.info("GET /openai/v1/models - запрошен список моделей");
        String modelsJson = openAiService.listModels();
        return ResponseEntity.ok(modelsJson);
    }

    /**
     * Создать Chat Completion.
     *
     * @param requestInputDTO Запрос с параметрами
     * @return Ответ от OpenAI как ChatCompletionResponseDTO
     */
    @Operation(
            summary = "Создать чат-комплишен",
            description = """
                    Отправляет запрос на /v1/chat/completions в OpenAI (через ProxyAPI).
                    """
    )
    @PostMapping("/chat/completions")
    public ResponseEntity<ChatCompletionResponseDTO> createChatCompletion(
            @Valid @RequestBody ChatCompletionRequestInputDTO requestInputDTO
    ) {
        log.info("POST /openai/v1/chat/completions - входящие данные: {}", requestInputDTO);
        ChatCompletionResponseDTO response = openAiService.createChatCompletion(requestInputDTO);
        log.info("POST /openai/v1/chat/completions - ответ: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * Получить Embeddings для переданного текста.
     *
     * @param requestDTO объект с параметрами model и input
     * @return EmbeddingsResponseDTO с векторным представлением текста
     */
    @Operation(
            summary = "Получить Embeddings",
            description = """
                    Отправляет запрос на /v1/embeddings в OpenAI (через ProxyAPI),
                    чтобы получить векторное представление (embedding) для переданного текста.
                    Модель можно выбрать из: text-embedding-3-small, text-embedding-3-large, text-embedding-ada-002.
                    """
    )
    @ApiResponse(responseCode = "200", description = "Успешное получение Embeddings")
    @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    @PostMapping("/embeddings")
    public ResponseEntity<EmbeddingsResponseDTO> createEmbeddings(
            @Valid @RequestBody EmbeddingsRequestDTO requestDTO
    ) {
        log.info("POST /openai/v1/embeddings - входящие данные: {}", requestDTO);
        EmbeddingsResponseDTO response = openAiService.createEmbeddings(requestDTO);
        log.info("POST /openai/v1/embeddings - ответ: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * Генерация изображений на основе текстового prompt.
     *
     * @param requestDTO Запрос с параметрами генерации
     * @return Ответ от OpenAI как ImageGenerationResponseDTO
     */
    @Operation(
            summary = "Генерация изображений",
            description = "Создает оригинальное изображение на основе текстового prompt с использованием DALL·E 2 или DALL·E 3."
    )
    @PostMapping("/images/generations")
    public ResponseEntity<ImageGenerationResponseDTO> generateImage(
            @Valid @RequestBody ImageGenerationRequestDTO requestDTO
    ) {
        log.info("POST /openai/v1/images/generations - входящие данные: {}", requestDTO);
        ImageGenerationResponseDTO response = openAiService.generateImage(requestDTO);
        log.info("POST /openai/v1/images/generations - ответ: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * Генерация аудио на основе текста.
     *
     * @param requestDTO Запрос с параметрами генерации аудио
     * @return Аудио файл в указанном формате
     */
    @Operation(
            summary = "Генерация аудио из текста",
            description = "Преобразует текст в аудио файл с использованием моделей tts-1 или tts-1-hd."
    )
    @PostMapping("/audio/speech")
    public ResponseEntity<byte[]> generateSpeech(
            @Valid @RequestBody AudioSpeechRequestDTO requestDTO
    ) {
        log.info("POST /openai/v1/audio/speech - входящие данные: {}", requestDTO);
        byte[] audioData = openAiService.generateSpeech(requestDTO);
        log.info("POST /openai/v1/audio/speech - аудио сгенерировано, размер: {} байт", audioData.length);

        // Определение типа контента на основе формата
        MediaType mediaType = getMediaTypeForFormat(requestDTO.getFormat());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentLength(audioData.length);
        headers.setContentDispositionFormData("attachment", "speech." + requestDTO.getFormat());

        return ResponseEntity.ok()
                .headers(headers)
                .body(audioData);
    }

    /**
     * Вспомогательный метод для определения типа контента по формату.
     *
     * @param format Формат аудио файла
     * @return Соответствующий MediaType
     */
    private MediaType getMediaTypeForFormat(String format) {
        return switch (format.toLowerCase()) {
            case "mp3" -> MediaType.parseMediaType("audio/mpeg");
            case "opus" -> MediaType.parseMediaType("audio/opus");
            case "aac" -> MediaType.parseMediaType("audio/aac");
            case "flac" -> MediaType.parseMediaType("audio/flac");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    /**
     * Транскрибировать аудио файл.
     *
     * @param file           Аудио файл
     * @param model          Название модели
     * @param responseFormat Формат ответа
     * @param prompt         Дополнительный prompt
     * @return Транскрибированный текст
     */
    @Operation(
            summary = "Транскрипция аудио",
            description = "Транскрибирует предоставленный аудио файл на основе модели Whisper.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешная транскрипция",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AudioResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            }
    )
    @PostMapping(value = "/audio/transcriptions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AudioResponseDTO> transcribeAudio(
            @Parameter(description = "Аудио файл для транскрипции", required = true)
            @RequestPart("file") MultipartFile file,

            @Parameter(description = "Название модели", required = true)
            @RequestPart("model") @Valid @Pattern(regexp = "whisper-1", message = "Model must be 'whisper-1'") String model,

            @Parameter(description = "Формат ответа")
            @RequestPart(value = "response_format", required = false) String responseFormat,

            @Parameter(description = "Дополнительный prompt")
            @RequestPart(value = "prompt", required = false) String prompt
    ) {
        log.info("POST /openai/v1/audio/transcriptions - загружается файл: {}", file.getOriginalFilename());

        AudioRequestDTO requestDTO = new AudioRequestDTO();
        requestDTO.setModel(model);
        requestDTO.setResponse_format(responseFormat);
        requestDTO.setPrompt(prompt);

        AudioResponseDTO response = openAiService.transcribeAudio(file, requestDTO);
        log.info("POST /openai/v1/audio/transcriptions - транскрипция завершена.");

        return ResponseEntity.ok(response);
    }

    /**
     * Перевести аудио файл на английский.
     *
     * @param file           Аудио файл
     * @param model          Название модели
     * @param responseFormat Формат ответа
     * @param prompt         Дополнительный prompt
     * @return Переведённый текст
     */
    @Operation(
            summary = "Перевод аудио",
            description = "Переводит предоставленный аудио файл на английский язык с использованием модели Whisper.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный перевод",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AudioResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            }
    )
    @PostMapping(value = "/audio/translations", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AudioResponseDTO> translateAudio(
            @Parameter(description = "Аудио файл для перевода", required = true)
            @RequestPart("file") MultipartFile file,

            @Parameter(description = "Название модели", required = true)
            @RequestPart("model") @Valid @Pattern(regexp = "whisper-1", message = "Model must be 'whisper-1'") String model,

            @Parameter(description = "Формат ответа")
            @RequestPart(value = "response_format", required = false) String responseFormat,

            @Parameter(description = "Дополнительный prompt")
            @RequestPart(value = "prompt", required = false) String prompt
    ) {
        log.info("POST /openai/v1/audio/translations - загружается файл: {}", file.getOriginalFilename());

        AudioRequestDTO requestDTO = new AudioRequestDTO();
        requestDTO.setModel(model);
        requestDTO.setResponse_format(responseFormat);
        requestDTO.setPrompt(prompt);

        AudioResponseDTO response = openAiService.translateAudio(file, requestDTO);
        log.info("POST /openai/v1/audio/translations - перевод завершён.");

        return ResponseEntity.ok(response);
    }
}
