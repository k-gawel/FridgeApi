package org.california.controller.service;

import org.california.controller.service.utils.Utils;
import org.california.model.entity.Account;
import org.california.model.entity.Place;
import org.california.model.entity.WishList;
import org.california.model.transfer.request.WishListForm;
import org.california.service.builders.EntityToDtoMapper;
import org.california.model.transfer.response.WishListDto;
import org.california.service.getter.GetterService;
import org.california.service.model.AccountPermissionsService;
import org.california.service.model.WishListService;
import org.california.util.exceptions.NotValidException;
import org.california.util.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class WishListControllerService {

    private GetterService getterService;
    private AccountPermissionsService accountPermissionsService;
    private WishListService wishListService;
    private final EntityToDtoMapper mapper;


    @Autowired
    public WishListControllerService(GetterService getterService, AccountPermissionsService accountPermissionsService, WishListService wishListService, EntityToDtoMapper mapper) {
        this.getterService = getterService;
        this.accountPermissionsService = accountPermissionsService;
        this.wishListService = wishListService;
        this.mapper = mapper;
    }


    public WishListDto newWishList(String token, WishListForm form) {
        Place place = getterService.places.getByKey(form.place);
        Account account = getterService.accounts.getByToken(token);

        if(!accountPermissionsService.hasAccessToPlace(account, place))
            throw new UnauthorizedException();

        WishList result = wishListService.create(form);
        return mapper.toDto(result);
    }


    public Collection<WishListDto> get(String token, String placeIdsString, String wishListIdsString, boolean active) {

        Collection<Long> wishListIds = wishListIdsString.equals("") ? null : Utils.collectionOf(wishListIdsString);
        Collection<Long> placeIds = placeIdsString.equals("") ? null : Utils.collectionOf(placeIdsString);
        Account account = getterService.accounts.getByToken(token);

        Collection<WishList> result;


        if(wishListIds != null)
            result = getterService.wishLists.get(wishListIds, true);
        else if (placeIds != null)
            result = getterService.wishLists.get(getterService.places.getByIds(placeIds), active);
        else if(account != null)
            result = getterService.wishLists.get(account.getPlaces(), active);
        else
            return Collections.emptySet();


        return result.stream()
                .filter(w -> accountPermissionsService.hasAccessToWishList(account, w))
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }


}
