package cn.itcast.core.service.goods;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojo.page.PageResult;
import cn.itcast.core.vo.GoodsVo;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private GoodsDescDao goodsDescDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private ItemCatDao itemCatDao;

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private SellerDao sellerDao;

    @Autowired
    private SolrTemplate solrTemplate;

    @Transactional
    @Override
    public void updateStatus(Long[] ids, String status) {
        if (ids != null && ids.length > 0) {
            Goods goods = new Goods();
            goods.setAuditStatus(status);
            for (Long id : ids) {
                goods.setId(id);
                goodsDao.updateByPrimaryKeySelective(goods);
                if ("1".equals(status)) {
                    //将全部信息保存到索引库当中
                    importDataToSolr();
                    // TODO:更新删除的状态
                    //TODO 删除索引库中的数据



                }
            }

        }
    }

    private void importDataToSolr() {
        List<Item> itemList = itemDao.selectByExample(null);
        if(itemList != null && itemList.size()>0){
            for (Item item : itemList) {
                String spec = item.getSpec();
                Map specMap = JSON.parseObject(spec, Map.class);
                item.setSpecMap(specMap);
            }
            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();
        }

    }

    /**
     * 更新商品信息
     * @apiNote  更新商品后,商品需要重新被审核,因此商品的审核状态需要更改
     * @param goodsVo
     * @throws Exception
     */
    @Transactional
    @Override
    public void update(GoodsVo goodsVo) throws Exception {
        //更新商品
        Goods goods = goodsVo.getGoods();
        goods.setAuditStatus("0");
        goodsDao.updateByPrimaryKeySelective(goods);

        //更新商品详情
        GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
        goodsDescDao.updateByPrimaryKeySelective(goodsDesc);

        /*商品项目更新, 由于商品项目中的属性多而复杂,因此选择更新并不明智
         * 因此需要将某个商品ID下的商品选项,先行删除,在插入即可实现功能
         * */
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(goods.getId());
        itemDao.deleteByExample(itemQuery);
        if ("1".equals(goods.getIsEnableSpec())) {
            //启用规格,一个商品对应多个规格
            List<Item> itemList = goodsVo.getItemList();
            if(itemList != null && itemList.size() > 0){
                for (Item item : itemList) {
                    //定义商品标题
                    String title = goods.getGoodsName() + " " + goods.getCaption();
                    //规格的数据
                    String specs = item.getSpec();
                    Map<String,String> specMap = JSON.parseObject(specs, Map.class);
                    Set<Map.Entry<String, String>> entrySet = specMap.entrySet();
                    for (Map.Entry<String, String> map : entrySet) {
                        //拼接副标题
                        title += " " + map.getValue();
                    }
                    //set副标题
                    item.setTitle(title);
                    //剩下的属性调用setAttributeForItem方法即可
                    setAttributeForItem(goods,goodsDesc,item);
                    //插入数据
                    itemDao.insertSelective(item);
                }
            }
        }else{
            //不启用规格的情况
            Item item = new Item();
            item.setTitle(goods.getGoodsName() + " " + goods.getCaption());
            item.setPrice(goods.getPrice());
            item.setIsDefault("1");
            item.setNum(9999);
            item.setSpec("{}");
            setAttributeForItem(goods,goodsDesc,item);
            itemDao.insertSelective(item);

        }

    }

    @Override
    public GoodsVo findOne(Long id) {
        GoodsVo goodsVo = new GoodsVo();
        //添加查询到的商品信息
        goodsVo.setGoods(goodsDao.selectByPrimaryKey(id));

        //添加查询道德商品详情
        goodsVo.setGoodsDesc(goodsDescDao.selectByPrimaryKey(id));
        //添加商品对应的库存信息
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(id);
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        goodsVo.setItemList(itemList);
        return goodsVo;
    }

    @Transactional
    @Override
    public void delete(Long[] ids) throws Exception {

        goodsDao.updateByPrimaryKeys(ids);
    }

    @Override
    public PageResult search(Integer page, Integer rows, Goods goods) throws Exception {
        PageHelper.startPage(page,rows);
        //这时查询条件,根据商家的id进行查询
        GoodsQuery goodsQuery = new GoodsQuery();
        GoodsQuery.Criteria criteria = goodsQuery.createCriteria();
        if (goods.getSellerId() != null && !"".equals(goods.getSellerId().trim())) {
            criteria.andSellerIdEqualTo(goods.getSellerId().trim());
            //这一步是判断当商品的is_delete为空时,就属于非删除状态,需要显示在页面,反之则不显示
            criteria.andIsDeleteIsNull();

        }
        goodsQuery.setOrderByClause("id desc");
        Page<Goods> p = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public PageResult searchForManager(Integer page, Integer rows, Goods goods) throws Exception {
        PageHelper.startPage(page,rows);
        GoodsQuery goodsQuery = new GoodsQuery();
        if (goods.getAuditStatus() != null && !"".equals(goods.getAuditStatus().trim())) {
            goodsQuery.createCriteria().andAuditStatusEqualTo(goods.getAuditStatus().trim());
        }
        goodsQuery.setOrderByClause("id desc");
        Page<Goods> p = (Page<Goods>)goodsDao.selectByExample(goodsQuery);
        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    @Transactional
    public void add(GoodsVo goodsVo) throws Exception {
        //获取商品对象
        Goods goods = goodsVo.getGoods();
        //设置商品的审核装状态,默认是未审核,未0
        goods.setAuditStatus("0");
        goodsDao.insertSelective(goods);

        GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
        goodsDesc.setGoodsId(goods.getId());
        goodsDescDao.insertSelective(goodsDesc);


        //判断,如果启用规格,就执行规格属性的添加
        if("1".equals(goods.getIsEnableSpec())){
            //一个产品下对应多个产品项目
            List<Item> itemList = goodsVo.getItemList();
            for (Item item : itemList) {
                //获取商品名称,作为副标题添加
                String title = goods.getGoodsName();
                //获取扩展属性
                String customAttributeItems = goodsDesc.getCustomAttributeItems();
                //获取产品项目里的规格JSON串,转换成Map集合
                Map<String, String> map = JSON.parseObject(item.getSpec(),Map.class);
                //遍历Map集合
                Set<Map.Entry<String, String>> entrySet = map.entrySet();
                //一般的产品副标题都是由不同属性组合在一起的,所以可以将这些属性叠加在副标题里
                for(Map.Entry entry:entrySet){
                    title +=" " + entry.getValue();
                }
                item.setTitle(title);

                setAttributeForItem(goods,goodsDesc,item); //设置库表的属性
                itemDao.insertSelective(item);
            }
        }else{
            //不启用规格,一个商品对应一个库存
            Item item = new Item();
            item.setTitle(goods.getGoodsName()+" "+goods.getCaption());
            item.setPrice(goods.getPrice());
            item.setNum(9999);
            item.setIsDefault("1");
            item.setSpec("{}");
            setAttributeForItem(goods,goodsDesc,item); //设置库表的属性
            itemDao.insertSelective(item);
        }
    }

    private void setAttributeForItem(Goods goods,GoodsDesc goodsDesc ,Item item) throws Exception {
        //将图片的Json串转换成Map集合
        List<Map> imgList = JSON.parseArray(goodsDesc.getItemImages(), Map.class);
        if(imgList != null && imgList.size() > 0){
            String img = imgList.get(0).get("url").toString();
            item.setImage(img);
        }
        item.setStatus("1"); //设置商品状态1为正常,2为下加,3为删除
        item.setCategoryid(goods.getCategory3Id()); //设置三级分类,此处为三级
        item.setCreateTime(new Date());//设置创建的时间
        item.setUpdateTime(new Date()); //设置更新的时间
        item.setGoodsId(goods.getId());  //设置商品ID
        item.setSellerId(goods.getSellerId()); //设置商家ID
        //设置分类
        item.setCategory(itemCatDao.selectByPrimaryKey(goods.getCategory3Id()).getName());
        //设置品牌
        item.setBrand(brandDao.selectByPrimaryKey(goods.getBrandId()).getName());
        //设置商家
        item.setSeller(sellerDao.selectByPrimaryKey(goods.getSellerId()).getNickName());

    }
}
