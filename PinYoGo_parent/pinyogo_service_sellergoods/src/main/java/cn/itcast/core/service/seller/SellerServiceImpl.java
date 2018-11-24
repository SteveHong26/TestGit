package cn.itcast.core.service.seller;

import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.page.PageResult;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojo.seller.SellerQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.Date;


@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerDao sellerDao;


    /**
     * 商家审核,将审核后要更改的商家状态更新到数据库
     * @param sellerId
     * @param status
     * @throws Exception
     */
    @Override
    public void updateStatus(String sellerId, String status) throws Exception {
        Seller seller = new Seller();
        seller.setSellerId(sellerId);
        seller.setStatus(status);
        sellerDao.updateByPrimaryKeySelective(seller);
    }

    /**
     * 根据页面提供的主键对商家进行搜索,并回显至页面
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public Seller findOne(String id) throws Exception {
        return sellerDao.selectByPrimaryKey(id);
    }

    /**
     * 商家注册,并将注册的信息写入数据库
     * @param seller
     * @throws Exception
     */
    @Override
    public void add(Seller seller) throws Exception {
        seller.setStatus("0");
        seller.setCreateTime(new Date());
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String password = bCryptPasswordEncoder.encode(seller.getPassword());
        seller.setPassword(password);
        sellerDao.insertSelective(seller);
    }


    /**
     * 根据用户提供的搜索条件对商家进行搜索
     * @param page
     * @param rows
     * @param seller
     * @return
     * @throws Exception
     */
    @Override
    public PageResult search(Integer page, Integer rows, Seller seller) throws Exception {
        PageHelper.startPage(page, rows);
        SellerQuery sellerQuery = new SellerQuery();
        if (seller.getStatus() != null && !"".equals(seller.getStatus().trim())) {
            sellerQuery.createCriteria().andStatusEqualTo(seller.getStatus().trim());
        }
        Page<Seller> p = (Page<Seller>)sellerDao.selectByExample(sellerQuery);

        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public void delete(Long[] ids) throws Exception {
        sellerDao.deleteByPrimaryKeys(ids);


    }
}
