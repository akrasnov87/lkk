package ru.mobnius.cic.di;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {DataBaseModule.class, MeterResultModule.class})
public interface AppComponent {

}
