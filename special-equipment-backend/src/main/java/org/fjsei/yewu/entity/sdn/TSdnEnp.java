package org.fjsei.yewu.entity.sdn;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
///不能用 import org.springframework.data.annotation.Id;


/* @SecondaryTables({
        @SecondaryTable(name = "Address"),
}) */


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="T_Sdn_Enp")
public class TSdnEnp {

  @Id
  @GeneratedValue
  @Column(name="ENP_ID")
  private Long enpId; //自增id，　实际数据不唯一

  private String status;

  @Column(name="unt_Name")
  private String untName;
 private String certSerial;

    private String untLkman;
    @Column(name="lkman_Mobil", length=20)
    private String lkmanMobil;
    private String untCod;
    private java.sql.Date createDate;

  private String createUserId;
  private String aptitudeFileId;
  private String oldCertSerial;
    //1:1关联T_EBM_USER.userId
    //1 ：1关系，关系是本类来维护，添加外键指向对方实体表的主键；
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")
    private TEbmUser tebmuser;      //tEbmUser
    //[特殊] 反向查询需要：因为对方关联的不是我方的id；
    @Column(name="USER_ID",updatable=false,insertable=false)
    private Long userId;

    public Long getEnpId() {
    return enpId;
  }

  public void setEnpId(Long enpId) {
    this.enpId = enpId;
  }

    public String getCertSerial() {
        return certSerial;
    }

    public void setCertSerial(String certSerial) {
        this.certSerial = certSerial;
    }

  public String getCreateUserId() {
    return createUserId;
  }

  public void setCreateUserId(String createUserId) {
    this.createUserId = createUserId;
  }

  public String getAptitudeFileId() {
    return aptitudeFileId;
  }

  public void setAptitudeFileId(String aptitudeFileId) {
    this.aptitudeFileId = aptitudeFileId;
  }

  public String getOldCertSerial() {
    return oldCertSerial;
  }

  public void setOldCertSerial(String oldCertSerial) {
    this.oldCertSerial = oldCertSerial;
  }


    public java.sql.Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(java.sql.Date createDate) {
        this.createDate = createDate;
    }

    public String getUntLkman() {
        return untLkman;
    }

    public void setUntLkman(String untLkman) {
        this.untLkman = untLkman;
    }


    public String getLkmanMobil() {
        return lkmanMobil;
    }

    public void setLkmanMobil(String lkmanMobil) {
        this.lkmanMobil = lkmanMobil;
    }

    public String getUntCod() {
        return untCod;
    }

    public void setUntCod(String untCod) {
        this.untCod = untCod;
    }
  public String getUntName() {
    return untName;
  }

  public void setUntName(String untName) {
    this.untName = untName;
  }

}

