package io.openweather.presentation.features;

import io.openweather.R;

public enum InputState {

    DISABLE(R.id.state_disabled),
    EDITABLE(R.id.state_editable);

    private int stateId;

    InputState(int stateId) {
        this.stateId = stateId;
    }

    public int getStateId() {
        return stateId;
    }
}
