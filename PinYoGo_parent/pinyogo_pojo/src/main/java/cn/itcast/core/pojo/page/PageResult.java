package cn.itcast.core.pojo.page;

import com.github.pagehelper.Page;

import java.io.Serializable;
import java.util.List;


/**
 * 这次分页当中我们使用angular和分页助手进行分页,所以不能拘泥于传统的分页思维中
 这次分页,当前页数和每页应显示的条数应该直接从页面中获取,这样更加客观,也就是说
 现在所操作的业务更加面向用户,用户的操作决定后台要发生什么,我们则要用代码来实现
 因此只有两个参数
 */


public class PageResult implements Serializable {
    private Long total; //总条数
    private List rows;  //结果集

    public PageResult(Long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
