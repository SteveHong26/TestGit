package cn.itcast.core.service.user;

import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.user.User;

import java.util.List;

public interface UserService {
    void add(User user) throws Exception;

    long createSmsCode(String phone) throws Exception;

    boolean checkSmsCode(String phone,String smsCode) throws Exception;


    List<Address> findProfileByUser(String userId) throws Exception;

}
