package cn.itcast.core.service.ad;

import cn.itcast.core.pojo.ad.ContentCategory;
import cn.itcast.core.pojo.page.PageResult;

import java.util.List;

public interface ContentCategoryService {

    ContentCategory findOne(Long id) throws Exception;

    List<ContentCategory> findAll() throws Exception;

    PageResult findPage(Integer page, Integer rows, ContentCategory contentCategory) throws Exception;

    void add(ContentCategory contentCategory) throws Exception;

    void update(ContentCategory contentCategory) throws Exception;

    void delete(Long[] ids) throws Exception;

}
