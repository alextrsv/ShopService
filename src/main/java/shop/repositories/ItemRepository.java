package shop.repositories;

import shop.entities.Item;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {

  List<Item> findByName(@Param(value = "name") final String name);

  @Query("select it from Item it where it.type = 'OFFER'")
  List<Item> getAllOffers();

}
