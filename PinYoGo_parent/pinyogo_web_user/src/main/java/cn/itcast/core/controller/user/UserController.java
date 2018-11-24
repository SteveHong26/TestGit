package cn.itcast.core.controller.user;

import cn.itcast.core.pojo.page.Result;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.user.UserService;
import cn.itcast.core.utils.PNCheckUtil.PhoneFormatCheckUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @RequestMapping("/add.do")
    public Result add(String smscode, @RequestBody User user) throws Exception {
        if (!userService.checkSmsCode(user.getPhone(), smscode)) {
            return new Result(false, "😅 Please make sure your code is identified!");
        }else {
            try {
                userService.add(user);
                return new Result(true, "😊 You have successfully registered");
            } catch (Exception e) {
                e.printStackTrace();
                return new Result(false, "😭 Something could be wrong!!");
            }
        }
    }

    @RequestMapping("/sendCode.do")
    public Result codeChecking(String phone) {
        if (!PhoneFormatCheckUtils.isPhoneLegal(phone)) {
            return new Result(false, "🙂 The format of your phone number is being illegal, try another one");
        } else {
            try {
                long smsCode = userService.createSmsCode(phone);
                return new Result(true, "🙂 Your code is:"+smsCode);
            } catch (Exception e) {
                e.printStackTrace();
                return new Result(false, "😅 Something could be wrong!");
            }
        }
    }
}
