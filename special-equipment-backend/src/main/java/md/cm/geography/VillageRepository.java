package md.cm.geography;
import org.fjsei.yewu.entity.fjtj.DictArea;
import org.fjsei.yewu.jpa.QuerydslNcExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VillageRepository extends JpaRepository<Village, Long>, JpaSpecificationExecutor<Village>, QuerydslNcExecutor<Village> {
}