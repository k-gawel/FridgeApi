package org.california.service.getter;

import org.california.service.model.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetterService {

    public final AccountGetter accounts;
    public final PlaceGetter places;
    public final TokenService tokens;
    public final ContainerGetter containers;
    public final ItemGetter items;
    public final ItemInstanceGetter itemInstances;
    public final CategoryGetter categories;
    public final WishListGetter wishLists;
    public final WishListItemGetter wishListItems;
    public final AllergenGetter allergens;
    public final IngredientGetter ingredients;
    public final ProducerGetter producers;
    public final PlaceUserStatsGetter placeUserStats;

    @Autowired
    public GetterService(AccountGetter accounts, PlaceGetter places, TokenService tokens, ContainerGetter containers, ItemGetter items, ItemInstanceGetter itemInstances, CategoryGetter categories, WishListGetter wishLists,
                         WishListItemGetter wishListItems, AllergenGetter allergens, IngredientGetter ingredients, ProducerGetter producers, PlaceUserStatsGetter placeUserStats) {
        this.accounts = accounts;
        this.places = places;
        this.tokens = tokens;
        this.containers = containers;
        this.items = items;
        this.itemInstances = itemInstances;
        this.categories = categories;
        this.wishLists = wishLists;
        this.wishListItems = wishListItems;
        this.allergens = allergens;
        this.ingredients = ingredients;
        this.producers = producers;
        this.placeUserStats = placeUserStats;
    }
}
