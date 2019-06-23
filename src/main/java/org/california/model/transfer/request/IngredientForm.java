package org.california.model.transfer.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IngredientForm implements Serializable {

    public final String name;

    @JsonCreator
    public IngredientForm(String name) {
        this.name = name;
    }

}
