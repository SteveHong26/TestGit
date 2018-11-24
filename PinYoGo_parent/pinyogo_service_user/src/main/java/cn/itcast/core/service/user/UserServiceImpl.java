package cn.itcast.core.service.user;

import cn.itcast.core.dao.address.AddressDao;
import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.address.AddressQuery;

import cn.itcast.core.pojo.user.User;

import com.alibaba.dubbo.config.annotation.Service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;



import javax.jms.Destination;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination ssmTextDestination;

    @Autowired
    private AddressDao addressDao;


    @Override
    public List<Address> findProfileByUser(String userId) throws Exception {
        //封装查询条件查询
        AddressQuery addressQuery = new AddressQuery();
        addressQuery.createCriteria().andUserIdEqualTo(userId);
        //查询该用户名下的练习人信息
        return addressDao.selectByExample(addressQuery);
    }

    @Override
    public void add(User user) throws Exception {
        user.setCreated(new Date());
        user.setUpdated(new Date());

        String password = DigestUtils.md5Hex(user.getPassword());
        user.setPassword(password);
        userDao.insert(user);
    }

    @Override
    public long createSmsCode(String phone) throws Exception {
        //随机生成一个6位数作为验证码
        Long code = (long) (Math.random() * 100000);
        //判断验证码是否为6位数
        if (code < 100000) {
            code = code + 100000;
        }
        System.out.println("验证码: " + code);
        HashMap map = new HashMap();
        map.put("mobile",phone);
        map.put("ssmCode",code+"");
        //将生成的验证码存入redis数据库当中
        redisTemplate.boundValueOps("ssmCode" + phone).set(code + "", 5, TimeUnit.MINUTES);
        //发送消息队列
        jmsTemplate.convertAndSend(ssmTextDestination,map);
        return code;

    }

    @Override
    public boolean checkSmsCode(String phone, String smsCode) throws Exception {
        String sysCode = redisTemplate.boundValueOps("ssmCode" + phone).get();
        System.out.println(sysCode);
        if (smsCode.equals(sysCode) && sysCode != null) {
            return true;
        }
        return false;

    }
}
