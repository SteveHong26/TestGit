package cn.itcast.core.controller.ad;

import cn.itcast.core.pojo.ad.ContentCategory;
import cn.itcast.core.pojo.page.PageResult;
import cn.itcast.core.pojo.page.Result;
import cn.itcast.core.service.ad.ContentCategoryService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contentCategory")
public class ContentCategoryController {

    @Reference
    private ContentCategoryService contentCategoryService;

    @RequestMapping("/add.do")
    public Result add(@RequestBody ContentCategory contentCategory) {
        try {
            contentCategoryService.add(contentCategory);
            return new Result(true,"😊 Successful!!!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"😭!!! Something could be wrong and contact our engineer Phone:010-2326456");
        }
    }

    @RequestMapping("/delete.do")
    public Result delete(Long[] ids) {
        try {
            contentCategoryService.delete(ids);
            return new Result(true,"😊 Successful!!!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"😭!!! Something could be wrong and contact our engineer Phone:010-2326456");
        }
    }

    @RequestMapping("/update.do")
    public Result update(@RequestBody ContentCategory contentCategory) {
        try {
            contentCategoryService.update(contentCategory);
            return new Result(true,"😊 Successful!!!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"😭!!! Something could be wrong and contact our engineer Phone:010-2326456");
        }
    }

    @RequestMapping("/findOne.do")
    public ContentCategory findOne(Long id) throws Exception {
        return contentCategoryService.findOne(id);
    }

    @RequestMapping("/findAll.do")
    public List<ContentCategory> findAll() throws Exception {
        return contentCategoryService.findAll();
    }

    @RequestMapping("/search.do")
    public PageResult search(Integer page,Integer rows,@RequestBody ContentCategory contentCategory) throws Exception {
        return contentCategoryService.findPage(page, rows, contentCategory);
    }

}
