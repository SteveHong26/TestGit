package cn.itcast.core.service.item;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.service.page.ItemPageService;
import com.alibaba.dubbo.config.annotation.Service;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${htmlDir}")
    private String htmlDir;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private GoodsDescDao goodsDescDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private ItemCatDao itemCatDao;


    @Override
    public Boolean getItemHTML(Long goodsId) {
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        try {

            Template template = configuration.getTemplate("item.ftl");
            Map goodsData = new HashMap();
            //获取根据商品ID查询到的商品对象
            Goods goods = goodsDao.selectByPrimaryKey(goodsId);
            //查询商品描述信息
            GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(goodsId);
            goodsData.put("goods",goods);
            goodsData.put("goodsDesc", goodsDesc);
            ItemCat itemCat1 = itemCatDao.selectByPrimaryKey(goods.getCategory1Id());
            ItemCat itemCat2 = itemCatDao.selectByPrimaryKey(goods.getCategory2Id());
            ItemCat itemCat3 = itemCatDao.selectByPrimaryKey(goods.getCategory3Id());

            goodsData.put("itemCat1",itemCat1);
            goodsData.put("itemCat2",itemCat2);
            goodsData.put("itemCat3",itemCat3);


            //查询商品条目信息
            ItemQuery itemQuery = new ItemQuery();
            ItemQuery.Criteria criteria = itemQuery.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId).andStatusEqualTo("1");
            itemQuery.setOrderByClause("is_default desc");
            List<Item> itemList = itemDao.selectByExample(itemQuery);
            goodsData.put("itemList",itemList);
            Writer out = new OutputStreamWriter
                    (new FileOutputStream(htmlDir + goodsId + ".html"), "UTF-8");
            template.process(goodsData,out);
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return false;
    }
}
