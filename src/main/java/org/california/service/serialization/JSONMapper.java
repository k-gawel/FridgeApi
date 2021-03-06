package org.california.service.serialization;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.classgraph.ClassGraph;
import org.california.service.serialization.deserializer.RequestDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class JSONMapper extends ObjectMapper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static Collection<Class<?>> deserializableClasses;
    public static JSONMapper MAPPER;

    public JSONMapper() {
        SimpleModule module = new SimpleModule("JSONModule", new Version(2, 0, 0, null, null, null));
        setDeserializers(module);
        registerModule(module);
        MAPPER = this;
    }


    private void setDeserializers(SimpleModule simpleModule) {
        for (Class clazz : getDeserializableClasses()) {
            System.out.println("---- SETTING DESERIALIZER FOR: " + clazz);
            simpleModule.addDeserializer(clazz, new RequestDeserializer(clazz));
        }
    }


    private Collection<Class<?>> getDeserializableClasses() {
        if (deserializableClasses == null) {
            Collection<Class<?>> dClasses = new HashSet<>();
            dClasses.addAll(getAllPackageClasses("org.california.model.transfer.request.forms"));
            dClasses.addAll(getAllPackageClasses("org.california.model.transfer.request.queries"));
            deserializableClasses = dClasses;
        }

        return deserializableClasses;
    }


    private Collection<Class<?>> getAllPackageClasses(String basePackage) {
        return new ClassGraph()
                .blacklistClasses("org.california")
                .whitelistPackages(basePackage)
                .scan()
                .getAllClasses()
                .getNames()
                .stream()
                .map(this::getClassFromName)
                .collect(Collectors.toSet());
    }


    private Class<?> getClassFromName(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
