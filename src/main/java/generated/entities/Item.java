package generated.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import generated.entities.dto.ItemDTO;
import generated.entities.enums.ItemType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Audited(withModifiedFlag = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "item")
public class Item {
  @Id
  protected UUID id;
  
  @Column(name = "name")
  protected String name;
  
  @Column(name = "type")
  private ItemType type;
  
  @Column(name = "price")
  protected Integer price;

  @Column(name = "date")
  private String date;

  @NotAudited
  @JsonIgnore
  @Column(name = "child_offer_amount")
  private Integer childOfferAmount;

//  @NotAudited
  @JsonIgnore
  @AuditMappedBy(mappedBy = "children")
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="parent_id", foreignKey=@ForeignKey(name = "FK_PARENT_ID"))
  protected Item parent;

  @Transient
  UUID parentId;

//  @NotAudited
  @AuditMappedBy(mappedBy = "parent")
  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "parent_id")
  private List<Item> children;

  
  public Item(final ItemDTO itemDTO) {
    this.id = itemDTO.getId();
    this.name = itemDTO.getName();
    this.type = ItemType.valueOf(itemDTO.getType());
    this.price = itemDTO.getPrice();
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ItemType getType() {
    return type;
  }

  public void setType(ItemType type) {
    this.type = type;
  }

  public Integer getPrice() {
    if (this.type.equals(ItemType.CATEGORY) && this.price != null && this.price != 0
            && this.childOfferAmount != null && this.childOfferAmount != 0 )
      return price/childOfferAmount;
    else return price;
  }

  @JsonIgnore
  public Integer getWholePrice(){
    return this.price;
  }

  public void setPrice(Integer price) {
    this.price = price;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public Integer getChildOfferAmount() {
    return childOfferAmount;
  }

  public void setChildOfferAmount(Integer childOfferAmount) {
    this.childOfferAmount = childOfferAmount;
  }

  public Item getParent() {
    return parent;
  }

  public void setParent(Item parent) {
    this.parent = parent;
  }

  public UUID getParentId() {
    if (this.parent != null)
      return this.parent.getId();
    return null;
  }

  public void setParentId(UUID parentId) {
    this.parentId = parentId;
  }

  public List<Item> getChildren() {
    if (this.type.equals(ItemType.OFFER) && children.size() == 0)
      children = null;
    return children;
  }

  public void setChildren(List<Item> children) {
    this.children = children;
  }
}
