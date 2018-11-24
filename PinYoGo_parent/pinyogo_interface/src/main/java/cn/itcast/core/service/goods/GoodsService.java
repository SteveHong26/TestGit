package cn.itcast.core.service.goods;

import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.page.PageResult;
import cn.itcast.core.vo.GoodsVo;

import java.util.List;

public interface GoodsService {
    void add(GoodsVo goodsVo) throws Exception;

    PageResult search(Integer page, Integer rows, Goods goods) throws Exception;

    void delete(Long[] ids) throws Exception;

    void update(GoodsVo goodsVo)throws Exception ;

    GoodsVo findOne(Long id);

    void updateStatus(Long[] ids,String status) throws Exception;

    PageResult searchForManager(Integer page,Integer rows,Goods goods) throws Exception;
}
