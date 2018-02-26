package com.example.jason.route_application_kotlin.di;

import com.example.jason.route_application_kotlin.features.commentInput.CommentInputActivity;
import com.example.jason.route_application_kotlin.features.commentInput.CommentInputModule;
import com.example.jason.route_application_kotlin.features.addressDetails.AddressDetailsActivity;
import com.example.jason.route_application_kotlin.features.addressDetails.AddressDetailsModule;
import com.example.jason.route_application_kotlin.features.correctInvalidAddresses.CorrectInvalidAddressesActivity;
import com.example.jason.route_application_kotlin.features.correctInvalidAddresses.CorrectInvalidAddressesModule;
import com.example.jason.route_application_kotlin.features.route.RouteActivity;
import com.example.jason.route_application_kotlin.features.route.RouteModule;
import com.example.jason.route_application_kotlin.features.routeInput.RouteInputActivity;
import com.example.jason.route_application_kotlin.features.routeInput.RouteInputModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Jason on 12-Feb-18.
 */

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = RouteInputModule.class)
    abstract RouteInputActivity bindRouteInput();

    @ContributesAndroidInjector(modules = RouteModule.class)
    abstract RouteActivity bindRoute();

    @ContributesAndroidInjector(modules = AddressDetailsModule.class)
    abstract AddressDetailsActivity bindAddressDetails();

    @ContributesAndroidInjector(modules = CommentInputModule.class)
    abstract CommentInputActivity bindCommentInput();

    @ContributesAndroidInjector(modules = CorrectInvalidAddressesModule.class)
    abstract CorrectInvalidAddressesActivity bindCorrectInvalidAddresses();

}
