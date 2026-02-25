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
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CommandService {
    private final CommandRepository repo;

    public CommandService(CommandRepository repo) {
        this.repo = repo;
    }

    /**
     * Get all commands
     */
    public List<Command> list() {
        return repo.findAll();
    }

    /**
     * Get command by ID
     */
    public Optional<Command> getById(String id) throws InputValidator.ValidationException {
        String validatedId = InputValidator.validateId(id);
        return repo.findById(validatedId);
    }

    /**
     * Add new command
     */
    public Command add(Command c) throws InputValidator.ValidationException {
        if (c == null) {
            throw new InputValidator.ValidationException("Command cannot be null");
        }

        // Validate all fields
        c.setId(InputValidator.validateId(c.getId()));
        c.setApp(InputValidator.validateApp(c.getApp()));
        c.setCategory(InputValidator.validateCategory(c.getCategory()));
        c.setType(InputValidator.validateType(c.getType()));
        c.setTitle(InputValidator.validateTitle(c.getTitle()));
        c.setCommand(InputValidator.validateCommand(c.getCommand()));
        c.setDescription(InputValidator.validateDescription(c.getDescription()));
        c.setPriority(InputValidator.validatePriority(c.getPriority()));
        c.setTagsJson(InputValidator.validateTagsJson(c.getTagsJson()));

        return repo.save(c);
    }

    /**
     * Update existing command
     */
    public Command update(String id, Command c) throws InputValidator.ValidationException {
        if (c == null) {
            throw new InputValidator.ValidationException("Command cannot be null");
        }

        String validatedId = InputValidator.validateId(id);

        if (repo.existsById(validatedId)) {
            // Validate all fields
            c.setId(validatedId);
            c.setApp(InputValidator.validateApp(c.getApp()));
            c.setCategory(InputValidator.validateCategory(c.getCategory()));
            c.setType(InputValidator.validateType(c.getType()));
            c.setTitle(InputValidator.validateTitle(c.getTitle()));
            c.setCommand(InputValidator.validateCommand(c.getCommand()));
            c.setDescription(InputValidator.validateDescription(c.getDescription()));
            c.setPriority(InputValidator.validatePriority(c.getPriority()));
            c.setTagsJson(InputValidator.validateTagsJson(c.getTagsJson()));

            return repo.save(c);
        }
        return null;
    }

    /**
     * Delete command by ID
     */
    public void delete(String id) throws InputValidator.ValidationException {
        String validatedId = InputValidator.validateId(id);
        repo.deleteById(validatedId);
    }
}
