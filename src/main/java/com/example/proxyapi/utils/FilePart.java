package com.example.proxyapi.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilePart {
    private String fieldName;
    private String fileName;
    private byte[] content;
    private String contentType;
}
