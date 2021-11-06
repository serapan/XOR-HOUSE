package gr.ntua.ece.softeng18b.repositories;

import java.util.List;
import java.util.Map;
import java.util.Optional;

//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import gr.ntua.ece.softeng18b.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import gr.ntua.ece.softeng18b.model.Product;
//import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

//import javax.transaction.Transactional;

//import javax.persistence.EntityManager;


public interface ProductRepository extends JpaRepository<Product, Integer>
{
    Optional<Product> findById(Long id);

    /*
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM info WHERE sku = ?1; DELETE FROM product WHERE sku = ?1", nativeQuery = true)
    void removeById(Long id);
    */

    List<Product> findByName(String name);

    List<Product> findByCategory(String category);

    @Query(value = "SELECT COUNT(*) FROM product", nativeQuery = true)
    int howMany();

    @Query(value = "SELECT * FROM product ORDER BY CASE WHEN ?3 = 'id|ASC' THEN sku ELSE NULL END ASC, CASE WHEN ?3 = 'id|DESC' THEN sku ELSE NULL END DESC, CASE WHEN ?3 = 'name|ASC' THEN model ELSE NULL END ASC, CASE WHEN ?3 = 'name|DESC' THEN model ELSE NULL END DESC LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Product> findAllProducts(Integer start, Integer count, String sort);

    @Query(value = "SELECT * FROM product WHERE withdrawn = ?4 ORDER BY CASE WHEN ?3 = 'id|ASC' THEN sku ELSE NULL END ASC, CASE WHEN ?3 = 'id|DESC' THEN sku ELSE NULL END DESC, CASE WHEN ?3 = 'name|ASC' THEN model ELSE NULL END ASC, CASE WHEN ?3 = 'name|DESC' THEN model ELSE NULL END DESC LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Product> findProducts(Integer start, Integer count, String sort, boolean withdrawn);

    @Query(value = "Select p.*, D.Minprice from\n" +
            "(Select B.sku as S, MIN(B.price) as Minprice  from\n" +
            "(Select sku, MAX(date_from) as MaxDate from info\n" +
            "    where date_from <= curdate()\n" +
            "    Group by sku) A, info B\n" +
            "where A.MaxDate = B.date_from AND\n" +
            "           A.sku = B.sku\n" +
            "Group by B.sku) AS D, product as p \n" +
            "where p.sku = D.S and p.category = ?1 LIMIT ?3 OFFSET ?2", nativeQuery = true)
    List<Map<String, Object>> findALLProductsWithPrices(String category, Integer start, Integer count);

    @Query(value = "Select p.*, D.Minprice from\n" +
            "(Select B.sku as S, MIN(B.price) as Minprice  from\n" +
            "(Select sku, MAX(date_from) as MaxDate from info\n" +
            "    where date_from <= curdate()\n" +
            "    Group by sku) A, info B\n" +
            "where A.MaxDate = B.date_from AND\n" +
            "           A.sku = B.sku\n" +
            "Group by B.sku) AS D, product as p \n" +
            "where p.sku = D.S and p.category = ?1 and p.withdrawn = ?4 LIMIT ?3 OFFSET ?2", nativeQuery = true)
    List<Map<String, Object>> findProductsWithPrices(String category, Integer start, Integer count, Boolean withdrawn);

    @Query(value = "select store.lng, store.lat, store.store_id, store.name, B.pr from\n" +
            "(select A.stid as storeid, info.price as pr from\n" +
            "(select store_id as stid, MAX(date_from) as MaxDate \n" +
            "from (select * from info where sku = ?1) as C\n" +
            "where C.date_from <= curdate()\n" +
            "group by C.store_id) as A, info\n" +
            "where info.date_from = A.MaxDate and info.store_id = A.stid and info.sku = ?1) as B, store\n" +
            "where store.store_id = B.storeid", nativeQuery = true)
    List<Map<String, Object>> findPricesForProduct(Integer sku);

    @Query(value = "select product.*, info.price from\n" +
            "(select sku, MAX(date_from) as MaxDate \n" +
            "from (select * from info where store_id = ?1) as C\n" +
            "where C.date_from <= curdate()\n" +
            "group by C.sku) as A, info, product\n" +
            "where info.store_id = ?1 and \n" +
            "A.sku = info.sku and \n" +
            "A.MaxDate = info.date_from and \n" +
            "product.sku = info.sku LIMIT ?3 OFFSET ?2", nativeQuery = true)
    List<Map<String, Object>> findAllProductsPerStore(Integer store_id, Integer start, Integer count);

    @Query(value = "select product.*, info.price from\n" +
            "(select sku, MAX(date_from) as MaxDate \n" +
            "from (select * from info where store_id = ?1) as C\n" +
            "where C.date_from <= curdate()\n" +
            "group by C.sku) as A, info, product\n" +
            "where info.store_id = ?1 and \n" +
            "A.sku = info.sku and \n" +
            "A.MaxDate = info.date_from and \n" +
            "product.sku = info.sku and product.withdrawn = ?4 LIMIT ?3 OFFSET ?2", nativeQuery = true)
    List<Map<String, Object>> findProductsPerStore(Integer store_id, Integer start, Integer count, Boolean withdrawn);
}