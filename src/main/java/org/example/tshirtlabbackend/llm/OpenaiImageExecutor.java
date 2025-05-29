package org.example.tshirtlabbackend.llm;

import com.openai.client.OpenAIClient;
import com.openai.models.ImageGenerateParams;
import lombok.RequiredArgsConstructor;
import org.example.tshirtlabbackend.llm.LLMService.*;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
@RequiredArgsConstructor
public class OpenaiImageExecutor implements LlmExecutor<ImageRequest, ImageResponse> {

    private final OpenAIClient openAI;

    @Override public TaskType supportedTask() { return TaskType.IMAGE_GENERATION; }
    @Override public String   modelName()      { return "gpt-image-1"; }

    @Override
    public ImageResponse execute(ImageRequest req) {
        var params = ImageGenerateParams.builder()
                .model(modelName())
                .prompt(req.prompt())
                .size(ImageGenerateParams.Size._1024X1024)
                .n(1)
                .build();

        var result = openAI.images().generate(params);
        byte[] bytes = Base64.getDecoder()
                .decode(result.data().get(0).b64Json().get());
        return new ImageResponse(bytes);
    }
}

