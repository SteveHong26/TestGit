package cn.itcast.core.service.template;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;

import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import cn.itcast.core.pojo.page.PageResult;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import com.alibaba.fastjson.JSON;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;


@Service
public class TypeTemplateImpl implements TypeTemplateService {


    @Autowired
    private TypeTemplateDao typeTemplateDao;

    @Autowired
    private SpecificationOptionDao specificationOptionDao;


    @Autowired
    private BrandDao brandDao;


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate) throws Exception {

        List<TypeTemplate> typeTemplateList = typeTemplateDao.selectByExample(null);
        if (typeTemplateList != null && typeTemplateList.size() > 0) {

            for (TypeTemplate template : typeTemplateList) {
                String brandIds = template.getBrandIds();
                List<Map> brandList = JSON.parseArray(brandIds, Map.class);
                redisTemplate.boundHashOps("brandList").put(template.getId(),brandList);
                List<Map> specList = findBySpecList(template.getId());
                redisTemplate.boundHashOps("specList").put(template.getId(), specList);
            }
        }

        PageHelper.startPage(page,rows);
        TypeTemplateQuery typeTemplateQuery = new TypeTemplateQuery();
        if (typeTemplate.getName() != null && !"".equals(typeTemplate.getName().trim())) {
            typeTemplateQuery.createCriteria().andNameLike("%"+typeTemplate.getName().trim()+"%");
        }
        PageHelper.orderBy("id desc");

        Page<TypeTemplate> p =(Page<TypeTemplate>)typeTemplateDao.selectByExample(typeTemplateQuery);

        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public void add(TypeTemplate typeTemplate) throws Exception {
        typeTemplateDao.insertSelective(typeTemplate);
    }

    @Override
    public TypeTemplate findOne(Long id) throws Exception {

        return typeTemplateDao.selectByPrimaryKey(id);
    }


    @Override
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
