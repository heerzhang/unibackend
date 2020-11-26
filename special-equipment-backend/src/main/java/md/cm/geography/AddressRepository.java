package md.cm.geography;
import md.specialEqp.Eqp;
import org.fjsei.yewu.jpa.QuerydslNcExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AddressRepository extends JpaRepository<Address, Long>, JpaSpecificationExecutor<Address>, QuerydslNcExecutor<Address> {
    Address findByName(String address);         //address=用户自定义的，后半部份的。
}
