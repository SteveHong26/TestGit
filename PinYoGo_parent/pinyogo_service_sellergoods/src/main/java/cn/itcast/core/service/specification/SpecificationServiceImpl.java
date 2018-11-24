package cn.itcast.core.service.specification;

import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.pojo.page.PageResult;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.specification.SpecificationQuery;
import cn.itcast.core.vo.SpecificationVo;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;



import java.util.List;
import java.util.Map;

@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecificationDao specificationDao;

    @Autowired
    private SpecificationOptionDao specificationOptionDao;


    @Override
    public List<Map<String, String>> findSpecList() throws Exception {

        return specificationDao.findSpecList();
    }

    @Override
    public SpecificationVo findOne(Long id) throws Exception {
        SpecificationVo specificationVo = new SpecificationVo();
        //先根據主鍵查詢到Specification
        Specification specification = specificationDao.selectByPrimaryKey(id);
        specificationVo.setSpecification(specification);
        //在通過獲取到的specification獲取到他的主鍵
        SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
        //添加查詢SpecificationOption的條件
        specificationOptionQuery.createCriteria().andSpecIdEqualTo(id);
        List<SpecificationOption> specificationOptions = specificationOptionDao.selectByExample(specificationOptionQuery);
        specificationVo.setSpecificationOptionList(specificationOptions);
        return specificationVo;
    }

    @Override
    public void add(SpecificationVo specificationVo) throws Exception {
        //通過specificationVo對象獲取specification對象,並插入到數據庫中
        Specification specification = specificationVo.getSpecification();
        //插入specification
        specificationDao.insertSelective(specification);
        //通過specificationVo對象獲取specificationOptionList對象,執行相關操作
        List<SpecificationOption> specificationOptionList = specificationVo.getSpecificationOptionList();
        //遍歷specificationOptionList,並設置外鍵
        if (specificationOptionList != null && specificationOptionList.size() > 0){

            // specificationOptionList.forEach(op->op.setId(specification.getId()));
            // 批量插入specificationOption到數據庫中
            for (SpecificationOption specificationOption : specificationOptionList) {
                specificationOption.setSpecId(specification.getId());
            }
            specificationOptionDao.insertSelectives(specificationOptionList);

        }

    }

    @Override
    public void update(SpecificationVo specificationVo) throws Exception {
        Specification specification = specificationVo.getSpecification();
        //更新規格
        specificationDao.updateByPrimaryKeySelective(specification);
        //更新規格選項的前提是，先刪除，再更新

        SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
        specificationOptionQuery.createCriteria().andSpecIdEqualTo(specification.getId());
        specificationOptionDao.deleteByExample(specificationOptionQuery);

        //獲取存儲SpecificationOption的集合
        List<SpecificationOption> specificationOptionList = specificationVo.getSpecificationOptionList();
        //遍歷集合中存儲的每一個specificationOption,並為它們設置外鍵
        if(specificationOptionList != null && specificationOptionList.size() > 0){
            //specificationOptionList.forEach(op->op.setId(specification.getId()));
            //將specificationOnptionList批量插入數據庫中
            for (SpecificationOption specificationOption : specificationOptionList) {
                specificationOption.setSpecId(specification.getId());
            }
            specificationOptionDao.insertSelectives(specificationOptionList);
        }


    }

    @Override
    public void delete(Long[] ids) throws Exception {
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
                specificationOptionQuery.createCriteria().andSpecIdEqualTo(id);
                specificationOptionDao.deleteByExample(specificationOptionQuery);
                specificationDao.deleteByPrimaryKey(id);
            }
        }
    }

    @Override
    public List<Specification> findAll() throws Exception {

        return specificationDao.selectByExample(null);
    }

    @Override
    public PageResult findPage(Integer page, Integer rows) throws Exception {
        PageHelper.startPage(page, rows);
        Page<Specification> pages = (Page<Specification>) specificationDao.selectByExample(null);
        return new PageResult(pages.getTotal(), pages.getResult());
    }

    @Override
    public PageResult search(Integer page, Integer rows, Specification specification) throws Exception {
        PageHelper.startPage(page, rows);
        SpecificationQuery specificationQuery = new SpecificationQuery();

        if (specification.getSpecName() != null && !"".equals(specification.getSpecName().trim())) {
            specificationQuery.createCriteria().andSpecNameLike("%" + specification.getSpecName() + "%");
        }
        specificationQuery.setOrderByClause("id desc");
        Page<Specification> pages = (Page<Specification>) specificationDao.selectByExample(specificationQuery);

        return new PageResult(pages.getTotal(), pages.getResult());
    }
}
