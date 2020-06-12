package com.example.libnetwork;

public class ApiResponse<T> {
    public boolean success;
    public int state;
    public String message;
    public T body;
}
