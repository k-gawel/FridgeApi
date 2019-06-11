package org.california.service.model;

import org.california.model.entity.Account;
import org.california.model.entity.Container;
import org.california.model.entity.Place;
import org.california.model.transfer.request.ContainerForm;
import org.california.model.transfer.response.PlaceUserStats;
import org.california.model.util.KeyValue;
import org.california.repository.container.ContainerRepository;
import org.california.service.getter.GetterService;
import org.california.util.exceptions.ForbiddentException;
import org.california.util.exceptions.NotValidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ContainerService {


    private GetterService getterService;
    private ContainerRepository containerRepository;
    private PlaceService placeService;

    @Autowired
    public ContainerService(GetterService getterService, ContainerRepository containerRepository, PlaceService placeService) {
        this.getterService = getterService;
        this.containerRepository = containerRepository;
        this.placeService = placeService;
    }


    public Container createNewContainer(Place place, Account account, String newContainerName) {

        if(place == null || account == null || newContainerName == null) throw new ForbiddentException();

        if(!place.getAdmin().equals(account)) throw new ForbiddentException();

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

        if(!containerForm.parse())
            throw new NotValidException("Container form not valid");

        long placeId = containerForm.getPlaceId();
        Place place = getterService.places.getByKey(placeId);

        return createNewContainer(place, account, containerForm.getName());
    }


    public Collection<PlaceUserStats> getUsersStats(Collection<Account> accounts, Collection<Container> containers) {

        if(accounts == null || containers == null || accounts.isEmpty() || containers.isEmpty())
            return Collections.emptySet();

        List<PlaceUserStats> result = new ArrayList<>();
        accounts.forEach(a -> result.add(new PlaceUserStats(a.getId(), 0, 0, 0)));

        Collection<KeyValue<Account, Long>> addStats = containerRepository.getAddedInstancesStats(accounts, containers);
        Collection<KeyValue<Account, Long>> openStats = containerRepository.getOpenedInstancesStats(accounts, containers);
        Collection<KeyValue<Account, Long>> deleteStats = containerRepository.getDeletedInstancesStats(accounts, containers);

        result.forEach(e -> {
            addStats.forEach(a -> { if(a.getKey().getId().equals(e.getUserId())) e.setInstancesAdded(a.getValue()); } );
            openStats.forEach(a -> { if(a.getKey().getId().equals(e.getUserId())) e.setInstancesOpened(a.getValue()); } );
            deleteStats.forEach(a -> { if(a.getKey().getId().equals(e.getUserId())) e.setInstancesDeleted(a.getValue()); } );
        });

        return result;
    }

}
