package org.california.service.model;

import org.california.model.entity.Account;
import org.california.model.entity.Container;
import org.california.model.entity.Place;
import org.california.model.transfer.request.ContainerForm;
import org.california.model.transfer.response.PlaceUserStats;
import org.california.repository.container.ContainerRepository;
import org.california.service.getter.GetterService;
import org.california.util.exceptions.ForbiddentException;
import org.california.util.exceptions.NotValidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContainerService {


    private final GetterService getterService;
    private final ContainerRepository containerRepository;
    private final PlaceService placeService;

    @Autowired
    public ContainerService(GetterService getterService, ContainerRepository containerRepository, PlaceService placeService) {
        this.getterService = getterService;
        this.containerRepository = containerRepository;
        this.placeService = placeService;
    }


    public Container createNewContainer(Place place, Account account, String newContainerName) {
        if(place == null || account == null || newContainerName == null) throw new ForbiddentException();

        if(!place.getAdmin().equals(account))
            throw new ForbiddentException();

        Container container = new Container();
        container.setName(newContainerName);
        container.setDate(new Date());
        container.setPlace(place);

        containerRepository.save(container);

        place.getContainers().add(container);

        placeService.save(place);

        return container;
    }


    public Container createNewContainer(Account account, ContainerForm containerForm) {
        if(!containerForm.validate())
            throw new NotValidException("Container form not valid");

        long placeId = containerForm.getPlaceId();
        Place place = getterService.places.getByKey(placeId);

        return createNewContainer(place, account, containerForm.getName());
    }


    public Collection<PlaceUserStats> getUsersStats(Collection<Account> accounts, Collection<Container> containers) {
        if(CollectionUtils.isEmpty(accounts) || CollectionUtils.isEmpty(containers))
            return Collections.emptySet();

        Map<Account, Long> added   = containerRepository.getAddedInstancesStats(accounts, containers);
        Map<Account, Long> opened  = containerRepository.getOpenedInstancesStats(accounts, containers);
        Map<Account, Long> deleted = containerRepository.getDeletedInstancesStats(accounts, containers);


        return accounts.stream()
                .map(a -> new PlaceUserStats(a.getId(), added.get(a), opened.get(a), deleted.get(a)))
                .collect(Collectors.toList());
    }


}