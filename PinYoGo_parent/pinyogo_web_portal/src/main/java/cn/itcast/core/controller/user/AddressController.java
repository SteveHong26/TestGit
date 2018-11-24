package cn.itcast.core.controller.user;

import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.service.user.UserService;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class AddressController {

    @Reference
    private UserService userService;

    /**
     * 根据当前登陆用户名的
     * @return
     * @throws Exception
     */
    @RequestMapping("/findProfileByUser.do")
    public List<Address> findProfileByUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            return userService.findProfileByUser(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
