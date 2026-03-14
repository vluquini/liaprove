package com.lia.liaprove.infrastructure.dtos.ai;

import java.util.List;

public record ProviderChatResponse(List<Choice> choices) {
    public record Choice(Message message) {}
    public record Message(String content) {}
}
