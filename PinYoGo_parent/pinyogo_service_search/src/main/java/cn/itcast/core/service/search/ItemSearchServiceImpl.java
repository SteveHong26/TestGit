package cn.itcast.core.service.search;

import cn.itcast.core.pojo.item.Item;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.sun.source.tree.NewClassTree;
import org.apache.activemq.console.filter.QueryFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;


import javax.annotation.Resource;
import java.util.*;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Resource
    private SolrTemplate solrTemplate;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Map<String, Object> search(Map<String, String> searchMap) throws Exception {
        //创建一个Map集合,用来存储搜索的数据
        Map<String, Object> resultMap = new HashMap<>();
        String keywords = searchMap.get("keywords");
        if (keywords != null && !"".equals(keywords)) {
            keywords = keywords.replace(" ", "");
            searchMap.put("keywords",keywords);
        }
        Map<String, Object> map = searchForHighlightPage(searchMap);
        resultMap.putAll(map);
            //商品分类表
        List<String> categoryList = searchForGroupPage(searchMap);
        if (categoryList != null && categoryList.size() > 0) {

            //商品品牌以及规格列表查询
            Map<String, Object> brandAndSpecMap = searchBrandAndSpecListByCategory(categoryList.get(0));
            //将返回的结果集添加到map集合当中
            resultMap.putAll(brandAndSpecMap);

            resultMap.put("categoryList",categoryList);
        }

        return resultMap;

    }

    private Map<String,Object> searchBrandAndSpecListByCategory(String category) {
        //获取缓存中的分类ID获取到模板ID
        Object typeId = redisTemplate.boundHashOps("itemCat").get(category);
        //通过模板ID获取品牌
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
        //通过模板ID获取规格
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
        //将获取到的两个集合添加到刚才创建的Map集合当中
        //创建一个Map集合存储要返回的值
        Map<String, Object> brandAndSpecMap = new HashMap<>();
        brandAndSpecMap.put("brandList", brandList);
        brandAndSpecMap.put("specList", specList);
        //return
        return brandAndSpecMap;
    }

    private List<String> searchForGroupPage(Map<String, String> searchMap) {
        //设置关键字
        String keywords = searchMap.get("keywords");
        Criteria criteria = new Criteria("item_keywords");
        if (keywords != null && !"".equals(keywords)) {
            //模糊查询
            criteria.is(keywords);
        }

        //创建Query查询对象
        SimpleQuery query = new SimpleQuery(criteria);
        //设置分组
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category"); //根据某个字段进行分组
        //对封装好了条件的分组对象set注入到query查询对象内
        query.setGroupOptions(groupOptions);

        //使用solrTemplate对象调用queryForGroupPage进行分页设置
        GroupPage<Item> groupPage = solrTemplate.queryForGroupPage(query, Item.class);
        //获取分组结果,创建一个集合存储分组结果
        List<String> categoryList = new ArrayList<>();
        //获取分页对象分页后的结果集
        GroupResult<Item> groupResult = groupPage.getGroupResult("item_category");
        //遍历
        Page<GroupEntry<Item>> groupEntries = groupResult.getGroupEntries();
        //遍历并将结果集封装到list集合内,并返回
        for (GroupEntry<Item> groupEntry : groupEntries) {
            String groupValue = groupEntry.getGroupValue();
            categoryList.add(groupValue);
        }
        return categoryList;
    }

    private Map<String, Object> searchForHighlightPage(Map<String, String> searchMap) {
        //获取关键字
        String keywords = searchMap.get("keywords");
        //封装条件,传递字段item_keywords
        Criteria criteria = new Criteria("item_keywords");
        if (keywords != null && !"".equals(keywords)) {
            criteria.is(keywords); //这个相当于模糊查询
        }
        SimpleHighlightQuery query = new SimpleHighlightQuery(criteria);
        //设置分页
        //获取当前页数
        Integer pageNo = Integer.valueOf(searchMap.get("pageNo").toString());

        //获取每页的条数
        Integer pageSize = Integer.valueOf(searchMap.get("pageSize").toString());

        //计算出起始页
        Integer offSet = (pageNo - 1) * pageSize;

        //设置起始页
        query.setOffset(offSet);

        //设置每页的条数
        query.setRows(pageSize);
        //关键字高亮
        HighlightOptions highLight = new HighlightOptions();
        //设置高亮的字段名
        highLight.addField("item_title");
        //设置高亮开始标签
        highLight.setSimplePrefix("<font color='red'>");
        //设置高亮的结束标签
        highLight.setSimplePostfix("</font>");
        //将设置好的高亮条件封装到 SimpleHighlightQuery 对象中
        query.setHighlightOptions(highLight);

        // 添加过滤条件


        //商品分类之条件封装
        String category = searchMap.get("category");
        if(category != null && !"".equals(category)){
            Criteria caCri = new Criteria("item_category");
            caCri.is(category);
            SimpleFilterQuery caFQ = new SimpleFilterQuery(caCri);
            query.addFilterQuery(caFQ);
        }
        //商品品牌之条件封装
        String brand = searchMap.get("brand");
        if (brand != null && !"".equals(brand)) {
            Criteria brCri = new Criteria("item_brand");
            brCri.is(brand);
            SimpleFilterQuery brFQ = new SimpleFilterQuery(brCri);
            query.addFilterQuery(brFQ);
        }
        //商品规格之条件封装
        String spec = searchMap.get("spec");
        if (spec != null && !"".equals(spec)) {
            Map<String,String> map = JSON.parseObject(spec, Map.class);
            Set<Map.Entry<String, String>> entrySet = map.entrySet();
            for (Map.Entry<String, String> stringEntry : entrySet) {
                Criteria spCri = new Criteria("item_spec_"+stringEntry.getKey());
                spCri.is(stringEntry.getValue());
                SimpleFilterQuery spFQ = new SimpleFilterQuery(spCri);
                query.addFilterQuery(spFQ);
            }
        }
        //商品价格之条件封装
        String price = searchMap.get("price");
        if (price != null && !"".equals(price)) {
            String[] priceArray = price.split("-");
            Criteria prCri = new Criteria("item_price");
            prCri.between(priceArray[0], priceArray[1], true,true);
            SimpleFilterQuery prFQ = new SimpleFilterQuery(prCri);
            query.addFilterQuery(prFQ);
        }
        //结果排序: 新品
        String s = searchMap.get("sort");
        Sort sort = null;
        if (s != null && !"".equals(s)) {
            if ("ASC".equals(s)) {
                sort = new Sort(Sort.Direction.ASC, "item_" + searchMap.get("sortField"));
            }else{
                sort = new Sort(Sort.Direction.DESC, "item_" + searchMap.get("sortField"));
            }
        }
        query.addSort(sort);

        HighlightPage<Item> highlightPage = solrTemplate.queryForHighlightPage(query, Item.class);
        //处理结果集
        //HighlightPage继承自 Page<T>
        List<HighlightEntry<Item>> highlighted = highlightPage.getHighlighted();
        if (highlighted != null && highlighted.size() > 0) {
            for (HighlightEntry<Item> highlightEntry : highlighted) {
                Item item = highlightEntry.getEntity();
                List<HighlightEntry.Highlight> highlight = highlightEntry.getHighlights();
                //高亮结果集
                if (highlight != null && highlight.size() > 0) {
                    for (HighlightEntry.Highlight h : highlight) {
                        item.setTitle(h.getSnipplets().get(0));
                    }
                }
            }
        }
        // 将结果封装到Map当中
        Map<String, Object> map = new HashMap<>();
        map.put("totalPages", highlightPage.getTotalPages());
        map.put("total", highlightPage.getTotalElements());
        map.put("rows", highlightPage.getContent());

        return map;

    }

    private Map<String, Object> searchForPage(Map<String, String> searchMap) {
        //获取关键字
        String keywords = searchMap.get("keywords");
        //封装条件,传递字段item_keywords
        Criteria criteria = new Criteria("item_keywords");
        if (keywords != null && !"".equals(keywords)) {
            criteria.is(keywords); //这个相当于模糊查询
        }
        SimpleQuery query = new SimpleQuery(criteria);

        //设置分页
        //获取当前页数
        Integer pageNo = Integer.valueOf(searchMap.get("pageNo").toString());

        //获取每页的条数
        Integer pageSize = Integer.valueOf(searchMap.get("pageSize").toString());

        //计算出起始页
        Integer offSet = (pageNo - 1) * pageSize;

        //设置起始页
        query.setOffset(offSet);

        //设置每页的条数
        query.setRows(pageSize);

        ScoredPage<Item> items = solrTemplate.queryForPage(query, Item.class);

        // 二 讲结果集封装到Map当中

        Map<String, Object> map = new HashMap<>();
        map.put("totalPages",items.getTotalPages());
        map.put("total", items.getTotalElements());
        //添加结果集
        map.put("rows", items.getContent());
        return map;

    }
}
