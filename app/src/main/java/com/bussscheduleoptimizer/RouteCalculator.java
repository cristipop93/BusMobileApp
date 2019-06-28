package com.bussscheduleoptimizer;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.bussscheduleoptimizer.adapters.RecyclerViewAdapter;
import com.bussscheduleoptimizer.model.Arrival;
import com.bussscheduleoptimizer.model.Result;
import com.bussscheduleoptimizer.model.Route;
import com.bussscheduleoptimizer.model.Station;
import com.bussscheduleoptimizer.model.VehicleType;
import com.bussscheduleoptimizer.utils.FeatureUtils;
import com.bussscheduleoptimizer.utils.TFLiteUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RouteCalculator {
    private static final String TAG = "RouteCalculator";
    public static RecyclerViewAdapter adapter;

    public static void getEstimation(Station station, String stationId, View view, DirectionsCalculator directionsCalculator) {

        getEstimation(station.getBusses(), view, Integer.parseInt(stationId), directionsCalculator);
    }

    private static void getEstimation(final List<Integer> busses, final View view, final int stationId, DirectionsCalculator directionsCalculator) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("route").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    ArrayList<Result> results = new ArrayList<>();
//                    adapter = initRecyclerView(view, results, directionsCalculator);
                    adapter.clear();
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        if (busses.contains(Integer.parseInt(document.getId()))) {
                            Route route = document.toObject(Route.class);
                            if (route != null) {
                                List<Integer> routeToStation = getRouteToStation(stationId, route);
                                List<Integer> completeRoute = getCompleteRoute(stationId, route);
                                String inferenceResult = "";
                                //getLastSeen only if live data and there are multiple stations ( > 1 || > 2)
                                if (routeToStation.size() == 1) {
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(new Date());
                                    float hour = FeatureUtils.getHour(calendar);
                                    float minute = FeatureUtils.getMinute(calendar);
                                    int startingTime = TFLiteUtils.getClosestTime(getSchedule(stationId, route), (int) (hour * 100 + minute), 0);
                                    inferenceResult =  (startingTime / 100) + ":" + (startingTime % 100) + ":00";
                                    Result res = new Result(document.getId(), VehicleType.getById(route.getVehicleTypeId()), routeToStation, inferenceResult, completeRoute, stationId);
                                    adapter.addResult(res);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    if (!TFLiteUtils.useTestData) {
                                        getLastSeenTime(routeToStation, document.getId(), route.getVehicleTypeId(), getSchedule(stationId, route), inferenceResult, completeRoute, stationId);
                                    } else {
                                        // normal flow;
                                        useNormalFlow(routeToStation, document.getId(), route.getVehicleTypeId(), getSchedule(stationId, route), completeRoute, stationId);
                                    }
                                }


                            }
                        }
                    }


                }
            } else {
                Log.e(RouteCalculator.class.getName(), "Query failed");
            }
        });
    }

    private static void getLastSeenTime(List<Integer> routeToStation, String busId, int vehicleTypeId, List<Integer> schedule, String fromStart, List<Integer> completeRoute, int stationId) {
        // -------- get delay from beginning to current station
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        float hour = FeatureUtils.getHour(calendar);
        float minute = FeatureUtils.getMinute(calendar);

        float delay = getDelay(vehicleTypeId, hour, minute, routeToStation);
        // --------- get start time from beginning
        int startingTime = getStartingTime(schedule, hour, minute, delay);
        int startHour = startingTime / 100;
        int startMinutes = startingTime % 100;
        // -----------------------------------------
        calendar.set(Calendar.HOUR_OF_DAY, startHour);
        calendar.set(Calendar.MINUTE, startMinutes);
        Date startDate = calendar.getTime();


        // --------
        Log.i("Arrival", "For bus " + busId + " searching for date greater than: " + startDate);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference arrivalReference = db.collection("arrival");
        arrivalReference.whereGreaterThan("completeDate", startDate)
                .whereEqualTo("busId", busId).orderBy("completeDate", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null && !task.getResult().isEmpty()) {
                    Arrival result = null;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Arrival arrival = document.toObject(Arrival.class);
                        if (routeToStation.contains(arrival.getStationId()) && !routeToStation.get(routeToStation.size()-1).equals(arrival.getStationId())) {
                            result = arrival;
                            break;
                        }
                    }
                    if (result != null) {
                        List<Integer> newRouteToStation = routeToStation.subList(routeToStation.indexOf(result.getStationId()), routeToStation.size());
                        Log.i("newRoute", newRouteToStation.toString());
                        float newDelay = getDelay(vehicleTypeId, hour, minute, newRouteToStation);
                        Calendar newCalendar = Calendar.getInstance();
                        newCalendar.setTime(result.getCompleteDate());
                        int newHour = newCalendar.get(Calendar.HOUR_OF_DAY);
                        int newMinute = newCalendar.get(Calendar.MINUTE);
                        int newStartingTime = newHour * 100 + newMinute;
                        //if newStartingTime + newDelay > currentTime use normal flow (bus left already)
                        newMinute = newMinute + (Math.round(newDelay) / 60);
                        if (newMinute % 60 != newMinute) {
                            newMinute = newMinute % 60;
                            newHour++;
                        }
                        if (newHour * 100 + newMinute < ((int) (hour * 100 + minute))) {
                            // use normal flow
                            useNormalFlow(routeToStation, busId, vehicleTypeId, schedule, completeRoute, stationId);
                            return;
                        }

                        String inferenceResult = TFLiteUtils.interpret(newStartingTime, newDelay);
                        Result res = new Result(busId, VehicleType.getById(vehicleTypeId), newRouteToStation, inferenceResult, completeRoute, stationId);
                        adapter.addResult(res);
                        Log.i("Arrival", "last seen: " + result);

                        adapter.notifyDataSetChanged();
                        return;
                    } else {
                        // use normal flow
                        useNormalFlow(routeToStation, busId, vehicleTypeId, schedule, completeRoute, stationId);
                        return;
                    }
                } else {
                    // use normal flow
                    useNormalFlow(routeToStation, busId, vehicleTypeId, schedule, completeRoute, stationId);
                    return;
                }
            }
        });
    }

    private static void useNormalFlow(List<Integer> routeToStation, String busId, int vehicleTypeId, List<Integer> schedule, List<Integer> completeRoute, int stationId) {
        String inferenceResult = TFLiteUtils.interpret(vehicleTypeId, routeToStation, schedule);
        Result res = new Result(busId, VehicleType.getById(vehicleTypeId), routeToStation, inferenceResult, completeRoute, stationId);
        adapter.addResult(res);
        adapter.notifyDataSetChanged();
    }

    public static float getDelay(int vehicleTypeId, float hour, float minute, List<Integer> routeToStation) {
        float temperature = FeatureUtils.getTemperature();
        float condition = FeatureUtils.getConditions();
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(new Date());
        float dayOfWeek = FeatureUtils.getDayOfWeek(calendar2);
        float month = FeatureUtils.getMonth(calendar2);
        float vacation = FeatureUtils.getVacation(calendar2);
        float holiday = FeatureUtils.getHoliday(calendar2);
        float vehicleType = FeatureUtils.getVehicleTypeId(vehicleTypeId);
        return TFLiteUtils.getDelay(routeToStation, hour, minute, temperature, condition, dayOfWeek, month, vacation, holiday, vehicleType);
    }

    public static int getStartingTime(List<Integer> schedule, float hour, float minute, float delay) {
        int delayMinutes = Math.round(delay) / 60; // transform to minutes
        int delaySeconds = Math.round(delay) % 60; // transform to seconds
        return TFLiteUtils.getClosestTime(schedule, (int) (hour * 100 + minute), delayMinutes);
    }



    private static List<Integer> getRouteToStation(int stationId, Route route) {
        int index;
        List<Integer> stations = new ArrayList<>();
        if ((index = route.getRoute1().indexOf(stationId)) != -1) {
            stations = route.getRoute1().subList(0, index + 1);
        } else if ((index = route.getRoute2().indexOf(stationId)) != -1) {
            stations = route.getRoute2().subList(0, index + 1);
        }
        return stations;
    }

    private static List<Integer> getSchedule(int stationId, Route route) {
        if (route.getRoute1().contains(stationId)) {
            return route.getSchedule1();
        } else if (route.getRoute2().contains(stationId)) {
            return route.getSchedule2();
        }
        return new ArrayList<>(Collections.singletonList(600)); // default 6
    }

    private static List<Integer> getCompleteRoute(int stationId, Route route) {
        if (route.getRoute1().contains(stationId)) {
            return route.getRoute1();
        } else {
            return route.getRoute2();
        }
    }
}
