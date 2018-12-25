//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.elepy.docs.settings;

import com.elepy.Elepy;
import com.elepy.dao.Crud;
import com.elepy.dao.CrudProvider;
import com.mongodb.DB;

public class SettingsDaoProvider implements CrudProvider {
    public SettingsDaoProvider() {
    }

    public Crud<Setting> crudFor(Class aClass, Elepy elepy) {
        return new SettingsDao((DB)elepy.getSingleton(DB.class));
    }
}
