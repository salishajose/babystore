package com.brocamp.babystore.serviceimpl;

import com.brocamp.babystore.service.CanvasjsChartService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
@Transactional
public class CanvasjsChartServiceImpl implements CanvasjsChartService {
    static Map<Object,Object> map = null;
    static List<List<Map<Object,Object>>> list = new ArrayList<>();
    static List<Map<Object,Object>> dataPoints1 = new ArrayList<Map<Object,Object>>();

    static {
        map = new HashMap<>(); map.put("x", 2006); map.put("y", 13.3);dataPoints1.add(map);
        map = new HashMap<Object,Object>(); map.put("x", 2007); map.put("y", 17);dataPoints1.add(map);
        map = new HashMap<Object,Object>(); map.put("x", 2008); map.put("y", 14.8);dataPoints1.add(map);
        map = new HashMap<Object,Object>(); map.put("x", 2009); map.put("y", 10.9);dataPoints1.add(map);
        map = new HashMap<Object,Object>(); map.put("x", 2010); map.put("y", 11.8);dataPoints1.add(map);
        map = new HashMap<Object,Object>(); map.put("x", 2011); map.put("y", 10.8);dataPoints1.add(map);
        map = new HashMap<Object,Object>(); map.put("x", 2012); map.put("y", 10.5);dataPoints1.add(map);
        map = new HashMap<Object,Object>(); map.put("x", 2013); map.put("y", 10.6);dataPoints1.add(map);
        map = new HashMap<Object,Object>(); map.put("x", 2014); map.put("y", 12);dataPoints1.add(map);
        map = new HashMap<Object,Object>(); map.put("x", 2015); map.put("y", 11.7);dataPoints1.add(map);
        map = new HashMap<Object,Object>(); map.put("x", 2016); map.put("y", 13);dataPoints1.add(map);

        list.add(dataPoints1);
    }
    @Override
    public List<List<Map<Object, Object>>> getCanvasjsChartData() {
        return list;
    }
}
