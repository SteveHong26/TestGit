package cn.itcast.core.controller.specification;


import cn.itcast.core.pojo.page.PageResult;
import cn.itcast.core.pojo.page.Result;
import cn.itcast.core.pojo.specification.Specification;


import cn.itcast.core.service.specification.SpecificationService;
import cn.itcast.core.vo.SpecificationVo;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;


    @RequestMapping("/selectOptionList.do")
    public List<Map<String, String>> selectOptionList() throws Exception {
        return specificationService.findSpecList();
    }

    @RequestMapping("/findAll.do")
    public List<Specification> findAll() throws Exception {
        return specificationService.findAll();
    }

    @RequestMapping("/findPage.do")
    public PageResult findPage(Integer page, Integer rows) throws Exception {

        PageResult pageResult = specificationService.findPage(page, rows);
        return pageResult;
    }

    @RequestMapping("/findOne.do")
    public SpecificationVo findOne(Long id) throws Exception {
        //根據ID查找specification對象,並回顯至頁面
        System.out.println(id);
        return specificationService.findOne(id);
    }

    @RequestMapping("/add.do")
    public Result add(@RequestBody SpecificationVo specificationVo) {
        try {
            specificationService.add(specificationVo);
            return new Result(true,"Inserting data to Mysql dataBase successful");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"Failed to insert");
        }
    }

    @RequestMapping("/update.do")
    public Result update(@RequestBody SpecificationVo specificationVo) {
        try{
            specificationService.update(specificationVo);
            return new Result(true, "Update successful!！");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"Failed to update!!");
        }
    }

    @RequestMapping("/delete.do")
    public Result delete(Long[] ids) {
        try{
            specificationService.delete(ids);
            return new Result(true,"Delete Successful");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"Delete failed!!");
        }

    }

    @RequestMapping("/search.do")
    public PageResult search(Integer page,Integer rows,@RequestBody Specification specification) throws Exception {
        PageResult pageResult = specificationService.search(page,rows,specification);
        return pageResult;
    }
}
