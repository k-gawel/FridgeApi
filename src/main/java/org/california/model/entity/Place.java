package org.california.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @ToString
public class Place extends BaseEntity {


    @Column(nullable = false)
    private String name;


    @LazyCollection(value = LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "place")
    private Collection<Container> containers = new HashSet<>();


    @OneToOne
    @JoinColumn(nullable = false, name = "admin_id")
    private Account admin;


    @LazyCollection(value = LazyCollectionOption.FALSE)
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(joinColumns        = @JoinColumn(name = "place_id"),
               inverseJoinColumns = @JoinColumn(name = "account_id"))
    private Set<Account> accounts = new HashSet<>();


    @LazyCollection(value              = LazyCollectionOption.FALSE)
    @ManyToMany(    cascade            = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(     joinColumns        = @JoinColumn(name = "place_id"),
                    inverseJoinColumns = @JoinColumn(name = "account_id"))
    private Set<Account> unaactiveAccounts = new HashSet<>();


    private Date createdOn = new Date();


}
