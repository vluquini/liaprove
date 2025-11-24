package com.lia.liaprove.infrastructure.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotEmpty
    private String oldPassword;

    @NotEmpty
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String newPassword;
}
