package cn.itcast.core.service.template;

import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.page.PageResult;
import cn.itcast.core.pojo.template.TypeTemplate;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService {
    PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate) throws Exception;

    void add(TypeTemplate typeTemplate) throws Exception;

    TypeTemplate findOne(Long id) throws Exception;

    List<Map> findBySpecList(Long id) throws Exception;
}
