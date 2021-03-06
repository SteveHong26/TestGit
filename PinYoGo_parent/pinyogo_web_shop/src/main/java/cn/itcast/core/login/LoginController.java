package cn.itcast.core.login;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/showName.do")
    public Map<String,String> showName() {
        Map<String, String> nameMap = new HashMap<>();
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        nameMap.put("username", name);
        return nameMap;
    }
}
