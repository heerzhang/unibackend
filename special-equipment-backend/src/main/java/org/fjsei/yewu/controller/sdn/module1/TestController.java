package org.fjsei.yewu.controller.sdn.module1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class TestController {

	@Qualifier("seiJdbcTemplate")
	@Autowired
	protected JdbcTemplate fooJdbcTemplate;

	@Qualifier("sdnJdbcTemplate")
	@Autowired
	protected JdbcTemplate barJdbcTemplate;

	//一个端口endpoit有多个URL同名映射，冲突？
	@RequestMapping("/")
	public String root() {
		return "redirect:/index";
	}

	@Transactional
	@RequestMapping("/test")
	public void test() {
		System.out.println("begin.....");
		//fooJdbcTemplate.execute("insert into test(id) values(103)");
		//barJdbcTemplate.execute("insert into person(id) values(215)");

		System.out.println("end.....");
	}
}

