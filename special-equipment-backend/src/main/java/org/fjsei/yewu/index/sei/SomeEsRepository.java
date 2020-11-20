package org.fjsei.yewu.index.sei;


import md.specialEqp.Eqp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.util.Streamable;

import java.util.stream.Stream;


public interface SomeEsRepository extends ElasticsearchRepository<SomeEs, Long> {

}

