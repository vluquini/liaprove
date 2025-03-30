package com.lia.liaprove.core.domain.assessment;

import java.time.LocalDateTime;

public class Assessment {
    private String title;
    private String description;
    // Como os Recruiters podem criar novas avaliações, a data de criação é uma informação importante
    private LocalDateTime creationDate;

}
