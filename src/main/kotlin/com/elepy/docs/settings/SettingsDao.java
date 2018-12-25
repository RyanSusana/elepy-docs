//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.elepy.docs.settings;

import com.elepy.dao.Page;
import com.elepy.dao.jongo.MongoDao;
import com.google.common.collect.Lists;
import com.mongodb.DB;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SettingsDao extends MongoDao<Setting> {
    public SettingsDao(DB db) {
        super(db, "settings", Setting.class, new NamedIdentityProvider());
    }

    public String getSettingValue(String id) {
        Optional<Setting> byId = this.getById(id);
        return (String) byId.map(Setting::getValue).orElse("");
    }

    public List<Setting> getByType(SettingType type) {
        return Lists.newArrayList(this.collection().find("{type: #}", new Object[]{type}).as(Setting.class).iterator());
    }

    public Page<Setting> get() {
        List<String> strings1 = new ArrayList();
        ArrayList<Setting> settings = new ArrayList();

        try {
            strings1 = IOUtils.readLines(this.getClass().getClassLoader().getResourceAsStream("settings.elepy"), StandardCharsets.UTF_8);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        strings1.forEach((s) -> {


            String[] split = s.split(":");
            Setting setting = new Setting();
            setting.setName(split[0]);
            setting.setId(split[1]);
            setting.setDefaultValue(split[2]);
            setting.setDescription(split[3]);
            setting.setType(SettingType.valueOf(split[4]));
            setting.setValue(this.getSettingValue(setting.getId()));
            settings.add(setting);
        });
        return new Page<>(1L, 1L, settings);
    }

    public void update(Iterable<Setting> items) {
        Iterator var2 = items.iterator();

        while (var2.hasNext()) {
            Setting item = (Setting) var2.next();
            this.collection().save(item);
        }

    }

    public Map<String, String> toMap() {
        Map<String, String> settingMap = new HashMap();
        this.get().getValues().forEach((setting) -> {
            String value = setting.getValue().trim().isEmpty() ? setting.getDefaultValue() : setting.getValue();
            settingMap.put(setting.getId(), value);
        });
        return settingMap;
    }
}
