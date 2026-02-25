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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CommandTest {

	private Command cmd1;
	private Command cmd2;
	private Command cmd3;

	@BeforeEach
	void setUp() {
		cmd1 = new Command("id1", "App", "Cat", "type", "title", "cmd", "desc", 1, "tags");
		cmd2 = new Command("id1", "App2", "Cat2", "type2", "title2", "cmd2", "desc2", 2, "tags2");
		cmd3 = new Command("id2", "App", "Cat", "type", "title", "cmd", "desc", 1, "tags");
	}

	@Test
	@DisplayName("Command equality based on ID only")
	void testCommandEquality() {
		// Same ID should be equal
		assertEquals(cmd1, cmd2);
		assertNotEquals(cmd1, cmd3);
		assertEquals(cmd1.hashCode(), cmd2.hashCode());
	}

	@Test
	@DisplayName("Command getters and setters work correctly")
	void testCommandGettersSetters() {
		Command cmd = new Command();

		cmd.setId("test-id");
		cmd.setApp("TestApp");
		cmd.setCategory("TestCat");
		cmd.setType("command");
		cmd.setTitle("Test Title");
		cmd.setCommand("test command");
		cmd.setDescription("Test Description");
		cmd.setPriority(5);
		cmd.setTagsJson("[\"tag1\",\"tag2\"]");

		assertEquals("test-id", cmd.getId());
		assertEquals("TestApp", cmd.getApp());
		assertEquals("TestCat", cmd.getCategory());
		assertEquals("command", cmd.getType());
		assertEquals("Test Title", cmd.getTitle());
		assertEquals("test command", cmd.getCommand());
		assertEquals("Test Description", cmd.getDescription());
		assertEquals(5, cmd.getPriority());
		assertEquals("[\"tag1\",\"tag2\"]", cmd.getTagsJson());
	}

	@Test
	@DisplayName("Command default constructor works")
	void testCommandDefaultConstructor() {
		Command cmd = new Command();
		assertNull(cmd.getId());
		assertNull(cmd.getApp());
		assertNull(cmd.getCategory());
	}

	@Test
	@DisplayName("Command full constructor initializes all fields")
	void testCommandFullConstructor() {
		Command cmd = new Command("id1", "App", "Cat", "type", "title", "cmd", "desc", 1, "tags");

		assertEquals("id1", cmd.getId());
		assertEquals("App", cmd.getApp());
		assertEquals("Cat", cmd.getCategory());
		assertEquals("type", cmd.getType());
		assertEquals("title", cmd.getTitle());
		assertEquals("cmd", cmd.getCommand());
		assertEquals("desc", cmd.getDescription());
		assertEquals(1, cmd.getPriority());
		assertEquals("tags", cmd.getTagsJson());
	}
}

