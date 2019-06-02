package io.openweather.domain.features.weather;

import androidx.annotation.Nullable;

import io.openweather.domain.entities.ValidationResult;
import io.openweather.domain.features.Resources;
import io.openweather.domain.misc.UseCaseParams;
import io.openweather.domain.misc.exceptions.IllegalDataException;

public class ValidatePlaceNameUseCase implements UseCaseParams<CharSequence, ValidationResult<String>> {

    private final Resources resources;

    public ValidatePlaceNameUseCase(Resources resources) {
        this.resources = resources;
    }

    @Override
    public ValidationResult<String> execute(@Nullable CharSequence place) {
        if (place != null && place.length() > 0) {
            String trimmed = place.toString().trim();
            if (!trimmed.isEmpty()) {
                return new ValidationResult<>(trimmed);
            }
        }
        return new ValidationResult<>(new IllegalDataException(resources.getInvalidPlaceMessage()));

    }
}
