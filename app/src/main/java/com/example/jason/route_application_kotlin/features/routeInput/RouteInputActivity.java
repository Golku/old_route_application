package com.example.jason.route_application_kotlin.features.routeInput;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.jason.route_application_kotlin.R;
import com.example.jason.route_application_kotlin.data.pojos.AddressItem;
import com.example.jason.route_application_kotlin.features.addressDetails.AddressDetailsActivity;
import com.example.jason.route_application_kotlin.features.route.RouteActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class RouteInputActivity extends AppCompatActivity implements MvpRouteInput.View{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_input);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.addAddressToListBtn)
    @Override
    public void addAddressToList() {

    }

    @Override
    public void showAddressDetails(AddressItem addressItem) {
        Intent intent = new Intent(this, AddressDetailsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.beginRouteBtn)
    @Override
    public void beginRoute() {
        Intent intent = new Intent(this, RouteActivity.class);
        startActivity(intent);
    }
}
