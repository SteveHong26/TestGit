package cn.itcast.core.service.itemCat;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private ItemCatDao itemCatDao;


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public List<ItemCat> findAll() throws Exception {

        return itemCatDao.selectByExample(null);
    }

    @Override
    public List<ItemCat> findByParentId(Long parentId) {

        List<ItemCat> itemCatList = itemCatDao.selectByExample(null);
        if (itemCatList != null && itemCatList.size() > 0) {
            for (ItemCat itemCat : itemCatList) {
                redisTemplate.boundHashOps("itemCat").put(itemCat.getName(), itemCat.getTypeId());
            }

        }
        //设置查询的条件
        ItemCatQuery itemCatQuery = new ItemCatQuery();
        itemCatQuery.createCriteria().andParentIdEqualTo(parentId);
        List<ItemCat> itemCatList1 = itemCatDao.selectByExample(itemCatQuery);
        return itemCatList1;
    }

    @Override
    public ItemCat findOne(Long id) throws Exception {

        return itemCatDao.selectByPrimaryKey(id);
    }
}
