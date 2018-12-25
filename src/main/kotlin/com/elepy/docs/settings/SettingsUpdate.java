//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.elepy.docs.settings;

import com.elepy.Elepy;
import com.elepy.concepts.AtomicIntegrityEvaluator;
import com.elepy.concepts.IntegrityEvaluatorImpl;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyErrorMessage;
import com.elepy.exceptions.ElepyException;
import com.elepy.routes.DefaultUpdate;
import com.elepy.utils.ClassUtils;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import spark.Request;
import spark.Response;

public class SettingsUpdate extends DefaultUpdate<Setting> {
    public SettingsUpdate() {
    }

    public void handle(Request request, Response response, Crud<Setting> dao, Elepy elepy, List<ObjectEvaluator<Setting>> objectEvaluators, Class<Setting> clazz) throws Exception {
        String body = request.body();
        ObjectMapper objectMapper = elepy.getObjectMapper();

        try {
            try {
                JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, dao.getType());
                List<Setting> ts = (List)objectMapper.readValue(body, type);
                this.multipleCreate(ts, dao, objectEvaluators);
                response.status(200);
                response.body("OK");
            } catch (JsonMappingException var12) {
                Setting item = (Setting)objectMapper.readValue(body, dao.getType());
                this.defaultCreate(item, dao, objectMapper, objectEvaluators);
                response.status(200);
                response.body("OK");
            }

        } catch (Exception var13) {
            var13.printStackTrace();
            throw new ElepyException(var13.getMessage());
        }
    }

    protected Optional<Setting> defaultCreate(Setting product, Crud<Setting> dao, ObjectMapper objectMapper, List<ObjectEvaluator<Setting>> objectEvaluators) throws Exception {
        Iterator var5 = objectEvaluators.iterator();

        while(var5.hasNext()) {
            ObjectEvaluator<Setting> objectEvaluator = (ObjectEvaluator)var5.next();
            objectEvaluator.evaluate(product, Setting.class);
        }

        (new IntegrityEvaluatorImpl()).evaluate(product, dao);
        dao.create(product);
        return Optional.ofNullable(product);
    }

    protected Optional<Iterable<Setting>> multipleCreate(Iterable<Setting> items, Crud<Setting> dao, List<ObjectEvaluator<Setting>> objectEvaluators) throws Exception {
        if (ClassUtils.hasIntegrityRules(dao.getType())) {
            (new AtomicIntegrityEvaluator()).evaluate(Lists.newArrayList(Iterables.toArray(items, dao.getType())));
        }

        Iterator var4 = items.iterator();

        while(var4.hasNext()) {
            Setting item = (Setting)var4.next();
            Iterator var6 = objectEvaluators.iterator();

            while(var6.hasNext()) {
                ObjectEvaluator<Setting> objectEvaluator = (ObjectEvaluator)var6.next();
                objectEvaluator.evaluate(item, Setting.class);
            }

            (new IntegrityEvaluatorImpl()).evaluate(item, dao);
        }

        System.out.println("Updatein");
        dao.update(items);
        return Optional.of(items);
    }
}
