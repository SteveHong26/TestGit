package cn.itcast.core.service.specification;

import cn.itcast.core.pojo.page.PageResult;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.vo.SpecificationVo;

import java.util.List;
import java.util.Map;

public interface SpecificationService {
    List<Specification> findAll() throws Exception;

    PageResult findPage(Integer page, Integer rows) throws Exception;

    PageResult search(Integer page, Integer rows, Specification specification) throws Exception;

    void delete(Long[] ids) throws Exception;

    void update(SpecificationVo specificationVo) throws Exception;

    void add(SpecificationVo specificationVo) throws Exception;

    SpecificationVo findOne(Long id) throws Exception;

    List<Map<String, String>> findSpecList() throws Exception;

}
