package com.example.jason.route_application.features.container.mapFragment;

import com.example.jason.route_application.data.models.CustomClusterRenderer;
import com.example.jason.route_application.data.pojos.Address;
import com.example.jason.route_application.data.pojos.Event;
import com.example.jason.route_application.data.pojos.MarkerInfo;
import com.example.jason.route_application.data.pojos.api.DriveRequest;
import com.example.jason.route_application.features.shared.BasePresenter;
import com.example.jason.route_application.features.shared.MvpBasePresenter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason on 3/28/2018.
 */

public class MapPresenter extends BasePresenter implements
        MvpBasePresenter,
        MvpMap.Presenter,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {

    private final String debugTag = "debugTag";

    private MvpMap.View view;

    private GoogleMap googleMap;
    private ClusterManager<Address> clusterManager;
    private CustomClusterRenderer renderer;

    private Address userLocation;
    private Address currentAddress;
    private List<Address> addressList;
    private List<Address> routeOrder;
    private Address previousSelectedAddress;

    MapPresenter(MvpMap.View view, List<Address> addressList) {
        this.view = view;
        this.addressList = addressList;
        this.routeOrder = new ArrayList<>();

        userLocation = new Address();
        userLocation.setUserLocation(true);
        userLocation.setAddress("Vrij-Harnasch 21, Den Hoorn");
        userLocation.setLat(52.008234);
        userLocation.setLng(4.312999);
        userLocation.setUserLocation(true);

        previousSelectedAddress = userLocation;
    }

    @Override
    public void setMapData(GoogleMap googleMap, Context context) {

        clusterManager = new ClusterManager<>(context, googleMap);
        renderer = new CustomClusterRenderer(context, googleMap, clusterManager, routeOrder);
        clusterManager.setRenderer(renderer);

        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);

        clusterManager.addItem(userLocation);

        for(Address address : addressList){
            if(address.isValid()){
                clusterManager.addItem(address);
            }
        }

        clusterManager.cluster();

        this.googleMap = googleMap;

        moveMapCamera(userLocation.getLat(), userLocation.getLng());
    }

    private void moveMapCamera(double lat, double lng) {
        CameraPosition cameraPosition = CameraPosition.builder().target(new LatLng(lat, lng)).zoom(10f).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        for(Address address: addressList){
            if(marker.getTitle().equals(address.getAddress())){
                createEvent("container", "itemClick", address, this);
                break;
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Address address = null;

        for(Address it : addressList){
            if(marker.getTitle().equals(it.getAddress())){
                address = it;
                break;
            }
        }

        if(address == null){
            return false;
        }

        if(address.isSelected()){

            if(previousSelectedAddress.equals(address)){

                address.setSelected(false);
                routeOrder.remove(address);

                if (routeOrder.size() > 0) {
                    previousSelectedAddress = routeOrder.get(routeOrder.size() - 1);
                } else {
                    previousSelectedAddress = userLocation;
                }

                removeDrive();
            }else{
                currentAddress = address;
                view.deselectedMultipleMarkers();
            }
        }else{

            address.setSelected(true);
            routeOrder.add(address);
            getDrive(address);
            previousSelectedAddress = address;
        }

        renderer.changeMarkerIcon(address);

        return false;
    }

    private void getDrive(Address address){

        LatLng start;
        LatLng end;
        DriveRequest request = new DriveRequest();

        request.setOrigin(previousSelectedAddress.getAddress());
        start = new LatLng(previousSelectedAddress.getLat(), previousSelectedAddress.getLng());

        request.setDestination(address.getAddress());
        end = new LatLng(address.getLat(), address.getLng());

        createEvent("container", "getDrive", request, this);

        view.getPolylineToMarker(start, end);
    }

    private void removeDrive(){
        createEvent("driveFragment","removeDrive",this);
        view.removePolyLine();
    }

    @Override
    public void multipleMarkersDeselected() {

        int addressPosition = routeOrder.indexOf(currentAddress);

        for(int i = addressPosition; i< routeOrder.size(); i++){
            Address address = routeOrder.get(i);
            address.setSelected(false);
            renderer.changeMarkerIcon(address);
        }

        routeOrder.subList(addressPosition, routeOrder.size()).clear();

        if (routeOrder.size() > 0) {
            previousSelectedAddress = routeOrder.get(routeOrder.size() - 1);
        } else {
            previousSelectedAddress = userLocation;
        }

        view.removePolyLine();

        createEvent("driveFragment", "RemoveMultipleDrive", currentAddress.getAddress(), this);
    }

    @Override
    public void eventReceived(Event event) {

        if(!(event.getReceiver().equals("mapFragment") || event.getReceiver().equals("all"))){
            return;
        }

        Log.d(debugTag, "Event received on mapFragment: "+ event.getEventName());

        switch (event.getEventName()) {
            case "addressTypeChange" : addressTypeChange(event.getAddress());
                break;
            case "updateList" : updateMarkers(event.getAddressList());
                break;
            case "showMarker" : showMarker(event.getAddress());
                break;
            case "markAddress" : addMarkerToMap(event.getAddress());
                break;
            case "removeMarker" : removeMarkerFromMap(event.getAddress());
                break;
        }
    }

    private void addressTypeChange(Address address){
        for(Address it : addressList){
            if(it.getAddress().equals(address.getAddress())){
                renderer.changeMarkerIcon(it);
                break;
            }
        }
    }

    private void updateMarkers(List<Address> addressList) {
        this.addressList = addressList;
        routeOrder.clear();
        clusterManager.clearItems();
        previousSelectedAddress = userLocation;
        view.removePolyLine();

        clusterManager.addItem(userLocation);

        for(Address address : addressList){
            if(address.isValid()){
                clusterManager.addItem(address);
            }
        }

        clusterManager.cluster();
        moveMapCamera(userLocation.getLat(), userLocation.getLng());
    }

    private void showMarker(Address address){
        if(address.isValid()){
            Marker marker = renderer.getMarker(address);
            moveMapCamera(address.getLat(), address.getLng());
            marker.showInfoWindow();
        }
    }

    private void addMarkerToMap(Address address) {
        if(address.isValid()){
            if(address.isUserLocation()){
                clusterManager.removeItem(userLocation);
                userLocation = address;
                if(routeOrder.size() == 0){
                    previousSelectedAddress = userLocation;
                }
            }
            clusterManager.addItem(address);
            clusterManager.cluster();
            moveMapCamera(address.getLat(), address.getLng());
        }
    }

    private void removeMarkerFromMap(Address address) {

        clusterManager.removeItem(address);
        clusterManager.cluster();

        if(address.isSelected()){
            currentAddress = address;
            multipleMarkersDeselected();
        }
    }

    @Override
    public void publishEvent(Event event) {
        view.postEvent(event);
    }
}