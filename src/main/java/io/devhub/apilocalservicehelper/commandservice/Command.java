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

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "commands")
public class Command {
    @Id
    private String id;
    private String app;
    private String category;
    private String type;
    private String title;
    @Column(columnDefinition = "CLOB")
    private String command;
    @Column(columnDefinition = "CLOB")
    private String description;
    private Integer priority;
    @Column(name = "tags_json", columnDefinition = "CLOB")
    private String tagsJson;

    // Constructors
    public Command() {}

    public Command(String id, String app, String category, String type, String title,
                   String command, String description, Integer priority, String tagsJson) {
        this.id = id;
        this.app = app;
        this.category = category;
        this.type = type;
        this.title = title;
        this.command = command;
        this.description = description;
        this.priority = priority;
        this.tagsJson = tagsJson;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getApp() { return app; }
    public void setApp(String app) { this.app = app; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public String getTagsJson() { return tagsJson; }
    public void setTagsJson(String tagsJson) { this.tagsJson = tagsJson; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command command1 = (Command) o;
        return Objects.equals(id, command1.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
