package com.tuum.interview.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Currency {

    private String id;
    private String name;

    public Currency(String name) {
        this.name = name;
    }
}
