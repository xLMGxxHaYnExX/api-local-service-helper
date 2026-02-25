/**
 * Copyright (c) HPI Limited 2026
 * <p>
 * All rights reserved. No part of this work may be reproduced or transmitted, in any form or by any
 * means, or adapted (including for the purposes of error correction) without the written permission
 * of the copyright owner except in accordance with the provisions of the Copyright, Designs and
 * Patents Act 1988 or under the terms of a Licence entered into with the copyright owner.
 * <p>
 * Warning: the doing of an unauthorised act in relation to a copyright work may result in both a
 * civil claim for damages and a criminal prosecution.
 */
package io.devhub.apilocalservicehelper.security;

import java.util.regex.Pattern;

/**
 * Input validation utility to prevent injection attacks and malicious input
 */
public class InputValidator {

    // Constants for field size limits
    public static final int MAX_ID_LENGTH = 100;
    public static final int MAX_APP_LENGTH = 100;
    public static final int MAX_CATEGORY_LENGTH = 100;
    public static final int MAX_TYPE_LENGTH = 100;
    public static final int MAX_TITLE_LENGTH = 500;
    public static final int MAX_COMMAND_LENGTH = 10000;
    public static final int MAX_DESCRIPTION_LENGTH = 5000;
    public static final int MAX_TAGS_JSON_LENGTH = 2000;
    public static final int MIN_PRIORITY = 0;
    public static final int MAX_PRIORITY = 1000;

    // Patterns for safe identifiers
    private static final Pattern SAFE_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\-\\.]+$");
    private static final Pattern SAFE_CATEGORY_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\-\\s]+$");
    private static final Pattern SAFE_TYPE_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\-]+$");

    // SQL Injection keywords to detect - using word boundaries
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)\\b(drop|delete|insert|update|select|exec|execute|script|javascript|onerror|onload|alert|union)\\b|;|--|/\\*",
        Pattern.CASE_INSENSITIVE
    );

    // Command injection patterns - allow < > for documentation placeholders
    private static final Pattern COMMAND_INJECTION_PATTERN = Pattern.compile(
        "(?i)[;&|`$()\\n\\r]|\\$\\{|\\$\\(|\\|\\||&&"
    );

    /**
     * Validates a command ID
     */
    public static String validateId(String id) throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("ID cannot be null or empty");
        }

        id = id.trim();

        if (id.length() > MAX_ID_LENGTH) {
            throw new ValidationException("ID length exceeds maximum allowed length of " + MAX_ID_LENGTH);
        }

        if (!SAFE_ID_PATTERN.matcher(id).matches()) {
            throw new ValidationException("ID contains invalid characters. Only alphanumeric, hyphen, underscore, and dot allowed");
        }

        if (containsInjectionPatterns(id)) {
            throw new ValidationException("ID contains potentially malicious content");
        }

        return id;
    }

    /**
     * Validates application name
     */
    public static String validateApp(String app) throws ValidationException {
        if (app == null || app.trim().isEmpty()) {
            throw new ValidationException("App cannot be null or empty");
        }

        app = app.trim();

        if (app.length() > MAX_APP_LENGTH) {
            throw new ValidationException("App length exceeds maximum allowed length of " + MAX_APP_LENGTH);
        }

        if (containsInjectionPatterns(app)) {
            throw new ValidationException("App contains potentially malicious content");
        }

        return app;
    }

    /**
     * Validates category
     */
    public static String validateCategory(String category) throws ValidationException {
        if (category == null || category.trim().isEmpty()) {
            throw new ValidationException("Category cannot be null or empty");
        }

        category = category.trim();

        if (category.length() > MAX_CATEGORY_LENGTH) {
            throw new ValidationException("Category length exceeds maximum allowed length of " + MAX_CATEGORY_LENGTH);
        }

        if (!SAFE_CATEGORY_PATTERN.matcher(category).matches()) {
            throw new ValidationException("Category contains invalid characters");
        }

        if (containsInjectionPatterns(category)) {
            throw new ValidationException("Category contains potentially malicious content");
        }

        return category;
    }

    /**
     * Validates type
     */
    public static String validateType(String type) throws ValidationException {
        if (type == null || type.trim().isEmpty()) {
            throw new ValidationException("Type cannot be null or empty");
        }

        type = type.trim();

        if (type.length() > MAX_TYPE_LENGTH) {
            throw new ValidationException("Type length exceeds maximum allowed length of " + MAX_TYPE_LENGTH);
        }

        if (!SAFE_TYPE_PATTERN.matcher(type).matches()) {
            throw new ValidationException("Type contains invalid characters");
        }

        return type;
    }

    /**
     * Validates title
     */
    public static String validateTitle(String title) throws ValidationException {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Title cannot be null or empty");
        }

        title = title.trim();

        if (title.length() > MAX_TITLE_LENGTH) {
            throw new ValidationException("Title length exceeds maximum allowed length of " + MAX_TITLE_LENGTH);
        }

        if (containsInjectionPatterns(title)) {
            throw new ValidationException("Title contains potentially malicious content");
        }

        return title;
    }

    /**
     * Validates command - with special attention to command injection
     */
    public static String validateCommand(String command) throws ValidationException {
        if (command == null || command.trim().isEmpty()) {
            throw new ValidationException("Command cannot be null or empty");
        }

        command = command.trim();

        if (command.length() > MAX_COMMAND_LENGTH) {
            throw new ValidationException("Command length exceeds maximum allowed length of " + MAX_COMMAND_LENGTH);
        }

        if (containsCommandInjectionPatterns(command)) {
            throw new ValidationException("Command contains potentially dangerous characters or patterns");
        }

        return command;
    }

    /**
     * Validates description
     */
    public static String validateDescription(String description) throws ValidationException {
        if (description == null) {
            return null;
        }

        description = description.trim();

        if (description.isEmpty()) {
            return null;
        }

        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new ValidationException("Description length exceeds maximum allowed length of " + MAX_DESCRIPTION_LENGTH);
        }

        if (containsInjectionPatterns(description)) {
            throw new ValidationException("Description contains potentially malicious content");
        }

        return description;
    }

    /**
     * Validates priority
     */
    public static Integer validatePriority(Integer priority) throws ValidationException {
        if (priority == null) {
            return 0;
        }

        if (priority < MIN_PRIORITY || priority > MAX_PRIORITY) {
            throw new ValidationException("Priority must be between " + MIN_PRIORITY + " and " + MAX_PRIORITY);
        }

        return priority;
    }

    /**
     * Validates tagsJson - should be valid JSON
     */
    public static String validateTagsJson(String tagsJson) throws ValidationException {
        if (tagsJson == null) {
            return null;
        }

        tagsJson = tagsJson.trim();

        if (tagsJson.isEmpty()) {
            return null;
        }

        if (tagsJson.length() > MAX_TAGS_JSON_LENGTH) {
            throw new ValidationException("Tags JSON length exceeds maximum allowed length of " + MAX_TAGS_JSON_LENGTH);
        }

        // Basic JSON validation
        if (!isValidJson(tagsJson)) {
            throw new ValidationException("Tags JSON is not valid JSON format");
        }

        if (containsInjectionPatterns(tagsJson)) {
            throw new ValidationException("Tags JSON contains potentially malicious content");
        }

        return tagsJson;
    }

    /**
     * Checks if string contains SQL injection patterns
     */
    private static boolean containsSqlInjectionPatterns(String input) {
        return SQL_INJECTION_PATTERN.matcher(input).find();
    }

    /**
     * Checks if string contains command injection patterns
     */
    private static boolean containsCommandInjectionPatterns(String input) {
        return COMMAND_INJECTION_PATTERN.matcher(input).find();
    }

    /**
     * Checks if string contains various injection patterns
     */
    private static boolean containsInjectionPatterns(String input) {
        if (input == null) {
            return false;
        }

        return containsSqlInjectionPatterns(input) ||
               containsCommandInjectionPatterns(input);
    }

    /**
     * Basic JSON validation
     */
    private static boolean isValidJson(String json) {
        if (!json.startsWith("[") && !json.startsWith("{")) {
            return false;
        }
        if (!json.endsWith("]") && !json.endsWith("}")) {
            return false;
        }
        try {
            // Simple check for basic JSON structure
            int braceCount = 0;
            int bracketCount = 0;
            for (char c : json.toCharArray()) {
                switch(c) {
                    case '{': braceCount++; break;
                    case '}': braceCount--; break;
                    case '[': bracketCount++; break;
                    case ']': bracketCount--; break;
                }
                if (braceCount < 0 || bracketCount < 0) {
                    return false;
                }
            }
            return braceCount == 0 && bracketCount == 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Exception for validation errors
     */
    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}



