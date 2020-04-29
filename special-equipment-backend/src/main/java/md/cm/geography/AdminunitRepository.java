package md.cm.geography;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdminunitRepository extends JpaRepository<Adminunit, Long>, JpaSpecificationExecutor<Adminunit> {
    Adminunit findByTownIs(Town town);
}