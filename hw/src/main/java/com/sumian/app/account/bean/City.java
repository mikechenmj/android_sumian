package com.sumian.app.account.bean;

import java.util.List;

/**
 * Created by jzz
 * on 2017/10/25.
 * desc:
 */

public class City {

    public String name;
    public List<String> area;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        City city = (City) o;

        if (name != null ? !name.equals(city.name) : city.name != null) return false;
        return area != null ? area.equals(city.area) : city.area == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (area != null ? area.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "City{" +
            "name='" + name + '\'' +
            ", area=" + area +
            '}';
    }
}
