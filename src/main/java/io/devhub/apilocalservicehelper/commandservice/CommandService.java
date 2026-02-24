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
    public Optional<Command> getById(String id) {
        return repo.findById(id);
    }

    /**
     * Add new command
     */
    public Command add(Command c) {
        return repo.save(c);
    }

    /**
     * Update existing command
     */
    public Command update(String id, Command c) {
        if (repo.existsById(id)) {
            c.setId(id);
            return repo.save(c);
        }
        return null;
    }

    /**
     * Delete command by ID
     */
    public void delete(String id) {
        repo.deleteById(id);
    }
}
