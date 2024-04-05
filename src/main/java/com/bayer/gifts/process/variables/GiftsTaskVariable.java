package com.bayer.gifts.process.variables;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
@Data
@Slf4j
public class GiftsTaskVariable implements Serializable {

    private static final long serialVersionUID = -7522975340449329808L;

    private Long userId;
    private String taskId;
    private String approve;
    private String comment;
}
