package cn.itcast.core.controller.brand;

import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.page.PageResult;
import cn.itcast.core.pojo.page.Result;

import cn.itcast.core.service.brand.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;


    @RequestMapping("/selectOptionList.do")
    public List<Map<String, String>> selectOptionList() throws Exception {
        return brandService.findBrandList();
    }

    @RequestMapping("/findAll.do")
    public List<Brand> getAllBrand() {
        return brandService.findAllBrand();
    }

    /**
     * 根据条件进行分页
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/findPage.do")
    public PageResult pagingResult(Integer pageNum, Integer pageSize){

        return brandService.pagingResult(pageNum,pageSize);
    }

    /**
     * 根据条件查找数据并回显至浏览器
     * @param pageNum
     * @param pageSize
     * @param brand
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult brandSearching(Integer pageNum, Integer pageSize, @RequestBody Brand brand) {
        PageResult pageResult = brandService.brandSearching(pageNum, pageSize, brand);
        return pageResult;
    }

    /**
     * 根据Brand对象向数据库中添加新数据
     * @param brand
     * @return
     */
    @RequestMapping("/add.do")
    public Result brandAdding(@RequestBody Brand brand) {
        System.out.println(brand.toString());
        return  brandService.brandAdding(brand);
    }

    /**
     * 根据ID查找指定数据,回显至页面
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping("/findOne.do")
    public Brand brandFindOne(Long id) throws Exception {
        Brand brand = brandService.brandFindOne(id);
        return brand;
    }

    /**
     * 根据ID对指定数据进行更新
     * @param brand
     * @return
     * @throws Exception
     */
    @RequestMapping("/update.do")
    public Result brandUpdating(@RequestBody Brand brand) throws Exception {
        Result result = brandService.brandUpdating(brand);
        return result;
    }

    @RequestMapping("/delete.do")
    public Result brandDel(Long[] ids){
        try {
            brandService.brandDel(ids);
            return new Result(true,"刪除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"刪除失敗!!");
        }

    }


}
