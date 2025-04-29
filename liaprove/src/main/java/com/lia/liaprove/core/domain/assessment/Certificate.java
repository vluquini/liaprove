package com.lia.liaprove.core.domain.assessment;

import java.time.LocalDate;
import java.util.UUID;

public class Certificate {
    private UUID id;
    private String title;
    private String description;
    private String certificateUrl;
    private LocalDate issueDate;
    private Float score;

}
