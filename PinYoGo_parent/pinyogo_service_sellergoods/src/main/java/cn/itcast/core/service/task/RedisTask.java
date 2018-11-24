package cn.itcast.core.service.task;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class RedisTask {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private ItemCatDao itemCatDao;



    @Resource
    private TypeTemplateDao typeTemplateDao;


    @Resource
    private SpecificationOptionDao specificationOptionDao;

    @Scheduled(cron = "00 43 16 * * ?")
    public void autoCachingForItemCat() {
        List<ItemCat> itemCatList = itemCatDao.selectByExample(null);
        if (itemCatList != null && itemCatList.size() > 0) {
            for (ItemCat itemCat : itemCatList) {
                redisTemplate.boundHashOps("itemCat").put(itemCat.getName(), itemCat.getTypeId());
            }
            System.out.println("将商品分类自动同步到了Redis当中!");
       }

    }

    @Scheduled(cron = "00 44 16 * * ?")
    public void autoCachingForTypeTemplate() throws Exception {
        List<TypeTemplate> typeTemplateList = typeTemplateDao.selectByExample(null);
        if (typeTemplateList != null && typeTemplateList.size() > 0) {
            for (TypeTemplate template : typeTemplateList) {
                String brandIds = template.getBrandIds();
                List<Map> brandList = JSON.parseArray(brandIds, Map.class);
                redisTemplate.boundHashOps("brandList").put(template.getId(),brandList);
                List<Map> specList = findBySpecList(template.getId());
                redisTemplate.boundHashOps("specList").put(template.getId(),specList);
            }
            System.out.println("将模板自动同步到了Reids当中!");
        }


    }
    public List<Map> findBySpecList(Long id) throws Exception {
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        String specIds = typeTemplate.getSpecIds();
        List<Map> list = JSON.parseArray(specIds, Map.class);
        for (Map map : list) {
            Long specId = Long.parseLong(map.get("id").toString());
            SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
            specificationOptionQuery.createCriteria().andSpecIdEqualTo(specId);
            List<SpecificationOption> specificationOptions = specificationOptionDao.selectByExample(specificationOptionQuery);
            map.put("options", specificationOptions);
        }

        return list;
    }

}
