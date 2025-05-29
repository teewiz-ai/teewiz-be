package org.example.tshirtlabbackend.llm;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LLMService {

    public enum TaskType { TEXT_COMPLETION, IMAGE_GENERATION, IMAGE_TO_TEXT }

    /** Marker interface for requests understood by a particular executor. */
    public sealed interface LlmRequest permits TextRequest, ImageRequest, VisionRequest { }

    /** Marker interface for responses returned by an executor. */
    public sealed interface LlmResponse permits ImageResponse, ResponseBase, TextResponse, VisionResponse { }

    public record TextRequest(List<String> messages)             implements LlmRequest { }
    public record ImageRequest(String prompt)                    implements LlmRequest { }
    public record VisionRequest(byte[] image, @Nullable String prompt) implements LlmRequest { }

    public record TextResponse(String content) implements LlmResponse, ResponseBase { }
    public record ImageResponse(byte[] imageBytes) implements LlmResponse, ResponseBase { }
    public record VisionResponse(String description) implements LlmResponse, ResponseBase { }

    public sealed interface ResponseBase extends LlmResponse permits TextResponse,
            ImageResponse,
            VisionResponse { }

    public interface LlmExecutor<TReq extends LlmRequest,
            TRes extends LlmResponse> {

        /** Text, image-gen, or image-to-text. */
        TaskType supportedTask();

        /** Exact model name, e.g. "gpt-4o", "gpt-image-1", "llama-3-70b-instruct". */
        String modelName();

        /** Perform the call and map SDK-specific result → domain response. */
        TRes execute(TReq request);
    }

    private final Map<TaskType, Map<String, LlmExecutor<?, ?>>> registry = new EnumMap<>(TaskType.class);

    public LLMService(List<LlmExecutor<?, ?>> executors) {
        for (LlmExecutor<?, ?> exec : executors) {
            registry
                    .computeIfAbsent(exec.supportedTask(), k -> new HashMap<>())
                    .put(exec.modelName(), exec);
        }
    }


    /** Chat/completions.  @param model null → use default (first registered) */
    public TextResponse chat(TextRequest request, @Nullable String model) {
        return invoke(TaskType.TEXT_COMPLETION, model, request, TextResponse.class);
    }

    /** Image generation. */
    public ImageResponse generateImage(ImageRequest request, @Nullable String model) {
        return invoke(TaskType.IMAGE_GENERATION, model, request, ImageResponse.class);
    }

    /** Vision / image-to-text (OCR, caption, etc.). */
    public VisionResponse describeImage(VisionRequest request, @Nullable String model) {
        return invoke(TaskType.IMAGE_TO_TEXT, model, request, VisionResponse.class);
    }

    @SuppressWarnings("unchecked")
    private <TReq extends LlmRequest,
            TRes extends LlmResponse>
    TRes invoke(TaskType task,
                @Nullable String model,
                TReq request,
                Class<TRes> expectedType) {

        var taskExecutors = registry.getOrDefault(task, Map.of());
        if (taskExecutors.isEmpty()) {
            throw new IllegalStateException("No executors registered for task " + task);
        }
        String chosenModel = model != null ? model : taskExecutors.keySet().iterator().next();

        var rawExec = taskExecutors.get(chosenModel);
        if (rawExec == null) {
            throw new IllegalArgumentException("Model '%s' not registered for task %s"
                    .formatted(chosenModel, task));
        }

        var result = ((LlmExecutor<TReq, ?>) rawExec).execute(request);
        if (!expectedType.isInstance(result)) {
            throw new IllegalStateException("Executor returned unexpected response type "
                    + result.getClass() + " (expected " + expectedType + ")");
        }
        return expectedType.cast(result);
    }
}
