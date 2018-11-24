package cn.itcast.core.service.ad;

import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.page.PageResult;

import java.util.List;


public interface ContentService {

    PageResult findPage(Integer page,Integer rows,Content content) throws Exception;

    Content findOne(Long id) throws Exception;

    void add(Content content) throws Exception;

    List<Content> findAll() throws Exception;

    void update(Content content) throws Exception;

    void delete(Long[] ids) throws Exception;

    List<Content> findByCategoryId(Long categoryId) throws Exception;


}
