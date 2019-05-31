package io.openweather.domain.misc;

public interface UseCaseParams<Params, Result> {

    Result execute(Params params);

}
