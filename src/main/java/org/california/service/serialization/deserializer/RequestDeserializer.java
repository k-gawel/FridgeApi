package org.california.service.serialization.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.california.model.transfer.request.forms.Form;
import org.california.service.serialization.ConstructorGetter;
import org.california.service.serialization.JSONMapper;
import org.california.service.serialization.annotations.ById;
import org.california.service.serialization.annotations.ByIdProcessor;
import org.california.service.serialization.annotations.ByIds;
import org.california.service.serialization.annotations.ByIdsProcessor;
import org.california.util.exceptions.NotValidException;
import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;


public class RequestDeserializer<T> extends StdDeserializer<T> {

    private ObjectMapper formMapper = JSONMapper.MAPPER;
    private ObjectMapper mapper     = new ObjectMapper();

    private Class<T> valueClass;


    public RequestDeserializer(Class<T> vc) {
        super(vc);
        this.valueClass = vc;
    }


    @Override
    public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        Object[] parameters = getParametersArray(node);
        Constructor<T> constructor = new ConstructorGetter(_valueClass).getConstructor();

        try {
            return constructor.newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }


    private Object[] getParametersArray(JsonNode node) {
        Field[]  fields = valueClass.getDeclaredFields();
        Object[] result = new Object[fields.length];

        for (int i = 0; i < fields.length; i++) {
            Object value = getValue(node, fields[i]);
            result[i] = value;
        }

        return result;
    }


    private Object getValue(JsonNode node, Field field) {
        String nodeName = field.getName();
        JsonNode value  = node.get(nodeName);

        if(value == null)
            return null;
        if(field.isAnnotationPresent(ById.class))
            return new ByIdProcessor(value.asLong(), field).getValue();
        else if(field.isAnnotationPresent(ByIds.class))
            return new ByIdsProcessor(getIds(value), field).getValues();
        else
            return getPrimitiveValue(value, field);
    }


    @Contract("_, null -> null")
    private Object getPrimitiveValue(JsonNode value, Field field) {
        if (value == null) return null;

        Class<?> fieldClass = field.getType();
        ObjectMapper mapper = Form.class.isAssignableFrom(fieldClass) ? formMapper : this.mapper;


        try {
            return mapper.readValue(value.toString(), fieldClass);
        } catch (IOException e) {
            throw new NotValidException("Cannot cast " + value.toString() + " to " + field.getType());
        }
    }


    @Contract("null -> null")
    private Collection<Number> getIds(JsonNode node) {
        if(node == null) return null;

        Collection<Number> result = new ArrayList<>();

        for(final JsonNode number : node) {
            if(number.canConvertToLong())
                result.add(number.asLong());
            else
                result.add(null);
        }

        return result;
    }

}
