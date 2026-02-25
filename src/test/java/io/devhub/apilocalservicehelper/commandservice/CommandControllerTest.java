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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CommandControllerTest {

	@Autowired
	private CommandRepository commandRepository;

	@Autowired
	private CommandService commandService;

	@Autowired
	private CommandController commandController;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

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

	@Nested
	@DisplayName("HTTP Endpoint Tests - MockMvc")
	class HttpEndpointTests {

		@Test
		@DisplayName("GET /api/commands returns all commands with 200 status")
		void testGetAllCommandsEndpoint() throws Exception {
			// Add test data
			commandService.add(testCommand);
			commandService.add(testCommand2);

			// Test GET all
			mockMvc.perform(get("/api/commands")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(2));
		}

		@Test
		@DisplayName("GET /api/commands returns empty array when no commands")
		void testGetAllCommandsEndpointEmpty() throws Exception {
			mockMvc.perform(get("/api/commands")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(0));
		}

		@Test
		@DisplayName("GET /api/commands/{id} returns command with 200 status")
		void testGetCommandByIdEndpoint() throws Exception {
			// Add test data
			commandService.add(testCommand);

			// Test GET by ID
			mockMvc.perform(get("/api/commands/test-git-clone")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("test-git-clone"))
				.andExpect(jsonPath("$.title").value("Clone repository"))
				.andExpect(jsonPath("$.app").value("Git"));
		}

		@Test
		@DisplayName("GET /api/commands/{id} returns 404 for non-existent command")
		void testGetCommandByIdNotFoundEndpoint() throws Exception {
			mockMvc.perform(get("/api/commands/non-existent-id")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Command not found"));
		}

		@Test
		@DisplayName("POST /api/commands creates new command with 201 status")
		void testCreateCommandEndpoint() throws Exception {
			String commandJson = objectMapper.writeValueAsString(testCommand);

			mockMvc.perform(post("/api/commands")
				.contentType(MediaType.APPLICATION_JSON)
				.content(commandJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value("test-git-clone"))
				.andExpect(jsonPath("$.title").value("Clone repository"));
		}

		@Test
		@DisplayName("POST /api/commands rejects command with empty ID with 400 status")
		void testCreateCommandWithEmptyIdEndpoint() throws Exception {
			Command invalidCommand = new Command("", "Git", "VCS", "command", "Title", "cmd", "desc", 1, "tags");
			String commandJson = objectMapper.writeValueAsString(invalidCommand);

			mockMvc.perform(post("/api/commands")
				.contentType(MediaType.APPLICATION_JSON)
				.content(commandJson))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("POST /api/commands rejects command with null ID with 400 status")
		void testCreateCommandWithNullIdEndpoint() throws Exception {
			Command invalidCommand = new Command(null, "Git", "VCS", "command", "Title", "cmd", "desc", 1, "tags");
			String commandJson = objectMapper.writeValueAsString(invalidCommand);

			mockMvc.perform(post("/api/commands")
				.contentType(MediaType.APPLICATION_JSON)
				.content(commandJson))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("POST /api/commands rejects duplicate command with 409 status")
		void testCreateDuplicateCommandEndpoint() throws Exception {
			// Create first command
			commandService.add(testCommand);

			// Try to create duplicate
			String commandJson = objectMapper.writeValueAsString(testCommand);
			mockMvc.perform(post("/api/commands")
				.contentType(MediaType.APPLICATION_JSON)
				.content(commandJson))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.error").value("Command already exists"));
		}

		@Test
		@DisplayName("PUT /api/commands/{id} updates command with 200 status")
		void testUpdateCommandEndpoint() throws Exception {
			// Create initial command
			commandService.add(testCommand);

			// Update it
			testCommand.setTitle("UpdatedTitle");
			testCommand.setPriority(20);
			String updatedJson = objectMapper.writeValueAsString(testCommand);

			mockMvc.perform(put("/api/commands/test-git-clone")
				.contentType(MediaType.APPLICATION_JSON)
				.content(updatedJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("UpdatedTitle"))
				.andExpect(jsonPath("$.priority").value(20));
		}

		@Test
		@DisplayName("PUT /api/commands/{id} returns 404 for non-existent command")
		void testUpdateNonExistentCommandEndpoint() throws Exception {
			String commandJson = objectMapper.writeValueAsString(testCommand);

			mockMvc.perform(put("/api/commands/non-existent-id")
				.contentType(MediaType.APPLICATION_JSON)
				.content(commandJson))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Command not found"));
		}

		@Test
		@DisplayName("DELETE /api/commands/{id} deletes command with 200 status")
		void testDeleteCommandEndpoint() throws Exception {
			// Create command
			commandService.add(testCommand);

			mockMvc.perform(delete("/api/commands/test-git-clone")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Command deleted successfully"));

			// Verify it's deleted
			assertTrue(commandService.getById("test-git-clone").isEmpty());
		}

		@Test
		@DisplayName("DELETE /api/commands/{id} returns 404 for non-existent command")
		void testDeleteNonExistentCommandEndpoint() throws Exception {
			mockMvc.perform(delete("/api/commands/non-existent-id")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Command not found"));
		}
	}

	@Nested
	@DisplayName("Direct Method Tests")
	class DirectMethodTests {

		@Test
		@DisplayName("Controller.getAll() returns list of commands")
		void testControllerGetAll() throws Exception {
			commandService.add(testCommand);
			commandService.add(testCommand2);

			ResponseEntity<List<Command>> response = commandController.getAll();

			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertNotNull(response.getBody());
			assertEquals(2, response.getBody().size());
		}

		@Test
		@DisplayName("Controller.getById() returns command when exists")
		void testControllerGetById() throws Exception {
			commandService.add(testCommand);

			ResponseEntity<?> response = commandController.getById("test-git-clone");

			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertNotNull(response.getBody());
			assertInstanceOf(Command.class, response.getBody());
			assertEquals("test-git-clone", ((Command) response.getBody()).getId());
		}

		@Test
		@DisplayName("Controller.getById() returns error when not found")
		void testControllerGetByIdNotFound() throws Exception {
			ResponseEntity<?> response = commandController.getById("non-existent-id");

			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
			assertNotNull(response.getBody());
			assertInstanceOf(Map.class, response.getBody());
			assertTrue(((Map<?, ?>) response.getBody()).containsKey("error"));
		}

		@Test
		@DisplayName("Controller.create() creates command successfully")
		void testControllerCreate() throws Exception {
			ResponseEntity<?> response = commandController.create(testCommand);

			assertEquals(HttpStatus.CREATED, response.getStatusCode());
			assertNotNull(response.getBody());
			assertInstanceOf(Command.class, response.getBody());
		}

		@Test
		@DisplayName("Controller.create() rejects null ID")
		void testControllerCreateNullId() {
			Command cmd = new Command(null, "App", "Cat", "type", "title", "cmd", "desc", 1, "tags");
			ResponseEntity<?> response = commandController.create(cmd);

			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
			assertInstanceOf(Map.class, response.getBody());
			assertTrue(((Map<?, ?>) response.getBody()).containsKey("error"));
		}

		@Test
		@DisplayName("Controller.create() rejects duplicate ID")
		void testControllerCreateDuplicate() throws Exception {
			commandService.add(testCommand);
			ResponseEntity<?> response = commandController.create(testCommand);

			assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
			assertInstanceOf(Map.class, response.getBody());
		}

		@Test
		@DisplayName("Controller.update() updates command successfully")
		void testControllerUpdate() throws Exception {
			commandService.add(testCommand);
			testCommand.setTitle("Updated");

			ResponseEntity<?> response = commandController.update("test-git-clone", testCommand);

			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertInstanceOf(Command.class, response.getBody());
			assertEquals("Updated", ((Command) response.getBody()).getTitle());
		}

		@Test
		@DisplayName("Controller.update() returns 404 when command not found")
		void testControllerUpdateNotFound() throws Exception {
			ResponseEntity<?> response = commandController.update("non-existent-id", testCommand);

			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
			assertInstanceOf(Map.class, response.getBody());
		}

		@Test
		@DisplayName("Controller.delete() deletes command successfully")
		void testControllerDelete() throws Exception {
			commandService.add(testCommand);

			ResponseEntity<?> response = commandController.delete("test-git-clone");

			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertInstanceOf(Map.class, response.getBody());
			assertTrue(((Map<?, ?>) response.getBody()).containsKey("message"));
		}

		@Test
		@DisplayName("Controller.delete() returns 404 when command not found")
		void testControllerDeleteNotFound() throws Exception {
			ResponseEntity<?> response = commandController.delete("non-existent-id");

			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
			assertInstanceOf(Map.class, response.getBody());
		}
	}
}

