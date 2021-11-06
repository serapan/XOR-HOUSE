package gr.ntua.ece.softeng18b.repositories;

//import java.util.Optional;

import gr.ntua.ece.softeng18b.model.Info2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
//import gr.ntua.ece.softeng18b.model.Product;
//import gr.ntua.ece.softeng18b.model.Store;


public interface Info2Repository extends JpaRepository<Info2, Integer>
{
    @Query(value = "SELECT * FROM info2 WHERE date = ?1 AND sku = ?2 AND store_id = ?3", nativeQuery = true)
    List<Info2> getInfo2ByDate(Date date, Long sku, Long store_id);

   /* @Query(value = "DELETE FROM info WHERE sku = ?1", nativeQuery = true)
    void deleteInfoBySku(Long id);*/
}