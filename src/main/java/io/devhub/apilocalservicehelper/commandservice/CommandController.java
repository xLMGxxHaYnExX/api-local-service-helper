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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/commands")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:8080", "*"})
public class CommandController {
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
        var command = service.getById(id);
        if (command.isPresent()) {
            return ResponseEntity.ok(command.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "Command not found: " + id));
    }

    /**
     * Create a new command
     * @param cmd Command to create
     * @return Created command or error if already exists
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Command cmd) {
        if (cmd.getId() == null || cmd.getId().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Command ID is required"));
        }

        // Check if command already exists
        if (service.getById(cmd.getId()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Command already exists: " + cmd.getId()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(service.add(cmd));
    }

    /**
     * Update existing command
     * @param id Command ID
     * @param cmd Command data to update
     * @return Updated command or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Command cmd) {
        Command updated = service.update(id, cmd);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "Command not found: " + id));
    }

    /**
     * Delete command by ID
     * @param id Command ID
     * @return Success message or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        if (service.getById(id).isPresent()) {
            service.delete(id);
            return ResponseEntity.ok(Map.of("message", "Command deleted: " + id));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "Command not found: " + id));
    }
}
