package gr.ntua.ece.softeng18b.repositories;

//import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gr.ntua.ece.softeng18b.model.Info;

import java.util.List;
import java.util.Map;
//import gr.ntua.ece.softeng18b.model.Product;
//import gr.ntua.ece.softeng18b.model.Store;


public interface InfoRepository extends JpaRepository<Info, Integer>
{
    @Query(value = "SELECT COUNT(*) FROM info WHERE sku = ?1 AND store_id = ?2 AND ( (date_from BETWEEN ?3 AND ?4) OR (date_to BETWEEN ?3 AND ?4) OR (date_from <= ?3 AND date_to >= ?4))", nativeQuery=true)
    int findHowManyLikeThis(Long productId, Long storeId, java.sql.Date sqlDateFrom, java.sql.Date sqlDateTo);

    @Query(value = "select info.store_id, store.name, (count(*) / (select count(*) from info) * 100) as percentage\n" +
            "from info, store\n" +
            "where store.store_id = info.store_id\n" +
            "group by store_id", nativeQuery = true)
    List<Map<String, Object>>findPercentagesPerStore();

    @Query(value = "select COUNT(sku) as counter\n" +
            "from (select store_id, sku from info group by store_id,sku) as A\n" +
            "where A.store_id = ?1\n" +
            "group by store_id", nativeQuery = true)
    List<Map<String, Object>>findNumProductsOfStore(Long store_id);

    @Query(value = "select D.store_id, store.name, D.counter from\n" +
            "(select store_id, count(*) as counter from\n" +
            "(select B.sku, info.store_id from\n" +
            "(select A.sku, min(info.price) as MinPrice\n" +
            "from (select store_id, sku, MAX(date_from) as MaxDate\n" +
            "from info\n" +
            "where info.date_from < curdate()\n" +
            "group by sku,store_id) as A, info\n" +
            "where info.store_id = A.store_id and info.sku = A.sku and info.date_from = A.MaxDate\n" +
            "group by A.sku) as B, info\n" +
            "where info.sku = B.sku and info.price = B.MinPrice) as C\n" +
            "group by C.store_id\n" +
            "order by counter desc limit 10) as D, store\n" +
            "where D.store_id = store.store_id", nativeQuery = true)
    List<Map<String, Object>>findThreeFirstStores();

    @Query(value = "select D.store_id, store.name, store.address, D.counter from\n" +
            "(select store_id, count(*) as counter from\n" +
            "(select B.sku, info.store_id from\n" +
            "(select A.sku, min(info.price) as MinPrice\n" +
            "from (select store_id, sku, MAX(date_from) as MaxDate\n" +
            "from info\n" +
            "where info.date_from < curdate()\n" +
            "group by sku,store_id) as A, info\n" +
            "where info.store_id = A.store_id and info.sku = A.sku and info.date_from = A.MaxDate\n" +
            "group by A.sku) as B, info\n" +
            "where info.sku = B.sku and info.price = B.MinPrice) as C\n" +
            "group by C.store_id\n" +
            "order by counter desc limit 10) as D, store\n" +
            "where D.store_id = store.store_id", nativeQuery = true)
    List<Map<String, Object>>findThreeFirstStores2();


    @Query(value = "select product.category, COUNT(*) as counter\n" +
            "from (select store_id, sku from info group by store_id,sku) as A, product\n" +
            "where A.store_id = ?1 and A.sku = product.sku\n" +
            "group by store_id, product.category", nativeQuery = true)
    List<Map<String, Object>>findNumProductsOfStorePerCategory(Long store_id);
   /* @Query(value = "DELETE FROM info WHERE sku = ?1", nativeQuery = true)
    void deleteInfoBySku(Long id);*/
}