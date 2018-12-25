//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.elepy.docs.settings;

import com.elepy.dao.Crud;
import com.elepy.dao.Page;
import com.elepy.exceptions.ElepyException;
import com.elepy.routes.DefaultFind;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.commons.io.IOUtils;
import spark.Request;
import spark.Response;

public class SettingsFind extends DefaultFind<Setting> {
    public SettingsFind() {
    }

    public void find(Request request, Response response, Crud<Setting> settingsDao, ObjectMapper objectMapper) {
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
                setting.setValue(this.getSettingValue(setting.getId(), settingsDao));
                settings.add(setting);
            });
            response.body(objectMapper.writeValueAsString(new Page(1L, 1L, settings)));
            response.status(200);
        } catch (Exception var7) {
            var7.printStackTrace();
            throw new ElepyException(var7.getMessage());
        }
    }

    public String getSettingValue(String id, Crud<Setting> dao) {
        Optional<Setting> byId = dao.getById(id);
        return (String)byId.map(Setting::getValue).orElse("");
    }
}
