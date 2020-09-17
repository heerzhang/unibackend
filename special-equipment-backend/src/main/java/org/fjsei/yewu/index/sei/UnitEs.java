package org.fjsei.yewu.index.sei;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import javax.persistence.*;


@Document(indexName = "unit")
@Data
@NoArgsConstructor
@Setting(settingPath = "es-config/elastic-analyzer.json")
public class UnitEs {
    @Id
    protected Long id;
    //根据单词来切分的，匹配某个词就能看见。　中文不行是单个汉字就要切分的=匹配太多了。
    @Field(type = FieldType.Text, analyzer = "autocomplete_index", searchAnalyzer = "autocomplete_search")
    private String name;        //企业或机构名，人名，楼盘称谓。
    private String address;     //详细住址，首要办公地点，楼盘地址。
    private String linkMen;     //负责管理人的个人名字
    private String phone;       //消息联系的主要手机号
}

