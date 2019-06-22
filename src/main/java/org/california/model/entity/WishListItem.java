package org.california.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.california.model.entity.item.Category;
import org.california.model.entity.item.Item;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@Getter @Setter @ToString
public class WishListItem extends BaseEntity {

    private Date createdOn;

    @ManyToOne @JoinColumn
    private Account author;

    @ManyToOne
    private WishList wishList;


    private Date addedOn;

    @ManyToOne @JoinColumn
    private ItemInstance addedInstance;

    @ManyToOne @JoinColumn
    private Account addedBy;


    private String comment;

    @ManyToOne @JoinColumn
    private Category category;

    @ManyToOne @JoinColumn
    private Item item;

}
