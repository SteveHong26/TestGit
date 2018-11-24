package cn.itcast.core.controller.ad;

import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.page.PageResult;
import cn.itcast.core.pojo.page.Result;
import cn.itcast.core.service.ad.ContentService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    private ContentService contentService;


    @RequestMapping("/add.do")
    public Result add(@RequestBody Content content) {
        try {
            contentService.add(content);
            return new Result(true,"😊 Successful!!!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"😭!!! Something could be wrong and contact our engineer Phone:010-2326456");
        }
    }

    @RequestMapping("/delete.do")
    public Result delete(Long[] ids) throws Exception {
        try {
            contentService.delete(ids);
            return new Result(true,"😊 Successful!!!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"😭!!! Something could be wrong and contact our engineer Phone:010-2326456");
        }
    }

    @RequestMapping("/update.do")
    public Result update(@RequestBody Content content) {
        try {
            contentService.update(content);
            return new Result(true,"😊 Successful!!!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"😭!!! Something could be wrong and contact our engineer Phone:010-2326456");
        }
    }

    @RequestMapping("/search.do")
    public PageResult search(Integer page,Integer rows,@RequestBody Content content) throws Exception {
        return contentService.findPage(page,rows,content);
    }

    @RequestMapping("/findAll.do")
    public List<Content> findAll() throws Exception {
        return contentService.findAll();
    }

    @RequestMapping("/findOne.do")
    public Content findOne(Long id) throws Exception {
        return contentService.findOne(id);
    }
}
