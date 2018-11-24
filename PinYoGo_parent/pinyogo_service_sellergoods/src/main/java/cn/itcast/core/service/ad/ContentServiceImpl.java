package cn.itcast.core.service.ad;

import cn.itcast.core.dao.ad.ContentDao;
import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.ad.ContentQuery;
import cn.itcast.core.pojo.page.PageResult;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {

    @Resource
    private ContentDao contentDao;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public PageResult findPage(Integer page, Integer rows, Content content) throws Exception {
        PageHelper.startPage(page,rows);
        Page<Content> p = (Page<Content>)contentDao.selectByExample(null);
        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public Content findOne(Long id) throws Exception {

        return contentDao.selectByPrimaryKey(id);
    }

    @Override
    @Transactional
    public void add(Content content) throws Exception {
        ClearCache(content.getCategoryId());
        contentDao.insertSelective(content);
    }

    @Override
    public List<Content> findAll() throws Exception {

        return contentDao.selectByExample(null);
    }

    @Override
    @Transactional
    public void update(Content content) throws Exception {
        //获取新的类ID
        Long newCategoryId = content.getCategoryId();
        //获取旧的类ID
        Long oldCategoryId = contentDao.selectByPrimaryKey(content.getId()).getCategoryId();
        //判断是否相同
        if(oldCategoryId != newCategoryId){
            ClearCache(oldCategoryId);
            ClearCache(newCategoryId);
        }else {
            ClearCache(oldCategoryId);
        }
        contentDao.updateByPrimaryKeySelective(content);
    }

    @Override
    @Transactional
    public void delete(Long[] ids) throws Exception {
        for (Long id : ids) {
            Content content = contentDao.selectByPrimaryKey(id);
            ClearCache(content.getCategoryId());
        }

        contentDao.deleteByPrimaryKeys(ids);
    }

    @Override
    public List<Content> findByCategoryId(Long categoryId) throws Exception {
        //先试图获取到缓存中的数据
        List<Content> list = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);

        //判断是否为空
        if(list == null){
            //面对高并发高访问,有时redis会出现缓存的穿透,这时可以通过上锁来解决问题
            synchronized (this){
                //再次尝试获取
                list = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
                //如果为空,就从数据库取
                if(list == null){
                    //根据图片的分类ID查找图片
                    ContentQuery contentQuery = new ContentQuery();
                    //绑定条件
                    contentQuery.createCriteria().andCategoryIdEqualTo(categoryId);
                    list = contentDao.selectByExample(contentQuery);
                    redisTemplate.boundHashOps("content").put(categoryId,list);

                }
            }
        }

        //反之,则直接返回
        return list;
    }

    private void ClearCache(Long categoryId) {
        redisTemplate.boundHashOps("content").delete(categoryId);
    }
}
