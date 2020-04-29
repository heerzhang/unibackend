package md.computer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import md.system.User;
import md.specialEqp.Report;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "FILES")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "filesSeq")
    @SequenceGenerator(name = "filesSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_FILES")
    protected Long id;

    //实际的文件系统的路径名
    @Column(length =512)
    private String  url;

    //纳入用户系统控制。
    @ManyToOne
    @JoinColumn
    private User creator;

    private boolean anyoneCanSee=false;

    //报告=原始记录的附带图片等的文件
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Report report;

}

