package cn.itcast.core.controller.template;

import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.service.template.TypeTemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTempplateController {

    @Reference
    private TypeTemplateService typeTemplateService;

    @RequestMapping("/findOne.do")
    public TypeTemplate findOne(Long id) throws Exception {
        return typeTemplateService.findOne(id);
    }

    @RequestMapping("/findBySpecList.do")
    public List<Map> findBySpecList(Long id) throws Exception {
        return typeTemplateService.findBySpecList(id);
    }
}
