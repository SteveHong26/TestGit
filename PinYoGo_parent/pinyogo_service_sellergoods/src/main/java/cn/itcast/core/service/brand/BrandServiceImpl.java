package cn.itcast.core.service.brand;


import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import cn.itcast.core.pojo.page.PageResult;
import cn.itcast.core.pojo.page.Result;
import cn.itcast.core.service.brand.BrandService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandDao brandDao;


    @Override
    public List<Map<String, String>> findBrandList() throws Exception {
        return brandDao.findBrandList();
    }

    @Override
    @Transactional
    public void brandDel(Long[] ids){

        if(ids != null && ids.length > 0){
            brandDao.brandDel(ids);
        }
    }

    /**
     * 根据内容更新数据
     * @param brand
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public Result brandUpdating(Brand brand) throws Exception {
        int i = brandDao.updateByPrimaryKeySelective(brand);
        if(i > 0){
            return new Result(true,"更新成功!");
        }
        return new Result(false,"更新失败!");
    }

    /**
     * 查询所有的品牌
     * @return
     */
    @Override
    public List<Brand> findAllBrand() {
        return brandDao.selectByExample(null);
    }

    /**
     * 手动分页
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult pagingResult(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<Brand> page =(Page<Brand>)brandDao.selectByExample(null);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 根据条件进行查询
     * @param pageNum
     * @param pageSize
     * @param brand
     * @return
     */
    @Override
    public PageResult brandSearching(Integer pageNum, Integer pageSize,Brand brand) {
        PageHelper.startPage(pageNum, pageSize);
        BrandQuery brandQuery = new BrandQuery();
        BrandQuery.Criteria criteria = brandQuery.createCriteria();
        //判断获取的brand属性是否为空和null,没有就调用方法,否则不执行
        if(brand.getName()!=null && !"".equals(brand.getName().trim())){
            //第一个条件封装用模糊查询 like
            criteria.andNameLike("%" + brand.getName().trim() + "%");
        }
        if(brand.getFirstChar()!=null && !"".equals(brand.getFirstChar().trim())){
          //第二个条件封装
            criteria.andFirstCharEqualTo(brand.getFirstChar().trim());
        }
        //根据ID降序
        brandQuery.setOrderByClause("id desc");
        Page<Brand> page = (Page<Brand>)brandDao.selectByExample(brandQuery);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public Brand brandFindOne(Long id) throws Exception {
        Brand brand = brandDao.selectByPrimaryKey(id);
        return brand;
    }

    @Override
    @Transactional
    public Result brandAdding(Brand brand) {
        try{
            brandDao.insertSelective(brand);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }



    }

}
