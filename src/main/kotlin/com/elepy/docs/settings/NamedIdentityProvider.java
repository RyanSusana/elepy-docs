package com.elepy.docs.settings;

import com.elepy.concepts.IdentityProvider;
import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;

import java.util.Optional;

public class NamedIdentityProvider<T extends Nameable> implements IdentityProvider<T> {
    public NamedIdentityProvider() {
    }

    public String getId(T nameable, Crud<T> crud) {
        Optional<T> byId = crud.getById(nameable.getName());
        if (byId.isPresent()) {
            throw new ElepyException("This already exists. Delete or edit instead");
        } else {
            return nameable.getName();
        }
    }
}
