package org.fjsei.yewu.entity.sdn;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

//import org.springframework.data.annotation.Id; 不是这个；


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="T_EBM_USER")
public class TEbmUser {

  @Id
  @GeneratedValue
  @Column(name="USER_ID")
  private Long userId;  //自增id　NUMBER

    private String status;
    private String userType;
    private String loginName;
    private String userName;
    private String password;

  private String type;
  private String portal;
  private String certcode;

    //如果不需要根据Address反向级联查询People，可以注释掉
    //双向的1 ：1关系，关系是TSdnEnp类来维护；
    @OneToOne(mappedBy = "tebmuser")
    private TSdnEnp  tsdnenp;    //JPA版本升級 大小寫敏感 tSdnEnp


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


  public String getPortal() {
    return portal;
  }

  public void setPortal(String portal) {
    this.portal = portal;
  }


  public String getCertcode() {
    return certcode;
  }

  public void setCertcode(String certcode) {
    this.certcode = certcode;
  }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

}

