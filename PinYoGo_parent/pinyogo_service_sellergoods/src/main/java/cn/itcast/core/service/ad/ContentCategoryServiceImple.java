package cn.itcast.core.service.ad;

import cn.itcast.core.dao.ad.ContentCategoryDao;
import cn.itcast.core.pojo.ad.ContentCategory;
import cn.itcast.core.pojo.page.PageResult;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
@Service
public class ContentCategoryServiceImple implements ContentCategoryService {

    @Autowired
    private ContentCategoryDao contentCategoryDao;



    @Override
    public ContentCategory findOne(Long id) throws Exception {

        return contentCategoryDao.selectByPrimaryKey(id);
    }

    @Override
    public List<ContentCategory> findAll() throws Exception {

        return contentCategoryDao.selectByExample(null);
    }

    @Override
    public PageResult findPage(Integer page, Integer rows, ContentCategory contentCategory) throws Exception {
        PageHelper.startPage(page,rows);
        Page<ContentCategory> p = (Page<ContentCategory>) contentCategoryDao.selectByExample(null);
        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    @Transactional
    public void add(ContentCategory contentCategory) throws Exception {
        contentCategoryDao.insertSelective(contentCategory);
    }

    @Override
    @Transactional
    public void update(ContentCategory contentCategory) throws Exception {
        contentCategoryDao.updateByPrimaryKeySelective(contentCategory);
    }

    @Override
    @Transactional
    public void delete(Long[] ids) throws Exception {

        contentCategoryDao.deleteByPrimaryKeys(ids);
    }



}
