package com.qease.exception;

/** Thrown when an authenticated user tries to act on a resource that belongs to someone else (HTTP 403). */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
