package modules;

import com.google.inject.AbstractModule;
import modules.preloader.DatabasePreloader;

public class OnStartModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DatabasePreloader.class).asEagerSingleton();
    }
}
