package top.bestguo.weatherapp.utils;

import java.util.List;
import java.util.Set;

/**
 * Created by He Guo on 2021/2/28.
 */

public class CollectionConvertUtils {

    /**
     * 无序集合转有序集合
     * @param set set集合
     * @param list 列表
     */
    public static void convertSetToList(Set<String> set, List<String> list) {
        if(set != null) {
            for (String aSet : set) {
                list.add(aSet);
            }
        }
    }

    /**
     * 有序集合转无序集合
     * @param list 列表
     * @param set set集合
     */
    public static void convertListToSet(List<String> list, Set<String> set) {
        if(list != null) {
            for (String str : list) {
                set.add(str);
            }
        }
    }



}
