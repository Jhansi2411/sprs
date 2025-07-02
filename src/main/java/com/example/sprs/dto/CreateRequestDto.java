package com.example.sprs.dto;

import com.example.sprs.model.Request;

import 	jakarta.validation.constraints.NotNull;

public class CreateRequestDto {
    @NotNull(message = "Request type is required")
    private Request.RequestType requestType;

    @NotNull(message = "Form data is required")
    private Request.FormData formData;

    // Constructors
    public CreateRequestDto() {}

    public CreateRequestDto(Request.RequestType requestType, Request.FormData formData) {
        this.requestType = requestType;
        this.formData = formData;
    }

    // Getters and setters
    public Request.RequestType getRequestType() { return requestType; }
    public void setRequestType(Request.RequestType requestType) { this.requestType = requestType; }

    public Request.FormData getFormData() { return formData; }
    public void setFormData(Request.FormData formData) { this.formData = formData; }
}