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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CommandServiceTest {

	@Autowired
	private CommandRepository commandRepository;

	@Autowired
	private CommandService commandService;

	private Command testCommand;
	private Command testCommand2;

	@BeforeEach
	void setUp() {
		// Clean up before each test
		commandRepository.deleteAll();

		// Create test commands
		testCommand = new Command(
			"test-git-clone",
			"Git",
			"VCS",
			"command",
			"Clone repository",
			"git clone <repo_url>",
			"Clone a remote repository",
			10,
			"[\"git\",\"vcs\"]"
		);

		testCommand2 = new Command(
			"test-maven-build",
			"Maven",
			"Build",
			"command",
			"Maven Build",
			"mvn clean package",
			"Build project with Maven",
			9,
			"[\"maven\",\"build\"]"
		);
	}

	@Test
	@DisplayName("Create command successfully")
	void testCreateCommand() throws Exception {
		// Test creating a new command
		Command saved = commandService.add(testCommand);
		assertNotNull(saved);
		assertEquals("test-git-clone", saved.getId());
		assertEquals("Git", saved.getApp());
		assertEquals("Clone repository", saved.getTitle());
	}

	@Test
	@DisplayName("Get command by ID successfully")
	void testGetCommandById() throws Exception {
		// Save command first
		commandService.add(testCommand);

		// Retrieve it
		Optional<Command> retrieved = commandService.getById("test-git-clone");
		assertTrue(retrieved.isPresent());
		assertEquals("Clone repository", retrieved.get().getTitle());
		assertEquals("git clone <repo_url>", retrieved.get().getCommand());
	}

	@Test
	@DisplayName("Get non-existent command returns empty")
	void testGetNonExistentCommand() throws Exception {
		Optional<Command> retrieved = commandService.getById("non-existent-id");
		assertTrue(retrieved.isEmpty());
	}

	@Test
	@DisplayName("Get all commands returns list")
	void testGetAllCommands() throws Exception {
		// Add multiple commands
		commandService.add(testCommand);
		commandService.add(testCommand2);

		// Retrieve all
		List<Command> commands = commandService.list();
		assertEquals(2, commands.size());
	}

	@Test
	@DisplayName("Get all commands returns empty list when no commands")
	void testGetAllCommandsEmpty() throws Exception {
		List<Command> commands = commandService.list();
		assertEquals(0, commands.size());
	}

	@Test
	@DisplayName("Update command successfully")
	void testUpdateCommand() throws Exception {
		// Create and save
		commandService.add(testCommand);

		// Update
		testCommand.setTitle("UpdatedTitle");
		testCommand.setPriority(15);
		Command updated = commandService.update("test-git-clone", testCommand);

		assertNotNull(updated);
		assertEquals("UpdatedTitle", updated.getTitle());
		assertEquals(15, updated.getPriority());
	}

	@Test
	@DisplayName("Update non-existent command returns null")
	void testUpdateNonExistentCommand() throws Exception {
		Command updated = commandService.update("non-existent-id", testCommand);
		assertNull(updated);
	}

	@Test
	@DisplayName("Delete command successfully")
	void testDeleteCommand() throws Exception {
		// Save command
		commandService.add(testCommand);
		assertTrue(commandService.getById("test-git-clone").isPresent());

		// Delete
		commandService.delete("test-git-clone");
		assertTrue(commandService.getById("test-git-clone").isEmpty());
	}

	@Test
	@DisplayName("Delete non-existent command does not throw error")
	void testDeleteNonExistentCommand() throws Exception {
		// Should not throw exception
		assertDoesNotThrow(() -> commandService.delete("non-existent-id"));
	}
}

