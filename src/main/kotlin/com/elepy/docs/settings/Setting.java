//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.elepy.docs.settings;

import com.elepy.admin.annotations.View;
import com.elepy.annotations.*;
import com.elepy.models.AccessLevel;
import com.fasterxml.jackson.annotation.JsonProperty;

@RestModel(
        name = "Site Settings",
        icon = "wrench",
        slug = "/settings",
        defaultSortField = "name"
)
@Create(
        accessLevel = AccessLevel.PUBLIC
)
@Update(
        handler = SettingsUpdate.class
)
@Find(
        handler = SettingsFind.class,
        accessLevel = AccessLevel.PUBLIC
)
@View(SettingsView.class)
@DaoProvider(value = SettingsDaoProvider.class
)
public class Setting {
    @Identifier
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("value")
    private String value;
    @JsonProperty("default_value")
    private String defaultValue;
    @JsonProperty("description")
    private String description;
    @JsonProperty("type")
    private SettingType type;

    public Setting() {
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(SettingType type) {
        this.type = type;
    }

    public String getId() {
        return this.id;
    }

    public String getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public SettingType getType() {
        return this.type;
    }
}
