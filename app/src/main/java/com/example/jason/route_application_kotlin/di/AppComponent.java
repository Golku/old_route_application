package com.example.jason.route_application_kotlin.di;

import com.example.jason.route_application_kotlin.data.models.AddressFormatter;
import com.example.jason.route_application_kotlin.di.network.GsonModule;
import com.example.jason.route_application_kotlin.di.network.NetworkModule;
import com.example.jason.route_application_kotlin.di.utilities.AddressFormatterModule;
import com.google.gson.Gson;

import dagger.Component;
import retrofit2.Retrofit;

/**
 * Created by Jason on 07-Feb-18.
 */
@AppScope
@Component(modules = {GsonModule.class, NetworkModule.class, AddressFormatterModule.class})
public interface  AppComponent {
    Retrofit getRetrofit();
    Gson getGson();
    AddressFormatter getAddressFormatter();
}














