package io.openweather.domain.misc;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public abstract class Mapper<From, To> {

    public abstract To map(@NonNull From value);

    public From reverseMap(@NonNull To value) {
        throw notImplementedException();
    }

    public AbstractMethodError notImplementedException() {
        return new AbstractMethodError("Not implemented method");
    }

    public List<To> mapList(List<From> typeList) {
        List<To> list = new ArrayList<>();

        for (From type : typeList) {
            list.add(map(type));
        }

        return list;

    }

    public List<From> reverseMapList(List<To> typeList) {
        List<From> list = new ArrayList<>();

        for (To type : typeList) {
            list.add(reverseMap(type));
        }

        return list;
    }

}