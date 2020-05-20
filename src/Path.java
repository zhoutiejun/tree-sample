import java.util.List;
import java.util.Map;

/**
 * @author : zhoutiejun@youngyedu.com, 2020/5/9 0009 下午 13:36
 * @description :
 * @modified : zhoutiejun@youngyedu.com, 2020/5/9 0009 下午 13:36
 */
public class Path {

    /** 当前节点的值*/
    private String value;
    /** 当前子节点的数量*/
    private Integer curNum;
    /** 最大子节点的数量*/
    private Integer maxNum;
    /** 当前节点的值*/
    private Map<String, Path> child;

    public Path(String value, Integer curNum, Integer maxNum, Map<String, Path> child) {
        this.value = value;
        this.curNum = curNum;
        this.maxNum = maxNum;
        this.child = child;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getCurNum() {
        return curNum;
    }

    public void setCurNum(Integer curNum) {
        this.curNum = curNum;
    }

    public Integer getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(Integer maxNum) {
        this.maxNum = maxNum;
    }

    public Map<String, Path> getChild() {
        return child;
    }

    public void setChild(Map<String, Path> child) {
        this.child = child;
    }
}
