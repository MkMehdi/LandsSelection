package com.sample.lands.mapbox.ui.jmap;

import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.sample.lands.R;
import com.sample.lands.mapbox.db.model.Land;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class JMapFragment extends Fragment implements OnMapReadyCallback,MapboxMap.OnMapLongClickListener,
View.OnClickListener{

    // mapBox
    private static final String CIRCLE_SOURCE_ID = "circle-source-id";
    private static final String FILL_SOURCE_ID = "fill-source-id";
    private static final String LINE_SOURCE_ID = "line-source-id";
    private static final String CIRCLE_LAYER_ID = "circle-layer-id";
    private static final String FILL_LAYER_ID = "fill-layer-polygon-id";
    private static final String LINE_LAYER_ID = "line-layer-id";
    private List<Point> fillLayerPointList = new ArrayList<>();
    private List<Point> lineLayerPointList = new ArrayList<>();
    private List<Feature> circleLayerFeatureList = new ArrayList<>();
    private List<List<Point>> listOfList;
    private GeoJsonSource circleSource;
    private GeoJsonSource fillSource;
    private GeoJsonSource lineSource;
    private Point firstPointOfPolygon;

    private MapboxMap mapboxMap;
    private MapView mapView;

    // END

    private View parentView;
    private FloatingActionButton btnSave;
    private View clearBtn;
    private JMapViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        parentView = inflater.inflate(R.layout.j_map_fragment, container, false);

        mapView = parentView.findViewById(R.id.mapView);
        clearBtn = parentView.findViewById(R.id.clear_button);
        btnSave = parentView.findViewById(R.id.save_button);

        return parentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(JMapViewModel.class);


        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        clearBtn.setOnClickListener(this);

        btnSave.setOnClickListener(view -> {
            mViewModel.saveLand();
            Snackbar.make(parentView,getString(R.string.successfully_msg),Snackbar.LENGTH_LONG).show();
        });

    }



    @Override
    public void onMapReady(@NonNull MapboxMap map) {
        this.mapboxMap = map;
        mapboxMap.addOnMapLongClickListener(this);

        mapboxMap.setStyle(Style.SATELLITE_STREETS, style -> {
            mapboxMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .build()), 4000);

            // Add sources to the map
            circleSource = initCircleSource(style);
            fillSource = initFillSource(style);
            lineSource = initLineSource(style);

            // Add layers to the map
            initCircleLayer(style);
            initLineLayer(style);
            initFillLayer(style);

            // show Land
            if(getArguments() != null){


                mViewModel.land = (Land) getArguments().getSerializable("land");

                Log.d("check","arg " + mViewModel.land.getPoints().get(0).getLongitude());

                showSavedLand();
            }

        });

    }



    @Override
    public boolean onMapLongClick(@NonNull LatLng point) {

        mViewModel.collectLandPoints(point);

        // Use the map click location to create a Point object
        Point mapTargetPoint = Point.fromLngLat(point.getLongitude(),
                point.getLatitude());

        // Make note of the first map click location so that it can be used to create a closed polygon later on
        if (circleLayerFeatureList.size() == 0) {
            firstPointOfPolygon = mapTargetPoint;
        }


        // Add the click point to the circle layer and update the display of the circle layer data
        circleLayerFeatureList.add(Feature.fromGeometry(mapTargetPoint));
        if (circleSource != null) {
            circleSource.setGeoJson(FeatureCollection.fromFeatures(circleLayerFeatureList));
        }

        // Add the click point to the line layer and update the display of the line layer data
        if (circleLayerFeatureList.size() < 3) {
            lineLayerPointList.add(mapTargetPoint);
        } else if (circleLayerFeatureList.size() == 3) {
            lineLayerPointList.add(mapTargetPoint);
            lineLayerPointList.add(firstPointOfPolygon);
        } else {
            lineLayerPointList.remove(circleLayerFeatureList.size() - 1);
            lineLayerPointList.add(mapTargetPoint);
            lineLayerPointList.add(firstPointOfPolygon);
        }
        if (lineSource != null) {
            lineSource.setGeoJson(FeatureCollection.fromFeatures(new Feature[]
                    {Feature.fromGeometry(LineString.fromLngLats(lineLayerPointList))}));
        }

        // Add the click point to the fill layer and update the display of the fill layer data
        if (circleLayerFeatureList.size() < 3) {
            fillLayerPointList.add(mapTargetPoint);
        } else if (circleLayerFeatureList.size() == 3) {
            fillLayerPointList.add(mapTargetPoint);
            fillLayerPointList.add(firstPointOfPolygon);
        } else {
            fillLayerPointList.remove(fillLayerPointList.size() - 1);
            fillLayerPointList.add(mapTargetPoint);
            fillLayerPointList.add(firstPointOfPolygon);
        }
        listOfList = new ArrayList<>();
        listOfList.add(fillLayerPointList);
        List<Feature> finalFeatureList = new ArrayList<>();
        finalFeatureList.add(Feature.fromGeometry(Polygon.fromLngLats(listOfList)));
        FeatureCollection newFeatureCollection = FeatureCollection.fromFeatures(finalFeatureList);
        if (fillSource != null) {
            fillSource.setGeoJson(newFeatureCollection);
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        clearEntireMap();

        Toast.makeText(getActivity(), "Clear", Toast.LENGTH_LONG).show();

    }

    private void showSavedLand(){

        for (int i = 0; i < mViewModel.land.getPoints().size(); i++) {

            Log.d("tag","id location :: " + i);


            Point mapTargetPoint =  Point.fromLngLat(mViewModel.land.getPoints().get(i).getLongitude(),
                    mViewModel.land.getPoints().get(i).getLatitude());

            if(i == 0){
                firstPointOfPolygon = mapTargetPoint;
            }

            // Add the click point to the circle layer and update the display of the circle layer data
            circleLayerFeatureList.add(Feature.fromGeometry(mapTargetPoint));
            if (circleSource != null) {
                circleSource.setGeoJson(FeatureCollection.fromFeatures(circleLayerFeatureList));
            }

            // Add the click point to the line layer and update the display of the line layer data
            if (circleLayerFeatureList.size() < 3) {
                lineLayerPointList.add(mapTargetPoint);
            } else if (circleLayerFeatureList.size() == 3) {
                lineLayerPointList.add(mapTargetPoint);
                lineLayerPointList.add(firstPointOfPolygon);
            } else {
                lineLayerPointList.remove(circleLayerFeatureList.size() - 1);
                lineLayerPointList.add(mapTargetPoint);
                lineLayerPointList.add(firstPointOfPolygon);
            }
            if (lineSource != null) {
                lineSource.setGeoJson(FeatureCollection.fromFeatures(new Feature[]
                        {Feature.fromGeometry(LineString.fromLngLats(lineLayerPointList))}));
            }

            // Add the click point to the fill layer and update the display of the fill layer data
            if (circleLayerFeatureList.size() < 3) {
                fillLayerPointList.add(mapTargetPoint);
            } else if (circleLayerFeatureList.size() == 3) {
                fillLayerPointList.add(mapTargetPoint);
                fillLayerPointList.add(firstPointOfPolygon);
            } else {
                fillLayerPointList.remove(fillLayerPointList.size() - 1);
                fillLayerPointList.add(mapTargetPoint);
                fillLayerPointList.add(firstPointOfPolygon);
            }
            listOfList = new ArrayList<>();
            listOfList.add(fillLayerPointList);
            List<Feature> finalFeatureList = new ArrayList<>();
            finalFeatureList.add(Feature.fromGeometry(Polygon.fromLngLats(listOfList)));
            FeatureCollection newFeatureCollection = FeatureCollection.fromFeatures(finalFeatureList);
            if (fillSource != null) {
                fillSource.setGeoJson(newFeatureCollection);
            }
        }


        mapboxMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(firstPointOfPolygon.latitude(), firstPointOfPolygon.longitude()))
                        .build()), 4000);

    }




    /**
     * Remove the drawn area from the map by resetting the FeatureCollections used by the layers' sources
     */
    private void clearEntireMap() {
        fillLayerPointList = new ArrayList<>();
        circleLayerFeatureList = new ArrayList<>();
        lineLayerPointList = new ArrayList<>();
        if (circleSource != null) {
            circleSource.setGeoJson(FeatureCollection.fromFeatures(new Feature[] {}));
        }
        if (lineSource != null) {
            lineSource.setGeoJson(FeatureCollection.fromFeatures(new Feature[] {}));
        }
        if (fillSource != null) {
            fillSource.setGeoJson(FeatureCollection.fromFeatures(new Feature[] {}));
        }

        // viewModel data collector
        mViewModel.clear();
    }

    /**
     * Set up the CircleLayer source for showing map click points
     */
    private GeoJsonSource initCircleSource(@NonNull Style loadedMapStyle) {
        FeatureCollection circleFeatureCollection = FeatureCollection.fromFeatures(new Feature[] {});
        GeoJsonSource circleGeoJsonSource = new GeoJsonSource(CIRCLE_SOURCE_ID, circleFeatureCollection);
        loadedMapStyle.addSource(circleGeoJsonSource);
        return circleGeoJsonSource;
    }

    /**
     * Set up the CircleLayer for showing polygon click points
     */
    private void initCircleLayer(@NonNull Style loadedMapStyle) {
        CircleLayer circleLayer = new CircleLayer(CIRCLE_LAYER_ID,
                CIRCLE_SOURCE_ID);
        circleLayer.setProperties(
                circleRadius(7f),
                circleColor(Color.parseColor("#170e00"))
        );
        loadedMapStyle.addLayer(circleLayer);
    }

    /**
     * Set up the FillLayer source for showing map click points
     */
    private GeoJsonSource initFillSource(@NonNull Style loadedMapStyle) {
        FeatureCollection fillFeatureCollection = FeatureCollection.fromFeatures(new Feature[] {});
        GeoJsonSource fillGeoJsonSource = new GeoJsonSource(FILL_SOURCE_ID, fillFeatureCollection);
        loadedMapStyle.addSource(fillGeoJsonSource);
        return fillGeoJsonSource;
    }

    /**
     * Set up the FillLayer for showing the set boundaries' polygons
     */
    private void initFillLayer(@NonNull Style loadedMapStyle) {
        FillLayer fillLayer = new FillLayer(FILL_LAYER_ID,
                FILL_SOURCE_ID);
        fillLayer.setProperties(
                fillOpacity(.6f),
                fillColor(Color.parseColor("#00e9ff"))
        );
        loadedMapStyle.addLayerBelow(fillLayer, LINE_LAYER_ID);
    }

    /**
     * Set up the LineLayer source for showing map click points
     */
    private GeoJsonSource initLineSource(@NonNull Style loadedMapStyle) {
        FeatureCollection lineFeatureCollection = FeatureCollection.fromFeatures(new Feature[] {});
        GeoJsonSource lineGeoJsonSource = new GeoJsonSource(LINE_SOURCE_ID, lineFeatureCollection);
        loadedMapStyle.addSource(lineGeoJsonSource);
        return lineGeoJsonSource;
    }

    /**
     * Set up the LineLayer for showing the set boundaries' polygons
     */
    private void initLineLayer(@NonNull Style loadedMapStyle) {
        LineLayer lineLayer = new LineLayer(LINE_LAYER_ID,
                LINE_SOURCE_ID);
        lineLayer.setProperties(
                lineColor(Color.WHITE),
                lineWidth(5f)
        );
        loadedMapStyle.addLayerBelow(lineLayer, CIRCLE_LAYER_ID);
    }


    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }



    }


