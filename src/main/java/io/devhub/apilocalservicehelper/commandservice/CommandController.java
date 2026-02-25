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
package io.devhub.apilocalservicehelper.commandservice;

import io.devhub.apilocalservicehelper.security.InputValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/commands")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:8080"},
             maxAge = 3600)
public class CommandController {
    private static final Logger logger = LoggerFactory.getLogger(CommandController.class);
    private final CommandService service;

    public CommandController(CommandService service) {
        this.service = service;
    }

    /**
     * Get all commands
     * @return List of all commands
     */
    @GetMapping
    public ResponseEntity<List<Command>> getAll() {
        List<Command> commands = service.list();
        return ResponseEntity.ok(commands);
    }

    /**
     * Get command by ID
     * @param id Command ID
     * @return Command details or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            String validatedId = InputValidator.validateId(id);
            var command = service.getById(validatedId);
            if (command.isPresent()) {
                return ResponseEntity.ok(command.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Command not found"));
        } catch (InputValidator.ValidationException e) {
            logger.warn("Invalid ID format provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Invalid ID format"));
        }
    }

    /**
     * Create a new command
     * @param cmd Command to create
     * @return Created command or error if already exists
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Command cmd) {
        try {
            if (cmd == null || cmd.getId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid input: Command ID is required"));
            }

            // Validate all input fields
            String id = InputValidator.validateId(cmd.getId());
            String app = InputValidator.validateApp(cmd.getApp());
            String category = InputValidator.validateCategory(cmd.getCategory());
            String type = InputValidator.validateType(cmd.getType());
            String title = InputValidator.validateTitle(cmd.getTitle());
            String command = InputValidator.validateCommand(cmd.getCommand());
            String description = InputValidator.validateDescription(cmd.getDescription());
            Integer priority = InputValidator.validatePriority(cmd.getPriority());
            String tagsJson = InputValidator.validateTagsJson(cmd.getTagsJson());

            // Set validated values
            cmd.setId(id);
            cmd.setApp(app);
            cmd.setCategory(category);
            cmd.setType(type);
            cmd.setTitle(title);
            cmd.setCommand(command);
            cmd.setDescription(description);
            cmd.setPriority(priority);
            cmd.setTagsJson(tagsJson);

            // Check if command already exists
            if (service.getById(id).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Command already exists"));
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(service.add(cmd));
        } catch (InputValidator.ValidationException e) {
            logger.warn("Input validation failed during command creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Invalid input: " + e.getMessage()));
        }
    }

    /**
     * Update existing command
     * @param id Command ID
     * @param cmd Command data to update
     * @return Updated command or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Command cmd) {
        try {
            String validatedId = InputValidator.validateId(id);

            // Validate all input fields
            String app = InputValidator.validateApp(cmd.getApp());
            String category = InputValidator.validateCategory(cmd.getCategory());
            String type = InputValidator.validateType(cmd.getType());
            String title = InputValidator.validateTitle(cmd.getTitle());
            String command = InputValidator.validateCommand(cmd.getCommand());
            String description = InputValidator.validateDescription(cmd.getDescription());
            Integer priority = InputValidator.validatePriority(cmd.getPriority());
            String tagsJson = InputValidator.validateTagsJson(cmd.getTagsJson());

            // Set validated values
            cmd.setApp(app);
            cmd.setCategory(category);
            cmd.setType(type);
            cmd.setTitle(title);
            cmd.setCommand(command);
            cmd.setDescription(description);
            cmd.setPriority(priority);
            cmd.setTagsJson(tagsJson);

            Command updated = service.update(validatedId, cmd);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Command not found"));
        } catch (InputValidator.ValidationException e) {
            logger.warn("Input validation failed during command update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Invalid input: " + e.getMessage()));
        }
    }

    /**
     * Delete command by ID
     * @param id Command ID
     * @return Success message or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            String validatedId = InputValidator.validateId(id);
            if (service.getById(validatedId).isPresent()) {
                service.delete(validatedId);
                return ResponseEntity.ok(Map.of("message", "Command deleted successfully"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Command not found"));
        } catch (InputValidator.ValidationException e) {
            logger.warn("Invalid ID format provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Invalid ID format"));
        }
    }
}
