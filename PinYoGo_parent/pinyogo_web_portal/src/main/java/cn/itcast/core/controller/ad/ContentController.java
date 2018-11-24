package cn.itcast.core.controller.ad;

import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.service.ad.ContentService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    private ContentService contentService;

    @RequestMapping("/findByCategoryId.do")
    public List<Content> findByCategoryId(Long categoryId) throws Exception {
        System.out.println("广告服务");
        return contentService.findByCategoryId(categoryId);
    }
}
