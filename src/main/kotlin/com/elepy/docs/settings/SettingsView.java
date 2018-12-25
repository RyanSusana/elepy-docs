//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.elepy.docs.settings;

import com.elepy.admin.ElepyAdminPanel;
import com.elepy.admin.concepts.ResourceView;
import com.elepy.docs.Frontend;
import com.mongodb.DB;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsView extends ResourceView {
    SettingsDao settingsDao;

    public SettingsView() {
    }

    public void setup(ElepyAdminPanel elepyAdminPanel) {
        this.settingsDao = new SettingsDao((DB) elepyAdminPanel.elepy().getSingleton(DB.class));
    }

    public String renderView(Map<String, Object> map) {
        try {
            List<String> strings1 = IOUtils.readLines(this.getClass().getClassLoader().getResourceAsStream("settings.elepy"), StandardCharsets.UTF_8);
            ArrayList<Setting> settings = new ArrayList();
            strings1.forEach((s) -> {
                String[] split = s.split(":");
                Setting setting = new Setting();
                setting.setName(split[0]);
                setting.setId(split[1]);
                setting.setDefaultValue(split[2]);
                setting.setDescription(split[3]);
                setting.setType(SettingType.valueOf(split[4]));
                setting.setValue(this.settingsDao.getSettingValue(setting.getId()));
                settings.add(setting);
            });
            HashMap<String, Object> model = new HashMap();
            model.put("settings", settings);
            return Frontend.Companion.compile("settings.peb", model);
        } catch (IOException var5) {
            var5.printStackTrace();
            return "<h1>Unable to load Settings.</h1>";
        }
    }

    public String renderHeaders() {
        return "";
    }
}
