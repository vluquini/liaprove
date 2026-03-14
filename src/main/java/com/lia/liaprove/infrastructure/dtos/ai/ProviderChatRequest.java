package com.lia.liaprove.infrastructure.dtos.ai;

import java.util.List;

public record ProviderChatRequest(
        String model,
        double temperature,
        List<Message> messages
) {
    public record Message(String role, String content) {}
}
