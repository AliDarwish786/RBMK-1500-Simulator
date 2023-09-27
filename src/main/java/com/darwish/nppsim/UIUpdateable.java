package com.darwish.nppsim;

public interface UIUpdateable {
    void update();
    void initializeDialUpdateThread();
    void setVisibility(boolean visible);
    void discard();
    void acknowledge();
}

