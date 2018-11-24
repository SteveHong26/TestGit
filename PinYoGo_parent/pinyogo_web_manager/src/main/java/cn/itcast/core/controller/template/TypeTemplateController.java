package cn.itcast.core.controller.template;

import cn.itcast.core.pojo.page.PageResult;
import cn.itcast.core.pojo.page.Result;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.service.template.TypeTemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;

    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody TypeTemplate typeTemplate) throws Exception {
        return typeTemplateService.search(page, rows, typeTemplate);
    }

    @RequestMapping("/add.do")
    public Result add(@RequestBody TypeTemplate typeTemplate) throws Exception {

        try {
            typeTemplateService.add(typeTemplate);
            return new Result(true,"Insert");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"Failed to insert");
        }
    }
}
