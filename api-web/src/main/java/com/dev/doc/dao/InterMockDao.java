package com.dev.doc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dev.base.mybatis.dao.BaseMybatisDao;
import com.dev.doc.entity.InterMock;

public interface InterMockDao extends BaseMybatisDao<InterMock, Long> {

	List<InterMock> listAllByInterId(@Param("interId")Long interId);
}
