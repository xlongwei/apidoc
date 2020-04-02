package com.dev.doc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dev.base.mybatis.dao.BaseMybatisDao;
import com.dev.doc.entity.InterCase;

public interface InterCaseDao extends BaseMybatisDao<InterCase, Long> {

	List<InterCase> listAllByInterId(@Param("interId")Long interId);
}
