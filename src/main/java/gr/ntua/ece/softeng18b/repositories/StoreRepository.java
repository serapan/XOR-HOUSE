package gr.ntua.ece.softeng18b.repositories;

import java.util.List;
import java.util.Optional;

//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import gr.ntua.ece.softeng18b.model.Store;
//import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

//import javax.transaction.Transactional;

//import javax.persistence.EntityManager;


public interface StoreRepository extends JpaRepository<Store, Integer>
{
    Optional<Store> findById(Long id);
    /*
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM store WHERE store_id = ?1", nativeQuery = true)
    void removeById(Long id);
    */

    List<Store> findByName(String name);

    @Query(value = "SELECT COUNT(*) FROM store", nativeQuery = true)
    int howMany();

    @Query(value = "SELECT s.* FROM store s, info i WHERE i.store_id = s.store_id and i.sku = ?1", nativeQuery = true)
    List<Store> findStoresByProductId(Long productId);

    @Query(value = "SELECT * FROM store ORDER BY CASE WHEN ?3 = 'id|ASC' THEN store_id ELSE NULL END ASC, CASE WHEN ?3 = 'id|DESC' THEN store_id ELSE NULL END DESC, CASE WHEN ?3 = 'name|ASC' THEN name ELSE NULL END ASC, CASE WHEN ?3 = 'name|DESC' THEN name ELSE NULL END DESC LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Store> findAllStores(Integer start, Integer count, String sort);

    @Query(value = "SELECT * FROM store WHERE withdrawn = ?4 ORDER BY CASE WHEN ?3 = 'id|ASC' THEN store_id ELSE NULL END ASC, CASE WHEN ?3 = 'id|DESC' THEN store_id ELSE NULL END DESC, CASE WHEN ?3 = 'name|ASC' THEN name ELSE NULL END ASC, CASE WHEN ?3 = 'name|DESC' THEN name ELSE NULL END DESC LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Store> findStores(Integer start, Integer count, String sort, boolean withdrawn);

    @Query(value = "SELECT * FROM store distanceSphericalLaw(?5, ?6, store.lat, store.lng) < ?7 ORDER BY CASE WHEN ?3 = 'id|ASC' THEN store_id ELSE NULL END ASC, CASE WHEN ?3 = 'id|DESC' THEN store_id ELSE NULL END DESC, CASE WHEN ?3 = 'name|ASC' THEN name ELSE NULL END ASC, CASE WHEN ?3 = 'name|DESC' THEN name ELSE NULL END DESC LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Store> findAllStoresDist(Integer start, Integer count, String sort, Double lat, Double lng, Double dist);

    @Query(value = "SELECT * FROM store WHERE distanceSphericalLaw(?5, ?6, store.lat, store.lng) < ?7 and withdrawn = ?4 ORDER BY CASE WHEN ?3 = 'id|ASC' THEN store_id ELSE NULL END ASC, CASE WHEN ?3 = 'id|DESC' THEN store_id ELSE NULL END DESC, CASE WHEN ?3 = 'name|ASC' THEN name ELSE NULL END ASC, CASE WHEN ?3 = 'name|DESC' THEN name ELSE NULL END DESC LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Store> findStoresDist(Integer start, Integer count, String sort, boolean withdrawn, Double lat, Double lng, Double dist);

    @Query(value = "select * from store where lat = ?1 and lng = ?2 limit 1", nativeQuery = true)
    Optional<Store> findByCoordinates(Double lat, Double lng);
}