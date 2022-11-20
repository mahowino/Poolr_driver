package com.example.poolrdriver.Helpers;

import static com.example.poolrdriver.Firebase.Constants.FirebaseCollections.*;
import static com.example.poolrdriver.Firebase.Constants.FirebaseInitVariables.db;
import static com.example.poolrdriver.Firebase.FirebaseRepository.*;
import static com.example.poolrdriver.util.AppSystem.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.icu.lang.UCharacter;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.Constants.FirebaseConstants;
import com.example.poolrdriver.Firebase.Constants.FirebaseFields;
import com.example.poolrdriver.Firebase.Constants.FirebasePaths;
import com.example.poolrdriver.Firebase.FirebaseRepository;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.Interfaces.FirebaseDocumentCount;
import com.example.poolrdriver.Interfaces.PassengerRetriever;
import com.example.poolrdriver.classes.Passenger;
import com.example.poolrdriver.models.Network;
import com.example.poolrdriver.userRegistrationJourney.LogInScreen;
import com.example.poolrdriver.util.AppSystem;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworksHelper {

    public static void createNetworkInDatabase(Network network, Callback callback){
        setDocument(createNetworkPath(network), NETWORK_REFERENCE, new Callback() {
            @Override
            public void onSuccess(Object object) {
                //generate code;
                addNetworkMember(network,callback);

            }

            @Override
            public void onError(Object object) {
                callback.onError(object);
            }
        });

    }
    private static Network setNetworkInformationFromSnapshot(DocumentSnapshot snapshot){
        Network network=new Network();
        network.setNetworkName(String.valueOf(snapshot.get(FirebaseFields.NETWORK_NAME)));
        network.setNetworkUID(snapshot.getId());
        network.setNetworkTravelAdminUID(String.valueOf(snapshot.get(FirebaseFields.NETWORK_TRAVEL_ADMIN)));
        network.setNetworkCode(String.valueOf(snapshot.get(FirebaseFields.NETWORK_CODE)));
        network.setNetworkAcceptOnCode(snapshot.getBoolean(FirebaseFields.NETWORK_IS_ACCEPT_ON_CODE));
        network.setHomeLocation(AppSystem.convertGeopointToLatLong(snapshot.getGeoPoint(FirebaseFields.NETWORK_HOME_LOCATION)));
        network.setWorkLocation(AppSystem.convertGeopointToLatLong(snapshot.getGeoPoint(FirebaseFields.NETWORK_WORK_LOCATION)));

        return network;
    }

    public static void getNumberOfPostedTripsInNetwork(Network network,FirebaseDocumentCount count){
        String membersReference =  FirebasePaths.NetworkPath + network.getNetworkUID() + FirebasePaths.NetworkTrips;
        AggregateQuery countQuery = createCollectionReference(membersReference).count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AggregateQuerySnapshot snapshot = task.getResult();
               count.onCount(snapshot.getCount());
            } else {
                count.onError("Count failed: "+ task.getException());
            }
        });
    }

    public static void getNumberOfMembersInNetwork(Network network,FirebaseDocumentCount count){
        String membersReference = FirebasePaths.NetworkPath + network.getNetworkUID() + FirebasePaths.NetworkMembers;
        AggregateQuery countQuery = createCollectionReference(membersReference).count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AggregateQuerySnapshot snapshot = task.getResult();
                count.onCount(snapshot.getCount());
            } else {
                count.onError("Count failed: "+ task.getException());
            }
        });

    }

    public static void searchNetworkByCode(String networkCode,Callback callback){

       //QUERY
        Query query=createQuery(NETWORK_REFERENCE,FirebaseFields.NETWORK_CODE,networkCode);

        getDocumentsFromQueryInCollection(query, new Callback() {
            @Override
            public void onSuccess(Object object) {
                Task<QuerySnapshot> task=(Task<QuerySnapshot>)object;
                for (DocumentSnapshot snapshot:task.getResult()){

                    if (snapshot.exists()){
                        //network Exists
                        callback.onSuccess(snapshot);
                    }
                    else callback.onError(null);
                }

            }

            @Override
            public void onError(Object object) {
                callback.onError(object);
            }
        });
    }


    public static void joinNetworkUsingCode(String code,Callback callback){
        searchNetworkByCode(code, new Callback() {
            @Override
            public void onSuccess(Object object) {
                DocumentSnapshot snapshot=(DocumentSnapshot)object;
                Network network=setNetworkInformationFromSnapshot(snapshot);
                String networkId=snapshot.getId();
                if (network.isNetworkAcceptOnCode())
                addNetworkMember(network,callback);
                else addNetworkRequest(networkId,callback);
            }

            @Override
            public void onError(Object object) {
                callback.onError(object);
            }

        });
    }

    private static void addNetworkRequest(String networkId, Callback callback) {
        String membersReference = FirebasePaths.NetworkPath +  networkId+ FirebasePaths.NetworkRequests;

        setDocument(createMemberMap(), createCollectionReference(membersReference), new Callback() {
            @Override
            public void onSuccess(Object object) {
                callback.onSuccess("Network code verified");
            }

            @Override
            public void onError(Object object) {

                callback.onError(object);
            }
        });
    }

    public static void getNetworkMembers(Network network, PassengerRetriever retriever){
        String membersReference = FirebasePaths.NetworkPath + network.getNetworkUID() + FirebasePaths.NetworkMembers+"/";
        getDocumentsInCollection(createCollectionReference(membersReference),new Callback() {
            @Override
            public void onSuccess(Object object) {

                Task<QuerySnapshot> task=(Task<QuerySnapshot>)object;

                List<Passenger> NetworkMemberList=new ArrayList<>();
                for (DocumentSnapshot snapshot:task.getResult())
                    NetworkMemberList.add(new Passenger(snapshot));

                retriever.onSuccess(NetworkMemberList);

            }

            @Override
            public void onError(Object object) {

            }
        });

    }


    public static void getNetworkRequests(Network network, PassengerRetriever retriever){
        String membersReference =  FirebasePaths.NetworkPath + network.getNetworkUID() + FirebasePaths.NetworkRequests;
        getDocument(createDocumentReference(membersReference),new Callback() {
            @Override
            public void onSuccess(Object object) {

                Task<QuerySnapshot> task=(Task<QuerySnapshot>)object;

                List<Passenger> NetworkRequestList=new ArrayList<>();
                for (DocumentSnapshot snapshot:task.getResult())
                    NetworkRequestList.add(new Passenger(snapshot));

                retriever.onSuccess(NetworkRequestList);

            }

            @Override
            public void onError(Object object) {

            }
        });


    }

    private static void addNetworkMember(Network network,Callback callback) {
        String membersReference = FirebasePaths.NetworkPath + network.getNetworkUID() + FirebasePaths.NetworkMembers+"/"+new User().getUID();
        String passengerPath= FirebaseConstants.PASSENGERS+"/"+new User().getUID()+"/"+FirebaseConstants.NETWORKS+"/"+network.getNetworkUID();

        setDocument(createMemberMap(network), createDocumentReference(membersReference), new Callback() {
            @Override
            public void onSuccess(Object object) {
                addNetworkToUserProfile(network,passengerPath,callback);
            }

            @Override
            public void onError(Object object) {

                callback.onError(object);
            }
        });

    }
    public static void getNetworkInformationFromID(String networkId,Callback callback){

        getDocument(NETWORK_REFERENCE.document(networkId), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Task<DocumentSnapshot> task=(Task<DocumentSnapshot>)object;
                DocumentSnapshot snapshot=task.getResult();

                    if (snapshot.exists()){
                        Network network=new Network();
                        network.setNetworkName(String.valueOf(snapshot.get(FirebaseFields.NETWORK_NAME)));
                        network.setNetworkUID(networkId);
                        network.setNetworkTravelAdminUID(String.valueOf(snapshot.get(FirebaseFields.NETWORK_TRAVEL_ADMIN)));
                        network.setNetworkCode(String.valueOf(snapshot.get(FirebaseFields.NETWORK_CODE)));
                        network.setNetworkAcceptOnCode(snapshot.getBoolean(FirebaseFields.NETWORK_IS_ACCEPT_ON_CODE));
                        network.setHomeLocation(AppSystem.convertGeopointToLatLong(snapshot.getGeoPoint(FirebaseFields.NETWORK_HOME_LOCATION)));
                        network.setWorkLocation(AppSystem.convertGeopointToLatLong(snapshot.getGeoPoint(FirebaseFields.NETWORK_WORK_LOCATION)));

                        callback.onSuccess(network);
                    }
                    else callback.onError(null);
                }

            @Override
            public void onError(Object object) {
                callback.onError(object);
            }
        });

    }

    private static void addNetworkToUserProfile(Network network,String passengerPath, Callback callback) {
        setDocument(createNetworkPath(network), createDocumentReference(passengerPath), new Callback() {
            @Override
            public void onSuccess(Object object) {
                callback.onSuccess("Network code verified");
            }

            @Override
            public void onError(Object object) {

                callback.onError(object);
            }
        });
    }


    private static Map createMemberMap(Network network) {
        User user=new User();
        Map<String,Object> map=new HashMap<>();
        map.put(FirebaseFields.NETWORK_MEMBER_ID,user.getUID());
        map.put(FirebaseFields.FULL_NAMES,user.getName());
        map.put(FirebaseFields.NETWORK_TRAVEL_ADMIN,network.getNetworkTravelAdminUID());
    return map;
    }

    private static Map createMemberMap() {
        User user=new User();
        Map<String,Object> map=new HashMap<>();
        map.put(FirebaseFields.NETWORK_MEMBER_ID,user.getUID());
        map.put(FirebaseFields.FULL_NAMES,user.getName());
        return map;
    }
    private static Map createNetworkPath(Network network) {
        Map<String,Object> map=new HashMap<>();
        map.put(FirebaseFields.NETWORK_NAME,network.getNetworkName());
        map.put(FirebaseFields. NETWORK_TRAVEL_ADMIN,network.getNetworkTravelAdminUID());
        map.put(FirebaseFields.NETWORK_IS_ACCEPT_ON_CODE,network.isNetworkAcceptOnCode());
        map.put(FirebaseFields.NETWORK_CODE,network.getNetworkCode());
        map.put(FirebaseFields.NETWORK_HOME_LOCATION, convertLatLongToGeopoint(network.getHomeLocation()));
        map.put(FirebaseFields.NETWORK_WORK_LOCATION,convertLatLongToGeopoint(network.getWorkLocation()));
        return map;

    }

    public static void getTravelAdminDetails(String networkTravelAdminUID,Callback callback) {
        getDocument(PASSENGER_REFERENCE.document(networkTravelAdminUID), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Task<DocumentSnapshot> task=(Task<DocumentSnapshot>)object;
                DocumentSnapshot snapshot=task.getResult();

                if (snapshot.exists()){
                    Passenger passenger=new Passenger(snapshot);
                    callback.onSuccess(passenger);
                }
                else callback.onError(null);
            }

            @Override
            public void onError(Object object) {
                callback.onError(object);
            }
        });

    }

    public static void suspendUser(Passenger passenger, Context mContext) {
        new AlertDialog.Builder(mContext)
                .setTitle("Suspend user")
                .setMessage("Are you sure you want to suspend this user for 30 days?")
                .setPositiveButton(android.R.string.yes, (dialog1, which) -> {

                })
                .setNegativeButton(android.R.string.no, (dialog12, which) -> dialog12.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    public static void removeUserFromNetwork(Passenger passenger, Network network, Activity activity, Callback callback) {
        new AlertDialog.Builder(activity)
                .setTitle("Remove user")
                .setMessage("Are you sure you want to remove this user from the network?")
                .setPositiveButton(android.R.string.yes, (dialog1, which) -> {
                    deletePassengerFromNetwork(passenger,network,callback);
                })
                .setNegativeButton(android.R.string.no, (dialog12, which) -> dialog12.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private static void deletePassengerFromNetwork(Passenger passenger,Network network,Callback callback) {
        String passengerPath=FirebasePaths.PassengerPath+passenger.getUsername()+"/"+FirebaseConstants.NETWORKSU+"/"+network.getNetworkUID();
        String networkPath=FirebasePaths.NetworkPath+network.getNetworkUID()+"/"+FirebaseConstants.NETWORKSU+"/"+passenger.getUsername();
        FirebaseRepository.deleteDocument(createDocumentReference(passengerPath), new Callback() {
            @Override
            public void onSuccess(Object object) {

                FirebaseRepository.deleteDocument(createDocumentReference(networkPath), new Callback() {
                            @Override
                            public void onSuccess(Object object) {
                                callback.onSuccess(object);
                            }

                            @Override
                            public void onError(Object object) {
                                callback.onError(object);
                            }
                        });

            }

            @Override
            public void onError(Object object) {

                callback.onError(object);
            }
        });
    }
}
