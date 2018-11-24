package cn.itcast.core.service.search;

import java.util.Map;

public interface ItemSearchService {
    public Map<String, Object> search(Map<String,String> searchMap) throws Exception;
}
