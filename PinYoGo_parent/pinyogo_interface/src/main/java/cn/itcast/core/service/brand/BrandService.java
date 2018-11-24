package cn.itcast.core.service.brand;

import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.page.PageResult;
import cn.itcast.core.pojo.page.Result;


import java.util.List;
import java.util.Map;

public interface BrandService {
    List<Brand> findAllBrand();

    PageResult pagingResult(Integer pageNum, Integer pageSize);

    PageResult brandSearching(Integer pageNum, Integer pageSize, Brand brand);

    Result brandAdding(Brand brand);

    Brand brandFindOne(Long id) throws Exception;

    Result brandUpdating(Brand brand) throws Exception;

    void brandDel(Long[] ids);

    List<Map<String, String>> findBrandList() throws Exception;
}
