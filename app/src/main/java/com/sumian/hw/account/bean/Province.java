package com.sumian.hw.account.bean;

import java.util.List;

/**
 * Created by jzz
 * on 2017/10/25.
 * desc:
 */

public class Province {

    public String name;
    public List<City> city;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Province province = (Province) o;

        if (name != null ? !name.equals(province.name) : province.name != null) return false;
        return city != null ? city.equals(province.city) : province.city == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (city != null ? city.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Province{" +
            "name='" + name + '\'' +
            ", city=" + city +
            '}';
    }
}
