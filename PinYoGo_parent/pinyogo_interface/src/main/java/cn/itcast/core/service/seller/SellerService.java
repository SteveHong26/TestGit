package cn.itcast.core.service.seller;

import cn.itcast.core.pojo.page.PageResult;
import cn.itcast.core.pojo.seller.Seller;

public interface SellerService {
    void add(Seller seller) throws Exception;

    PageResult search(Integer page,Integer rows,Seller seller) throws Exception;

    Seller findOne(String id) throws Exception;

    void updateStatus(String sellerId, String status) throws Exception;

    void delete(Long[] ids) throws Exception;
}
