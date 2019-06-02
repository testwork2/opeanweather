package io.openweather.domain.features.weather;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.openweather.domain.entities.ValidationResult;
import io.openweather.domain.features.Resources;
import io.openweather.domain.misc.exceptions.IllegalDataException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

public class ValidatePlaceNameUseCaseTest {

    @Mock
    private Resources resources;
    private String errorMessage = "errorMessage";
    private ValidatePlaceNameUseCase useCase;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(resources.getInvalidPlaceMessage()).thenReturn(errorMessage);
        useCase = new ValidatePlaceNameUseCase(resources);
    }

    @Test
    public void testValidationSuccess() {
        assertThat(useCase.execute("place"), is(new ValidationResult<>("place")));
    }

    @Test
    public void testValidationSuccessTrimmed() {
        assertThat(useCase.execute("   place   "), is(new ValidationResult<>("place")));
    }

    @Test
    public void testValidationEmptyTrimmed() {
        assertException(useCase.execute(""));
    }

    @Test
    public void testValidationEmpty() {
        assertException(useCase.execute("   "));
    }

    @Test
    public void testValidationSpace() {
        assertException(useCase.execute("   "));
    }


    private IllegalDataException error() {
        return new IllegalDataException(errorMessage);
    }


    private void assertException(ValidationResult<?> result) {
        assertThat(result.getResult(), nullValue());
        assertThat(result.getThrowable(), notNullValue());
        assertThat(result.getThrowable(), instanceOf(IllegalDataException.class));
        assertThat(result.getThrowable().getMessage(), is(errorMessage));
    }

}