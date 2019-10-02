package org.california.controller.service;

import org.california.model.entity.item.Ingredient;
import org.california.model.transfer.response.item.IngredientDto;
import org.california.service.builders.EntityToDtoMapper;
import org.california.service.getter.GetterService;
import org.california.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class IngredientControllerService {

    private GetterService getter;
    private EntityToDtoMapper mapper;

    @Autowired
    public IngredientControllerService(GetterService getter, EntityToDtoMapper mapper) {
        this.getter = getter;
        this.mapper = mapper;
    }


    public Collection<IngredientDto> search(String idsString, String name, String nameStart) {
        Collection<Number> ids = StringUtils.collectionOf(idsString);

        Collection<Ingredient> resultList;

        if(!ids.isEmpty())
            resultList = getter.ingredients.getByKeys(ids);
        else if(org.apache.commons.lang3.StringUtils.isNotBlank(name))
            resultList = getter.ingredients.searchByName(name);
        else if(!org.apache.commons.lang3.StringUtils.isNotBlank(nameStart))
            resultList = getter.ingredients.getWhereNameStartsWith(nameStart);
        else
            return Collections.emptySet();

        return resultList.stream().map(mapper::toDto).collect(Collectors.toList());
    }


}
