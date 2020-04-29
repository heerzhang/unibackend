package md.julienne;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

//JPA的核心，数据库装入,到对应的实体类去。

public interface RecipeRepository extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {


 //   List<EqpMge> findAllByCreatedByEquals(User user);


}

